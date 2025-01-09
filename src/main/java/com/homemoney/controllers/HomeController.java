package com.homemoney.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.homemoney.services.expense.ExpenseService;
import com.homemoney.services.user.UserService;
import com.homemoney.model.user.User;

import org.springframework.ui.Model;
import java.util.Map;


@Controller
public class HomeController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        User user = userService.findById(1L);

        double totalExpenses = expenseService.calculateTotalExpenses();
        Map<String, Double> expensesByCategory = expenseService.calculateExpensesByCategory();

        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("expensesByCategory", expensesByCategory);
        model.addAttribute("user", user);
        return "home";
    }
}
