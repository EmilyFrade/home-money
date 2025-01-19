package com.homemoney.controllers;

import com.homemoney.services.budget.BudgetService;
import com.homemoney.services.expense.ExpenseService;
import com.homemoney.services.user.UserService;
import com.homemoney.model.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Month;
import java.util.Map;

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

        double totalExpensesMonth = expenseService.calculateTotalExpenses(true);
        Map<String, Double> expensesByCategory = expenseService.calculateExpensesByCategory(true);
        Map<String, Map<String, Double>> monthlyExpensesByCategory = expenseService.calculateMonthlyExpensesByCategory();
        Map<Month, Double> monthlyExpenses = expenseService.calculateTotalExpensesByMonth();

        double availableBudget = budgetService.calculateAvailableBudget(totalExpensesMonth);
        Map<String, Double> budgetByCategory = budgetService.calculateBudgetByCategoryCurrentMonth();
        Map<String, Map<Month, Double>> monthlyBudgetByCategory = budgetService.calculateMonthlyBudgetByCategory();
        Map<Month, Double> monthlyBudget = budgetService.calculateTotalBudgetByMonth(); 

        double averageMonthlyExpense = monthlyExpenses.values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);

        // Passando as variáveis para a view
        model.addAttribute("totalExpensesMonth", totalExpensesMonth);
        model.addAttribute("availableBudget", availableBudget);
        model.addAttribute("averageMonthlyExpense", averageMonthlyExpense);
        model.addAttribute("expensesByCategory", expensesByCategory);
        model.addAttribute("budgetByCategory", budgetByCategory);
        model.addAttribute("monthlyExpensesByCategory", monthlyExpensesByCategory);
        model.addAttribute("monthlyBudgetByCategory", monthlyBudgetByCategory);
        model.addAttribute("monthlyExpenses", monthlyExpenses);  
        model.addAttribute("monthlyBudget", monthlyBudget);    
        model.addAttribute("user", user);

        return "home";
    }
}
