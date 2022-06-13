name := "Kafka_Flink_Connector"

version := "0.1"

organization := "wcd"

scalaVersion in ThisBuild := "2.12.7"

val flinkVersion = "1.13.1"

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion,
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion,
  "org.apache.flink" %% "flink-connector-kafka" % flinkVersion)

lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= flinkDependencies
  )

mainClass in assembly := Some("wcd.BusData")


// exclude Scala library from assembly
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

assemblyJarName in assembly := s"${name.value}_${scalaBinaryVersion.value}-${version.value}.jar"
