package wcd

import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.api.common.serialization.SimpleStringSchema
import java.util.Properties
/**
  *
  *
  */
object BusData {

  def main(args: Array[String]) {

    // set up streaming execution environment
    val env = StreamExecutionEnvironment.getExecutionEnvironment()

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "kafka:9092")
    properties.setProperty("group.id", "wcd_flink");

    val flinkKafkaConsumer = new FlinkKafkaConsumer("dbserver1.demo.bus_status", new SimpleStringSchema(), properties)
    flinkKafkaConsumer.setStartFromLatest()

    val dataStream = env.addSource(flinkKafkaConsumer)
    val result = dataStream.map((s: String) => s"This is a string: $s")

    result.print
    env.execute("print_data")
    
  }

}
