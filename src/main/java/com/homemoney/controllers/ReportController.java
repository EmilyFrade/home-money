
package com.homemoney.controllers;

import com.homemoney.repositories.expense.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ExpenseRepository expenseRepository;

    @GetMapping("/report")
    public String relatorio(Model model) {

        BigDecimal totalExpensesMonth = expenseRepository.findTotalExpensesMonth();
        model.addAttribute("totalExpensesMonth", totalExpensesMonth != null ? totalExpensesMonth : BigDecimal.ZERO);

        BigDecimal averageMonthlyExpense = expenseRepository.findAverageMonthlyExpense();
        model.addAttribute("averageMonthlyExpense", averageMonthlyExpense != null ? averageMonthlyExpense : BigDecimal.ZERO);

        List<Object[]> expensesByCategoryData = expenseRepository.findExpensesByCategory();
        Map<String, BigDecimal> expensesByCategory = new HashMap<>();
        for (Object[] data : expensesByCategoryData) {
            expensesByCategory.put(data[0].toString(), (BigDecimal) data[1]);
        }
        model.addAttribute("expensesByCategory", expensesByCategory);

        List<Object[]> monthlyExpensesData = expenseRepository.findMonthlyExpensesLast6Months();
        Map<String, BigDecimal> monthlyExpenses = new HashMap<>();
        for (Object[] data : monthlyExpensesData) {
            monthlyExpenses.put(data[0].toString(), (BigDecimal) data[1]);
        }
        model.addAttribute("monthlyExpenses", monthlyExpenses);

        return "report";
    }
}