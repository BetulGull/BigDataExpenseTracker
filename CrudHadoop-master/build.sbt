name := "demo1"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.12.18"  // Spark 3.x ile uyumlu Scala sürümü

libraryDependencies ++= Seq(
  "org.springframework.boot" % "spring-boot-starter-data-jpa" % "2.7.12",
  "org.hibernate" % "hibernate-core" % "5.6.10.Final",
  // Spark Core ve SQL
  "org.apache.spark" %% "spark-core" % "3.4.4",
  "org.apache.spark" %% "spark-sql" % "3.4.4",

  // Spark Streaming ve Kafka Entegrasyonu
  "org.apache.spark" %% "spark-streaming" % "3.4.4",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.4.4",

  // Cassandra Spark Connector
  "com.datastax.spark" %% "spark-cassandra-connector" % "3.0.1", // Uyumluluğu kontrol edin

  // MySQL ve diğer veritabanı bağlantıları
  "mysql" % "mysql-connector-java" % "8.0.28",
  "com.datastax.oss" % "java-driver-core" % "4.14.1",

  // Spring Boot ve Kafka
  "org.springframework.boot" % "spring-boot-starter-web" % "2.7.12",
  "org.springframework.kafka" % "spring-kafka" % "2.8.1"
)
