package com.saytech.expentrack.expenseservice.service;

import com.saytech.expentrack.expenseservice.dto.BudgetDTO;
import com.saytech.expentrack.expenseservice.dto.ExpenseDTO;
import com.saytech.expentrack.expenseservice.event.ExpenseEvent;
import com.saytech.expentrack.expenseservice.exception.ResourceNotFoundException;
import com.saytech.expentrack.expenseservice.model.Expense;
import com.saytech.expentrack.expenseservice.repository.BudgetRepository;
import com.saytech.expentrack.expenseservice.repository.ExpenseRepository;
import com.saytech.expentrack.expenseservice.utility.GenericConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ExpenseService {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ExpenseEventPublisher eventPublisher;

    @Autowired
    private GenericConverter genericConverter;

    @Autowired
    private BudgetService budgetService;

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
        Expense expense = genericConverter.convertToEntity(expenseDTO,Expense.class);
        Expense createdExpense = expenseRepository.save(expense);
        // Publish an event after saving the expense
        ExpenseEvent event = createExpenseEvent(expenseDTO);
        eventPublisher.publishExpenseEvent(event);
        return genericConverter.convertToDto(createdExpense,ExpenseDTO.class);
    }

    // Get an expense by ID
    public ExpenseDTO getExpenseById(Long id) {
        Expense expense = findExpenseById(id);
        return genericConverter.convertToDto(expense,ExpenseDTO.class);
    }

    // Update an existing expense
    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        findExpenseById(id); // Throws exception if not found
        Expense updatedExpense = genericConverter.convertToEntity(expenseDTO,Expense.class);
        updatedExpense.setId(id); // Ensure the ID is preserved
        return genericConverter.convertToDto(expenseRepository.save(updatedExpense),ExpenseDTO.class);
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


    private ExpenseEvent createExpenseEvent(ExpenseDTO expenseDTO) {
        Long userId = expenseDTO.getUserId();
        String category = expenseDTO.getCategory();
        Double expenseAmount = expenseDTO.getAmount();
        BudgetDTO budgetDTO = budgetService.getBudgetByUserIdAndCategory(userId, category);

        if (budgetDTO != null) {
            double currentLimit = budgetDTO.getLimit();
            double currentExpense = getCurrentTotalExpenses(userId, category);
            if (currentExpense + expenseAmount > currentLimit) {
                // Create a BudgetExceeded event
                return new ExpenseEvent(userId, expenseAmount, category, "BudgetExceeded");
            }
        }
        // If no budget or not exceeding, create a regular ExpenseEvent
        return new ExpenseEvent(userId, expenseAmount, category, "ExpenseEvent");
    }

    private double getCurrentTotalExpenses(Long userId, String category) {
        List<Expense> expenses = expenseRepository.findByUserIdAndCategory(userId, category);
        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }
}
