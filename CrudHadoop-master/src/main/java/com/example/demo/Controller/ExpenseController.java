package com.example.demo.Controller;

import com.example.demo.DTO.ExpenseDTO;
import com.example.demo.Service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    /**
     * Gerçek zamanlı harcama verilerini döndürür.
     *
     * @return List<ExpenseDTO>
     */
    @GetMapping("/data")
    public List<ExpenseDTO> getRealTimeExpenses() {
        return expenseService.getAggregatedExpenses();
    }
}
