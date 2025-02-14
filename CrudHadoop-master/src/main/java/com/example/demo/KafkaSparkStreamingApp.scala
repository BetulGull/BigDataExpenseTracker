package com.example.demo

import com.example.demo.Service.EmployeeService
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010._
import org.apache.spark.sql.functions._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class KafkaSparkStreamingApp {

  @Autowired
  private var employeeService: EmployeeService = _

  def startStreaming(): Unit = {
    val spark = SparkSession.builder()
      .appName("KafkaSparkCassandraPipeline")
      .master("local[*]")
      .config("spark.ui.enabled", "false")
      .config("spark.sql.shuffle.partitions", "200")
      .config("spark.cassandra.connection.host", "127.0.0.1")
      .getOrCreate()

    val ssc = new StreamingContext(spark.sparkContext, Seconds(10))

    val kafkaParams = Map(
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "your_custom_group_id",
      "auto.offset.reset" -> "latest"
    )

    val topics = Array("expenses")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
    )

    val jdbcUrl = "jdbc:mysql://localhost:3306/bigdata3" // MySQL bağlantı URL'si
    val jdbcUser = "hay" // MySQL kullanıcı adı
    val jdbcPassword = "hay13." // MySQL şifresi

    stream.foreachRDD { rdd =>
      if (!rdd.isEmpty) {
        val messages = rdd.map(record => record.value)
        println(s"Received ${messages.count()} messages")

        import spark.implicits._
        val messageDF = messages.toDS()

        val parsedDF = try {
          spark.read.json(messageDF)
        } catch {
          case e: Exception =>
            println(s"Error while parsing JSON: ${e.getMessage}")
            null
        }

        if (parsedDF != null) {
          val finalDF = parsedDF.select(
            col("id"),
            unix_timestamp(col("date_time"), "yyyy-MM-dd'T'HH:mm:ss.SSSSSS").cast("timestamp").as("date_time"),
            col("description"),
            col("payment"),
            col("user_id").cast("long")
          )

          // Cassandra'ya yazma
          finalDF.write
            .format("org.apache.spark.sql.cassandra")
            .option("keyspace", "demo")
            .option("table", "expenses")
            .mode("append")
            .save()

          println("Data written to Cassandra")

          // MySQL için güncelleme işlemi
          val employeeExpenseDF = finalDF
            .groupBy("user_id")
            .agg(sum("payment").as("total_expense"))

          employeeExpenseDF.collect().foreach { row =>
            val userId = row.getAs[Long]("user_id")
            val totalExpense = row.getAs[Double]("total_expense")

            val updateQuery = s"""
          UPDATE employee
          SET expense = $totalExpense
          WHERE user_id = $userId
        """

            val connection = java.sql.DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword)
            val statement = connection.createStatement()
            try {
              statement.executeUpdate(updateQuery)
              println(s"Updated expense for userId: $userId with totalExpense: $totalExpense")
            } finally {
              statement.close()
              connection.close()
            }
          }
        }
      }
    }


    ssc.start()

    try {
      ssc.awaitTermination()
    } catch {
      case e: InterruptedException =>
        println("StreamingContext interrupted: " + e.getMessage)
    } finally {
      println("Stopping StreamingContext")
      ssc.stop(stopSparkContext = true, stopGracefully = true)
    }

  }
}
