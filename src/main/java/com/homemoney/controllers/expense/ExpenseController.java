package com.homemoney.controllers.expense;

import com.homemoney.model.expense.Expense;
import com.homemoney.services.expense.ExpenseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping
    public String listExpenses(Model model) {
        List<Expense> expenses = expenseService.findAll();
        model.addAttribute("expenses", expenses);

        return "expense/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("expense", new Expense());
        model.addAttribute("categories", Expense.Category.values());
        model.addAttribute("paymentMethods", Expense.PaymentMethod.values());

        return "expense/form";
    }

    @PostMapping
    public String saveExpense(@ModelAttribute Expense expense) {
        expenseService.save(expense);

        return "redirect:/expense";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Expense expense = expenseService.findById(id);
        model.addAttribute("expense", expense);
        model.addAttribute("categories", Expense.Category.values());
        model.addAttribute("paymentMethods", Expense.PaymentMethod.values());
        model.addAttribute("currentDate", LocalDate.now());

        return "expense/form";
    }

    @PostMapping("/edit/{id}")
    public String updateExpense(@PathVariable Long id, @ModelAttribute Expense expense) {
        expense.setId(id); 
        expenseService.save(expense); 

        return "redirect:/expense";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseService.delete(id);

        return "redirect:/expense";
    }
}
