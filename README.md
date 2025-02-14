# BigDataExpenseTracker

This repository demonstrates an end-to-end data processing pipeline for **real-time credit card expense tracking**. The solution leverages Apache Kafka, Apache Spark, Cassandra, and Java Spring Boot to simulate, ingest, process, store, and visualize expense data in a scalable and efficient manner. Additionally, it shows how to integrate Hadoop-HDFS for handling personnel images.

## Table of Contents
1. [Features](#features)  
2. [Technologies](#technologies)  
3. [Architecture Overview](#architecture-overview)  

---

## Features

1. **Data Generation and Ingestion**  
   - Generate simulated credit card expenses using a MySQL-based data generator.  
   - Stream expense records to user-specific Kafka topics in real time.

2. **Real-Time Data Processing**  
   - Leverage an Apache Spark Streaming module to consume data from Kafka.  
   - Perform transformations, aggregations, and other data processing tasks.  
   - Write processed data to a Cassandra NoSQL database for quick lookups.

3. **Image Storage and Management**  
   - Use Hadoop-HDFS for storing and managing personnel images at scale.

4. **Dynamic Reporting with Spring Boot**  
   - Build a Java Spring Boot web application to visualize real-time reports.  
   - Provide insights into total user expenses and other personal details.

---

## Technologies

- **MySQL**  
  Used to simulate and generate credit card expense data.

- **Apache Kafka**  
  Facilitates real-time data ingestion by streaming the expense data.

- **Apache Spark**  
  Handles distributed, real-time data processing from Kafka to Cassandra.

- **Cassandra**  
  Serves as a NoSQL database for fast reads and writes of processed expense data.

- **Hadoop-HDFS**  
  Stores large volumes of data, particularly personnel images.

- **Spring Boot (Java)**  
  Provides a RESTful API and user interface for dynamic reporting.

---

## Architecture Overview

1. **Data Generator (MySQL)**  
   - Creates credit card expense data records.

2. **Kafka**  
   - Streams the generated expense data to user-specific topics in real time.

3. **Spark Streaming**  
   - Consumes data from Kafka, performs transformations/aggregations, and writes results to Cassandra.

4. **Cassandra**  
   - Stores processed expense data for real-time querying and reporting.

5. **Hadoop-HDFS**  
   - Handles storage and management of personnel images.

6. **Spring Boot Application**  
   - Provides a web interface and APIs for generating dynamic reports, such as total expenses by user.

