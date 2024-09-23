package com.saytech.expentrack.expenseservice.service;

import com.saytech.expentrack.expenseservice.dto.ExpenseDTO;
import com.saytech.expentrack.expenseservice.event.ExpenseEvent;
import com.saytech.expentrack.expenseservice.exception.ResourceNotFoundException;
import com.saytech.expentrack.expenseservice.model.Expense;
import com.saytech.expentrack.expenseservice.repository.ExpenseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ExpenseEventPublisher eventPublisher;

    // Get all expenses for a specific user
    public List<ExpenseDTO> getAllExpensesForUser(Long userId) {
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        if (expenses.isEmpty()) {
            throw new ResourceNotFoundException("Expenses for user", userId);
        }
        return expenses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Create a new expense
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        Expense expense = convertToEntity(expenseDTO);
        Expense createdExpense = expenseRepository.save(expense);
        // Publish an event after saving the expense
        ExpenseEvent event = new ExpenseEvent(createdExpense.getUserId(), createdExpense.getAmount(), createdExpense.getCategory());
        eventPublisher.publishExpenseEvent(event);
        return convertToDto(createdExpense);
    }

    // Get an expense by ID
    public ExpenseDTO getExpenseById(Long id) {
        Expense expense = findExpenseById(id);
        return convertToDto(expense);
    }

    // Update an existing expense
    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        findExpenseById(id); // Throws exception if not found
        Expense updatedExpense = convertToEntity(expenseDTO);
        updatedExpense.setId(id); // Ensure the ID is preserved
        return convertToDto(expenseRepository.save(updatedExpense));
    }

    // Delete an expense
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense", id); // Handle non-existing expense
        }
        expenseRepository.deleteById(id);
    }

    // Helper method to find an expense by ID
    private Expense findExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
    }

    // Convert Expense to ExpenseDTO
    private ExpenseDTO convertToDto(Expense expense) {
        return modelMapper.map(expense, ExpenseDTO.class);
    }

    // Convert ExpenseDTO to Expense
    private Expense convertToEntity(ExpenseDTO dto) {
        return modelMapper.map(dto, Expense.class);
    }
}
