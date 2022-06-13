package weclouddata.streaming

import weclouddata.wrapper.SparkSessionWrapper
import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.functions._

object StreamingJob extends App with SparkSessionWrapper {

  val currentDirectory = new java.io.File(".").getCanonicalPath
  val schema = spark.read.json("file:///jars/bus_status.json").schema
  val kafkaReaderConfig =  KafkaReaderConfig("kafka:9092", "dbserver1.demo.bus_status")
  val jdbcConfig = JDBCConfig(url = "jdbc:postgresql://localhost:5432/test")

  new StreamingJobExecutor(
    spark,
    kafkaReaderConfig,
    currentDirectory + "/checkpoint/job",
    jdbcConfig,
    schema
  ).execute()
}

case class JDBCConfig(
    url: String,
    user: String = "test",
    password: String = "Test123",
    tableName: String = "test"
)

case class KafkaReaderConfig(
    kafkaBootstrapServers: String,
    topics: String,
    startingOffsets: String = "earliest"
)

case class StreamingJobConfig(
    checkpointLocation: String,
    kafkaReaderConfig: KafkaReaderConfig
)

class StreamingJobExecutor(
    spark: SparkSession,
    kafkaReaderConfig: KafkaReaderConfig,
    checkpointLocation: String,
    jdbcConfig: JDBCConfig,
    schema: StructType
) {
  import spark.implicits._

  def execute(): Unit = {
    // read data from kafka and parse them

    val transformDF =
      read().select(from_json($"value".cast("string"), schema).as("value"))

    // print to the console
    // transformDF.select($"value.payload.after.*")
    //             .writeStream.option("checkpointLocation", "/checkpoint/job")
    //             .format("console")
    //             .option("truncate", "false")
    //             .start()
    //             .awaitTermination()

    // writing to sql

    // transformDF
    //   .writeStream
    //   .option("checkpointLocation", checkpointLocation)
    //   .foreachBatch { (batchDF: DataFrame, _: Long) => {
    //     batchDF.write
    //       .format("jdbc")
    //       .option("url", jdbcConfig.url)
    //       .option("user", jdbcConfig.user)
    //       .option("password", jdbcConfig.password)
    //       .option("driver", "org.postgresql.Driver")
    //       .option(JDBCOptions.JDBC_TABLE_NAME, jdbcConfig.tableName)
    //       .option("stringtype", "unspecified")
    //       .mode(SaveMode.Append)
    //       .save()
    //   }
    //   }.start()
    //   .awaitTermination()

    // write to hudi
    transformDF
      .select($"value.payload.after.*")
      .writeStream
      .queryName("Write hudi data")
      .foreachBatch { (batchDF: DataFrame, _: Long) =>
        {
          batchDF.write
            .format("org.apache.hudi")
            .option("hoodie.datasource.write.table.type", "COPY_ON_WRITE")
            .option("hoodie.datasource.write.precombine.field", "event_time")
            .option("hoodie.datasource.write.recordkey.field", "id")
            .option("hoodie.datasource.write.partitionpath.field", "routeId")
            .option("hoodie.table.name", "bus_status")
            .option("hoodie.datasource.write.hive_style_partitioning", true)
            .option("hoodie.upsert.shuffle.parallelism", "100")
            .option("hoodie.insert.shuffle.parallelism", "100")
            .mode(SaveMode.Append)
            .save("/tmp/sparkHudi/bus_status")
        }
      }
      .option("checkpointLocation", "/tmp/sparkHudi/checkpoint/")
      .start()
      .awaitTermination()
  }

  def read(): DataFrame = {
    spark.readStream
      .format("kafka")
      .option(
        "kafka.bootstrap.servers",
        kafkaReaderConfig.kafkaBootstrapServers
      )
      .option("subscribe", kafkaReaderConfig.topics)
      .option("startingOffsets", kafkaReaderConfig.startingOffsets)
      .load()
  }
}
