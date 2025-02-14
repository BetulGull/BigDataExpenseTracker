package com.example.demo.Service;

import com.example.demo.Model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class DataGeneratorService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmployeeService employeeService;

    private static final String TOPIC = "expenses";
    private final Random random = new Random();
    private List<Employee> employees;

    private int messageCount = 0;  // Counter to stop data generation after 50 messages

    public void initializeEmployees() {
        this.employees = employeeService.getAllEmployees();
    }

    @Scheduled(fixedRate = 1000) // Generates data every 1 second
    public void generateExpense() {
        if (messageCount >= 200) {
            System.out.println("Data generation stopped after 50 messages.");
            return; // Stop generating data after 50 messages
        }

        if (employees == null || employees.isEmpty()) {
            initializeEmployees();
        }

        Employee randomEmployee = employees.get(random.nextInt(employees.size()));
        String expense = String.format(
                "{ \"id\": \"%s\", \"user_id\": %d, \"date_time\": \"%s\", \"description\": \"%s\", \"payment\": %.2f }",
                UUID.randomUUID(),
                randomEmployee.getEmpno(),
                LocalDateTime.now(),
                getRandomDescription(),
                random.nextDouble() * 1000
        );

        System.out.println("Sending message to Kafka: " + expense);  // Log the message
        kafkaTemplate.send(TOPIC, expense); // Send data to Kafka
        messageCount++; // Increment the message count
    }

    // This method checks if the data generation is complete (after 50 messages)
    public boolean isDataGenerationComplete() {
        return messageCount >= 50;
    }

    private String getRandomDescription() {
        String[] descriptions = {"Macaroni", "Food", "Clothes", "Car", "Electronics"};
        return descriptions[random.nextInt(descriptions.length)];
    }
}
