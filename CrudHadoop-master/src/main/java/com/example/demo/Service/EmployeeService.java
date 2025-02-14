package com.example.demo.Service;

import com.example.demo.Model.Employee;
import com.example.demo.Repository.DepartmentRepository;
import com.example.demo.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ImageService imageService;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, ImageService imageService) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.imageService = imageService;
    }

    // Existing method to get User IDs
    public List<Long> getUserIds() {
        String query = "SELECT empno FROM employee"; // Employee empno'larını alın
        List<Long> empnos = jdbcTemplate.queryForList(query, Long.class);

        List<Long> userIds = new ArrayList<>();
        for (Long empno : empnos) {
            String userIdQuery = "SELECT user_id FROM user_data3 WHERE empno = ?";
            Long userId = jdbcTemplate.queryForObject(userIdQuery, Long.class, empno); // user_id artık Long
            userIds.add(userId);
        }

        return userIds;
    }

    public Double getTotalExpenseFromCassandra(Long userId) {
        String query = "SELECT SUM(payment) FROM demo.expenses WHERE user_id = ?";
        Double totalExpense = jdbcTemplate.queryForObject(query, new Object[]{userId}, Double.class);
        return totalExpense != null ? totalExpense : 0.0;
    }



    // Add a method to get Employee by userId
    public Employee getEmployeeByUserId(String userId) {
        // You can adjust the query to fetch the Employee by userId if it's stored elsewhere
        String query = "SELECT * FROM employee WHERE user_id = ?";
        List<Employee> employees = jdbcTemplate.query(query, new Object[]{userId}, new EmployeeRowMapper());

        if (employees.isEmpty()) {
            return null;
        }
        return employees.get(0); // Return the first employee (assuming userId is unique)
    }

    // Other existing methods...
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long empno) {
        return employeeRepository.findById(empno).orElse(null);
    }

    public void saveEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    public void updateEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    public void deleteEmployeeById(Long id) {
        employeeRepository.deleteById(id);
    }

    public Resource loadImageFromHDFS(String imageName) throws IOException {
        return imageService.loadImageFromHDFS(imageName);
    }
}
