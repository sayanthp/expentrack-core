package com.saytech.expentrack.expenseservice.service;

import com.saytech.expentrack.expenseservice.dto.BudgetDTO;
import com.saytech.expentrack.expenseservice.exception.ResourceNotFoundException;
import com.saytech.expentrack.expenseservice.model.Budget;
import com.saytech.expentrack.expenseservice.repository.BudgetRepository;
import com.saytech.expentrack.expenseservice.utility.GenericConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private GenericConverter genericConverter;


    public BudgetDTO getBudget(Long id) {
        Budget budget = findBudgetById(id);
        return genericConverter.convertToDto(budget, BudgetDTO.class);
    }

    public BudgetDTO getBudgetByUserIdAndCategory(Long userId, String category) {
        Budget budget = budgetRepository.findByUserIdAndCategory(userId,category)
                .orElseThrow(() -> new ResourceNotFoundException("Budget"));
        return genericConverter.convertToDto(budget, BudgetDTO.class);
    }

    public BudgetDTO createBudget(BudgetDTO budgetDTO) {
        Budget budget = genericConverter.convertToEntity(budgetDTO, Budget.class);
        Budget createdBudget = budgetRepository.save(budget);
        return genericConverter.convertToDto(createdBudget, BudgetDTO.class);
    }

    public BudgetDTO updateBudget(Long id, BudgetDTO budgetDTO) {
        findBudgetById(id);
        Budget updatedBudget = genericConverter.convertToEntity(budgetDTO, Budget.class);
        updatedBudget.setId(id);
        return genericConverter.convertToDto(budgetRepository.save(updatedBudget), BudgetDTO.class);
    }

    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Budget", id);
        }
        budgetRepository.deleteById(id);
    }

    private Budget findBudgetById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget",id));
    }


}
