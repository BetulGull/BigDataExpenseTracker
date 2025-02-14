package com.example.demo.DTO;



public class ExpenseDTO {
    private Long userId;
    private Double totalExpense;

    // Parametresiz constructor (Gerekli olabilir)
    public ExpenseDTO() {
    }

    // Parametreli constructor
    public ExpenseDTO(Long userId, Double totalExpense) {
        this.userId = userId;
        this.totalExpense = totalExpense;
    }

    // Getter ve Setter metodları
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Double totalExpense) {
        this.totalExpense = totalExpense;
    }

    @Override
    public String toString() {
        return "ExpenseDTO{" +
                "userId=" + userId +
                ", totalExpense=" + totalExpense +
                '}';
    }
}

