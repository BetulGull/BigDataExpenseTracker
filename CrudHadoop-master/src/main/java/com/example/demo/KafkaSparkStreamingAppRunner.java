package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class KafkaSparkStreamingAppRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting Kafka Spark Streaming App...");

        // Scala KafkaSparkStreamingApp sınıfının örneğini oluştur
        KafkaSparkStreamingApp kafkaSparkStreamingApp = new KafkaSparkStreamingApp();

        // startStreaming metodunu çalıştır
        Thread streamingThread = new Thread(kafkaSparkStreamingApp::startStreaming);

        streamingThread.setDaemon(true); // Uygulama kapandığında thread sonlanır
        streamingThread.start();

        System.out.println("Kafka Spark Streaming App started.");
    }
}
