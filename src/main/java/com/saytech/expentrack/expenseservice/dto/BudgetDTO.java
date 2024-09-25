package com.saytech.expentrack.expenseservice.dto;

public class BudgetDTO {

    private Long id;
    private String category; // e.g., "Food", "Transportation", etc.
    private double limit; // Budget limit for the category
    private Long userId;

    public String getCategory() {
        return category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
