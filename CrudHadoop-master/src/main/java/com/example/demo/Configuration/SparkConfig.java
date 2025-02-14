package com.example.demo.Configuration;

import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Bean
    public SparkSession sparkSession() {
        return SparkSession.builder()
                .appName("KafkaSparkCassandraPipeline")
                .master("local[*]") // Yerel olarak çalışacak şekilde ayarla
                .config("spark.ui.enabled", "false")
                .config("spark.sql.shuffle.partitions", "200")
                .config("spark.cassandra.connection.host", "127.0.0.1") // Cassandra bağlantısı
                .config("spark.cassandra.connection.port", "9042")  // Varsayılan port
                .getOrCreate();
    }
}
