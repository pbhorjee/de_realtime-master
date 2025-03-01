# A Sample End-to-End Streaming Pipeline with Automated ETL and near-real time Data Visualization

## Spark Streaming Application With Debezium CDC and Kafka, Hudi Data Lake, Glue Data Catalog, and Superset data viz

- Docker
- Docker Compose
- AWS EC2
- Apache Nifi (docker deployed on single instance EC2) for HTTP GET extraction from Toronto Public Transportation Bus Route #7 API in real time writing messages to a Mysql initial data store 
- MySQL (docker deployed on single instance EC2)
- Debezium CDC Kafka Connect Module (docker deployed on single instance EC2) for CDC from MySQL to Kafka topics
- Kafka on clustered AWS MSK
- Spark on clustered Hadoop (HDSF) AWS EMR
- Spark Structured Streaming app implemented in Scala cleaning and transforming input streams from native Kafka source and writing to S3 sink
- Hudi data lake on S3 in Parquet columnar file format
- Glue Data Catalog documenting the data lake
- Athena for ad hoc queries and monitoring of MySQL DB and the Hudi data lake via the Glue Data Catalog
- Superset data visualization of live geolocation data of each bus on route #7 in near real time via a MapBox plugin


![](https://docs.google.com/drawings/d/e/2PACX-1vR2yxJob0s1HsqiYCgRlnc82GDz3zw8Gj9EURkijLivvnQeIoENGLQEYBY3mXz5RnqSo29BOKgP2Hp0/pub?w=960&h=720)
