package com.saytech.expentrack.expenseservice.repository;

import com.saytech.expentrack.expenseservice.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);
}
