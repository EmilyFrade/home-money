package com.homemoney.services.budget;

import com.homemoney.model.budget.Budget;
import com.homemoney.model.budget.Budget.Status;
import com.homemoney.repositories.budget.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
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

    public Map<String, Double> calculateBudgetByCategory() {
        return findAll().stream()
            .filter(budget -> budget.getStatus() == Status.Ativo)
            .collect(Collectors.groupingBy(
                budget -> budget.getCategory().toString(),
                Collectors.summingDouble(budget -> budget.getValue().doubleValue())
            ));
    }

    public double calculateTotalBudgetByCurrentMonth() {
        return budgetRepository.findAll().stream()
                .filter(budget -> budget.getStatus() == Status.Ativo)
                .mapToDouble(budget -> budget.getValue().doubleValue())
                .sum();
    }

    public double calculateAvailableBudget(double totalExpensesMonth) {
        return calculateTotalBudgetByCurrentMonth() - totalExpensesMonth;
    }

    public Map<String, Double> calculateBudgetByCategoryCurrentMonth() {
        LocalDate now = LocalDate.now();
        return budgetRepository.findAll().stream()
                .filter(budget -> budget.getStatus() == Status.Ativo)
                .filter(budget -> budget.getStartDate() != null && budget.getStartDate().getMonth() == now.getMonth() && budget.getStartDate().getYear() == now.getYear())
                .collect(Collectors.groupingBy(
                    budget -> budget.getCategory().toString(),
                    Collectors.summingDouble(budget -> budget.getValue().doubleValue())
                ));
    }

    public Map<String, Map<Month, Double>> calculateMonthlyBudgetByCategory() {
        return budgetRepository.findAll().stream()
                .filter(budget -> budget.getStatus() == Status.Ativo)
                .filter(budget -> budget.getStartDate() != null)
                .collect(Collectors.groupingBy(
                    budget -> budget.getCategory().toString(),
                    Collectors.groupingBy(
                        budget -> budget.getStartDate().getMonth(),
                        Collectors.summingDouble(budget -> budget.getValue().doubleValue())
                    )
                ));
    }

    public Map<Month, Double> calculateTotalBudgetByMonth() { 
        return budgetRepository.findAll().stream()
                .filter(budget -> budget.getStartDate() != null) //USA DATA DE INICIO PARA DETERMINAR MES
                .collect(Collectors.groupingBy(
                    budget -> budget.getStartDate().getMonth(),
                    Collectors.summingDouble(budget -> budget.getValue().doubleValue())
                ));
    }
}