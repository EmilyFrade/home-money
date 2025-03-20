package com.homemoney.controllers;

import com.homemoney.model.expense.Expense;
import com.homemoney.model.user.User;
import com.homemoney.services.expense.ExpenseService;
import com.homemoney.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ReportController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @GetMapping("/report")
    public String relatorio(Model model, Authentication authentication) {
        User currentUser = userService.findCurrentUserByUsername(authentication);

        if (currentUser == null) {
            return "redirect:/login";
        }

        BigDecimal totalExpensesMonth = expenseService.calculateTotalExpensesThisMonth(currentUser);
        model.addAttribute("totalExpensesMonth", totalExpensesMonth != null ? totalExpensesMonth : BigDecimal.ZERO);

        BigDecimal averageMonthlyExpense = expenseService.calculateAverageMonthlyExpense(currentUser);
        model.addAttribute("averageMonthlyExpense", averageMonthlyExpense != null ? averageMonthlyExpense : BigDecimal.ZERO);

        Map<Expense.Category, BigDecimal> expensesByCategory = expenseService.getExpensesByCategory(currentUser);
        model.addAttribute("expensesByCategory", expensesByCategory);

        Map<String, BigDecimal> currentMonthExpensesByCategory = expenseService.getCurrentMonthExpensesByCategory(currentUser);
        model.addAttribute("currentMonthExpensesByCategory", currentMonthExpensesByCategory);

        return "report";
    }
}