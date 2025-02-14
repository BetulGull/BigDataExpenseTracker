package com.example.demo.Service;



import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CassandraService {

    public List<Map<String, Object>> getExpenses() {
        // Cassandra'dan veri çekmek için Spark veya başka bir Cassandra bağlantı kütüphanesini kullanabilirsiniz
        // Örnek veri döndürme
        return List.of(
                Map.of("user_id", 1, "payment", 200.50, "description", "Food"),
                Map.of("user_id", 2, "payment", 450.00, "description", "Electronics")
        );
    }
}
