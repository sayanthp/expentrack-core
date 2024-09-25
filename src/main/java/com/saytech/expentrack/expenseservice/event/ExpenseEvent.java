package com.saytech.expentrack.expenseservice.event;

public class ExpenseEvent {

    private Long userId;
    private Double amount;
    private String category;

    private String type;

    public ExpenseEvent(Long userId, Double amount, String category, String type) {
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
