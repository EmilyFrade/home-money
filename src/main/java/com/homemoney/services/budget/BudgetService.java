package com.homemoney.services.budget;

import com.homemoney.model.budget.Budget;
import com.homemoney.repositories.budget.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    public List<Budget> findAll() {
        return budgetRepository.findAll();
    }

    public Budget findById(Long id) {
        return budgetRepository.findById(id).orElse(null);
    }

    public void save(Budget budget) {
        budget.setStatusBasedOnDate();
        budgetRepository.save(budget);
    }

    public void delete(Long id) {
        budgetRepository.deleteById(id);
    }

    // Novo método para calcular o orçamento por categoria
public Map<String, Double> calculateBudgetByCategory() {
    return findAll().stream()
        .collect(Collectors.groupingBy(
            budget -> budget.getCategory().toString(), // Garantir que a categoria seja uma String
            Collectors.summingDouble(budget -> budget.getValue().doubleValue())
        ));
}
}
