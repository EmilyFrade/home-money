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

    private List<Budget> getActiveBudgets() {
        return budgetRepository.findAll().stream()
                .filter(budget -> budget.getStatus() == Status.Ativo)
                .collect(Collectors.toList());
    }

    private boolean isInCurrentMonth(LocalDate date) {
        LocalDate now = LocalDate.now();
        return date != null && date.getMonth() == now.getMonth() && date.getYear() == now.getYear();
    }

    private Map<String, Double> groupBudgetsByCategory(List<Budget> budgets) {
        return budgets.stream()
                .collect(Collectors.groupingBy(
                        budget -> budget.getCategory().toString(),
                        Collectors.summingDouble(budget -> budget.getValue().doubleValue())
                ));
    }

    public Map<String, Double> calculateBudgetByCategory() {
        return groupBudgetsByCategory(getActiveBudgets());
    }

    public double calculateTotalBudgetByCurrentMonth() {
        return getActiveBudgets().stream()
                .filter(budget -> isInCurrentMonth(budget.getStartDate()))
                .mapToDouble(budget -> budget.getValue().doubleValue())
                .sum();
    }

    public double calculateAvailableBudget(double totalExpensesMonth) {
        return calculateTotalBudgetByCurrentMonth() - totalExpensesMonth;
    }

    public Map<String, Double> calculateBudgetByCategoryCurrentMonth() {
        List<Budget> currentMonthBudgets = getActiveBudgets().stream()
                .filter(budget -> isInCurrentMonth(budget.getStartDate()))
                .collect(Collectors.toList());

        return groupBudgetsByCategory(currentMonthBudgets);
    }

    public Map<String, Map<Month, Double>> calculateMonthlyBudgetByCategory() {
        return getActiveBudgets().stream()
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
        return getActiveBudgets().stream()
                .filter(budget -> budget.getStartDate() != null)
                .collect(Collectors.groupingBy(
                        budget -> budget.getStartDate().getMonth(),
                        Collectors.summingDouble(budget -> budget.getValue().doubleValue())
                ));
    }
    
}
