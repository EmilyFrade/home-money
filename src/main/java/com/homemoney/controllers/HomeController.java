package com.homemoney.controllers;

import com.homemoney.services.budget.BudgetService;
import com.homemoney.services.expense.ExpenseService;
import com.homemoney.services.user.UserService;
import com.homemoney.model.user.User;
import com.homemoney.model.budget.Budget;
import com.homemoney.model.expense.Expense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;
import java.util.List;
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

        // Dados de despesas
        double totalExpenses = expenseService.calculateTotalExpenses();
        Map<String, Double> expensesByCategory = expenseService.calculateExpensesByCategory();

        // Dados de orçamento
        List<Budget> budgets = budgetService.findAll();
        double totalBudget = budgets.stream()
                                    .mapToDouble(budget -> budget.getValue().doubleValue()) // Conversão para double
                                    .sum(); // Soma os valores de todos os orçamentos
        double availableBudget = totalBudget - totalExpenses; // Orçamento disponível

        // Preparando dados para os gráficos
        Map<String, Double> budgetVsSpentData = Map.of(
            "Orçamento", totalBudget,
            "Gastos", totalExpenses
        );

        // Categorias para o gráfico de distribuição de gastos
        List<String> categories = budgets.stream()
                                         .map(budget -> budget.getCategory().name()) // Convertendo para String
                                         .distinct() // Filtra as categorias únicas
                                         .collect(Collectors.toList());

        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("availableBudget", availableBudget);
        model.addAttribute("expensesByCategory", expensesByCategory);
        model.addAttribute("budgetVsSpentData", budgetVsSpentData);
        model.addAttribute("categories", categories);
        model.addAttribute("user", user);

        return "home";
    }
}
