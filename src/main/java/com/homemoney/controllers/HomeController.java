package com.homemoney.controllers;

import com.homemoney.services.budget.BudgetService;
import com.homemoney.services.expense.ExpenseService;
import com.homemoney.services.user.UserService;
import com.homemoney.model.user.User;
//import com.homemoney.model.budget.Budget;
//import com.homemoney.model.expense.Expense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Month;
//import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @Autowired
    private BudgetService budgetService;

    @GetMapping("/")
    public String home(Model model) {
        User user = userService.findById(1L);

        double totalExpensesMonth = expenseService.calculateTotalExpensesByCurrentMonth();
        Map<String, Double> expensesByCategory = expenseService.calculateExpensesByCategoryCurrentMonth();

        double availableBudget = budgetService.calculateAvailableBudget(totalExpensesMonth);
        Map<String, Double> budgetByCategory = budgetService.calculateBudgetByCategoryCurrentMonth();

        Map<Month, Double> monthlyExpenses = expenseService.findAll().stream()
            .filter(expense -> expense.getPaymentDate() != null)
            .collect(Collectors.groupingBy(
                expense -> expense.getPaymentDate().getMonth(),
                Collectors.summingDouble(expense -> expense.getValue().doubleValue())
            ));

        double averageMonthlyExpense = monthlyExpenses.values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);

        model.addAttribute("totalExpensesMonth", totalExpensesMonth);
        model.addAttribute("availableBudget", availableBudget);
        model.addAttribute("averageMonthlyExpense", averageMonthlyExpense);
        model.addAttribute("expensesByCategory", expensesByCategory);
        model.addAttribute("budgetByCategory", budgetByCategory);
        model.addAttribute("user", user);

        return "home";
    }
}