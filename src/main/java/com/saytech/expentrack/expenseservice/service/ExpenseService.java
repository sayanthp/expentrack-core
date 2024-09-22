package com.saytech.expentrack.expenseservice.service;

import com.saytech.expentrack.expenseservice.dto.ExpenseDTO;
import com.saytech.expentrack.expenseservice.model.Expense;
import com.saytech.expentrack.expenseservice.repository.ExpenseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ModelMapper modelMapper;


    public List<ExpenseDTO> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get all expenses for a specific user
    public List<ExpenseDTO> getAllExpensesForUser(Long userId) {
        return expenseRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        Expense expense = convertToEntity(expenseDTO);
        Expense createdExpense = expenseRepository.save(expense);
        return convertToDto(createdExpense);
    }

    public Optional<ExpenseDTO> getExpenseById(Long id) {
        return expenseRepository.findById(id).map(expense -> modelMapper.map(expense, ExpenseDTO.class));
    }

    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        if (!expenseRepository.existsById(id)) {
            return null; // Or throw an exception
        }
        Expense expense = convertToEntity(expenseDTO);
        expense.setId(id);
        Expense updatedExpense = expenseRepository.save(expense);
        return convertToDto(updatedExpense);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
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
