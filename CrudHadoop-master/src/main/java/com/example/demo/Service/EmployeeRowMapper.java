package com.example.demo.Service;

import com.example.demo.Model.Employee;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeRowMapper implements RowMapper<Employee> {

    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        Employee employee = new Employee();

        employee.setEmpno(rs.getLong("empno"));
        employee.setEname(rs.getString("ename"));
        employee.setJob(rs.getString("job"));
        employee.setMgr(rs.getLong("mgr"));
        employee.setHiredate(rs.getDate("hiredate"));
        employee.setSal(rs.getDouble("sal"));
        employee.setComm(rs.getDouble("comm"));
        employee.setExpense(rs.getDouble("expense"));
        employee.setUserId(rs.getObject("user_id", Long.class)); // `user_id` alanını Long olarak alın

        return employee;
    }
}
