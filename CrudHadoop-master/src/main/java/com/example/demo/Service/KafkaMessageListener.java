package com.example.demo.Service;



import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageListener {

    @KafkaListener(topics = "expenses", groupId = "group_id")
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}
