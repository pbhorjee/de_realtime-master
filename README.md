# get all routes from ttc
http://restbus.info/api/agencies/ttc/routes
# API listed
http://restbus.info/

# view on the map
https://retro.umoiq.com/googleMap/?a=ttc&r=7&d=7_1_7&s=14141

# getting vicheal inforation
http://restbus.info/api/agencies/ttc/routes/7/vehicles

# docker images for nifi:
https://hub.docker.com/r/apache/nifi
https://github.com/apiri/dockerfile-apache-nifi

reference
# awesome nifi
https://github.com/jfrazee/awesome-nifi

# template
https://github.com/hortonworks-gallery/nifi-templates/tree/master/templates


# ui
https://github.com/G2Jose/livetransit
# open API
https://opendata.tpl.ca/



# JDBC:mysql://mysql:3306/demo
# driver com.mysql.jdbc.Driver
# mkdir /opt/nifi/nifi-current/custom-jar
# cd /opt/nifi/nifi-current/custom-jar
# wget http://www.java2s.com/Code/JarDownload/mysql/mysql-connector-java-5.1.17-bin.jar.zip
# unzip mysql-connector-java-5.1.17-bin.jar.zip 


# setup nifi
https://www.youtube.com/watch?v=8KxcAiNdqvw&ab_channel=StevenKoon


INSERT INTO bus_status (id, routeId, directionId, predictable, secsSinceReport, kph, heading, lat, lon, leadingVehicleId) VALUES (8122, 7, '7_1_7', 1, 5, 21, 34, 43.7899061, -79.4459783, null)

# schema
create table bus_status (
   record_id INT NOT NULL AUTO_INCREMENT,
   id INT NOT NULL,
   routeId INT NOT NULL,
   directionId VARCHAR(40),
   predictable BOOLEAN,
   secsSinceReport INT NOT NULL,
   kph INT NOT NULL,
   heading INT,
   lat DECIMAL(10, 8) NOT NULL,
   lon DECIMAL(10, 8) NOT NULL,
   leadingVehicleId INT,
   event_time DATETIME DEFAULT NOW(),
   PRIMARY KEY (record_id) 
);



# nifi controler
https://community.cloudera.com/t5/Community-Articles/Understanding-Controller-Service-Availability-in-Apache-NiFi/ta-p/244904

A NiFi Controller Service provides a shared starting point and functionality across Processors, other ControllerServices, and ReportingTasks within a single JVM. Controllers are used to provide shared resources, such as a database, ssl context, or a server connection to an external server and much more.May 17, 2018


In Apache NiFi, Controller Services are shared services that can be used by Processors, Reporting Tasks, and other Controller Services. In order to modify a Controller Service, all referencing components must be stopped. This means that the user attempting to modify the Controller Service, must also have permissions to modify all referencing components. With the introduction of multi-tenant authorization, Controller Service scoping was updated to ensure service usage does not become so broad that it becomes impossible to update the Controller Service because no users are allowed to modified all components that reference it.



# create template and export and import.
https://docs.cloudera.com/HDPDocuments/HDF3/HDF-3.0.1/bk_user-guide/content/Export_Template.html#:~:text=To%20export%20a%20Template%2C%20locate,XML%20file%20to%20your%20computer.




------------
https://www.programmersought.com/article/46374596352/
https://www.youtube.com/watch?v=8KxcAiNdqvw&ab_channel=StevenKoon




# nifi info
https://github.com/jfrazee/awesome-nifi





# https://debezium.io/documentation/reference/tutorial.html#starting-zookeeper


# setup docker
https://docs.aws.amazon.com/AmazonECS/latest/developerguide/docker-basics.html


# step1: setup mysql
```
docker run -dit --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=debezium -e MYSQL_USER=mysqluser -e MYSQL_PASSWORD=mysqlpw debezium/example-mysql:1.5

- login
docker exec -it mysql bash
mysql -u root -p debezium

- create database
create database demo;
use demo;

create table bus_status (
   record_id INT NOT NULL AUTO_INCREMENT,
   id INT NOT NULL,
   routeId INT NOT NULL,
   directionId VARCHAR(40),
   predictable BOOLEAN,
   secsSinceReport INT NOT NULL,
   kph INT NOT NULL,
   heading INT,
   lat float NOT NULL,
   lon float NOT NULL,
   leadingVehicleId INT,
   event_time DATETIME DEFAULT NOW(),
   PRIMARY KEY (record_id) 
);

```

# step2: setup nifi
```
docker run --name nifi -p 8080:8080 --link mysql:mysql  -d apache/nifi:latest
```

# step3: setup zookeeper, kafka
https://debezium.io/documentation/reference/tutorial.html#starting-zookeeper
docker run -dit --name zookeeper -p 2181:2181 -p 2888:2888 -p 3888:3888 debezium/zookeeper:1.5


docker run -dit --name kafka -p 9092:9092 --link zookeeper:zookeeper debezium/kafka:1.5

docker run -dit --name connect -p 8083:8083 -e GROUP_ID=1 -e CONFIG_STORAGE_TOPIC=my_connect_configs -e OFFSET_STORAGE_TOPIC=my_connect_offsets -e STATUS_STORAGE_TOPIC=my_connect_statuses --link zookeeper:zookeeper --link kafka:kafka --link mysql:mysql debezium/connect:1.5

# check kafka connect rest api
curl -H "Accept:application/json" localhost:8083

# check connectors
curl -H "Accept:application/json" localhost:8083/connectors/

# Deploying the MySQL connector
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d '{ "name": "inventory-connector", "config": { "connector.class": "io.debezium.connector.mysql.MySqlConnector", "tasks.max": "1", "database.hostname": "mysql", "database.port": "3306", "database.user": "debezium", "database.password": "dbz", "database.server.id": "184054", "database.server.name": "dbserver1", "database.include.list": "demo", "database.history.kafka.bootstrap.servers": "kafka:9092", "database.history.kafka.topic": "dbhistory.demo" } }'

# check data from topic
 bin/kafka-console-consumer.sh  --topic dbserver1.demo.bus_status --bootstrap-server 48831e366779:9092

# flink

https://github.com/docker-flink/examples/blob/master/docker-compose.yml
https://stackoverflow.com/questions/54853195/kafka-client-timeout-of-60000ms-expired-before-the-position-for-partition-could
https://github.com/ververica/flink-cdc-connectors

# *** https://github.com/morsapaes/flink-sql-CDC

docker run -dit --name jobmanager -p 8081:8081  --link zookeeper:zookeeper --link kafka:kafka -v /home/ec2-user:/jar -e DISABLE_JEMALLOC=true -e JOB_MANAGER_RPC_ADDRESS=jobmanager flink jobmanager
docker run -dit --name taskmanager  --link zookeeper:zookeeper --link kafka:kafka --link jobmanager:jobmanager -v /home/ec2-user:/jar -e DISABLE_JEMALLOC=true -e JOB_MANAGER_RPC_ADDRESS=jobmanager flink taskmanager


# to enable flink read s3 data
wget https://repo1.maven.org/maven2/org/apache/flink/flink-s3-fs-hadoop/1.12.1/flink-s3-fs-hadoop-1.12.1.jar
flink-scala-shell  local -a /home/hadoop/flink-s3-fs-hadoop-1.12.1.jar





start spark:
```
  
docker run -dit -p 4040:4040 -p 8079:8080 -p 8082:8081 -v /home/ec2-user:/jars -e SPARK_MASTER=spark://spark-master:7077 --link kafka:kafka --name spark-master --network spark-streaming_default bde2020/spark-master:3.1.1-hadoop3.2

docker run -dit -p 8081:8081 -v /home/ec2-user:/jars -e SPARK_MASTER=spark://spark-master:7077 --link kafka:kafka --name spark-worker --network spark-streaming_default bde2020/spark-worker:3.1.1-hadoop3.2
```



# convert string to json
https://stackoverflow.com/questions/49088401/spark-from-json-with-dynamic-schema
https://forums.databricks.com/questions/12518/spark-structured-streaming-append-mode-console-dis.html

# code
spark/bin/spark-shell --jars /jars/wcd-spark-streaming-with-debezium_2.12.8-0.1.jar

import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
case class JDBCConfig(url: String, user: String = "test", password: String = "Test123", tableName: String = "orders_it")
case class KafkaReaderConfig(kafkaBootstrapServers: String, topics: String, startingOffsets: String = "latest")
case class StreamingJobConfig(checkpointLocation: String, kafkaReaderConfig: KafkaReaderConfig)
val currentDirectory = new java.io.File(".").getCanonicalPath
val kafkaReaderConfig = KafkaReaderConfig("kafka:9092", "dbserver1.demo.bus_status")
val jdbcConfig = JDBCConfig(url = "jdbc:postgresql://localhost:5432/test")

val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaReaderConfig.kafkaBootstrapServers)
      .option("subscribe", kafkaReaderConfig.topics)
      .option("startingOffsets", kafkaReaderConfig.startingOffsets)
      .load()

val schemas = spark.read.json("file:///jars/bus_status.json").schema

val transformDF = df.select(from_json($"value".cast("string"), schemas).as("value"))

transformDF.select($"value.payload.after").writeStream.option("checkpointLocation", "/checkpoint/job").format("console").option("truncate", "false").start().awaitTermination() 


https://programming.vip/docs/actual-write-to-hudi-using-spark-streaming.html

curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d '{ "name": "inventory-connector", "config": { "connector.class": "io.debezium.connector.mysql.MySqlConnector", "tasks.max": "1", "database.hostname": "mysql", "database.port": "3306", "database.user": "debezium", "database.password": "dbz", "database.server.id": "184054", "database.server.name": "dbserver1", "database.include.list": "demo", "database.history.kafka.bootstrap.servers": "b-1.edwin-demo.k5iubr.c3.kafka.us-east-1.amazonaws.com:9092", "database.history.kafka.topic": "dbhistory.demo" } }'


curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" 192.168.120.127:8083/connectors/ -d '{ "name": "inventory-connector", "config": { "connector.class": "io.debezium.connector.mysql.MySqlConnector", "tasks.max": "1", "database.hostname": "mysql", "database.port": "3306", "database.user": "debezium", "database.password": "dbz", "database.server.id": "184054", "database.server.name": "dbserver1", "database.include.list": "demo", "database.history.kafka.bootstrap.servers": "my-confluent-cp-kafka:9092", "database.history.kafka.topic": "dbhistory.demo" } }'




# ----------------------------------------- to deploy under k8s
```
helm repo add confluentinc https://confluentinc.github.io/cp-helm-charts/
helm install confluentinc/cp-helm-charts --name my-confluent --version 0.6.0 -n prod
helm install my-confluent --version 0.6.0 -n prod  confluentinc/cp-helm-charts
kubectl apply -f kafka-client.yml
kubectl apply -f mysql-client.yml 
kubectl apply -f nifi-client.yml
kubectl apply -f kafka-connect.yml 
kubectl apply -f mysql-svc.yml  
kubectl apply -f nifi-svc.yml
```
