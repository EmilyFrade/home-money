package com.homemoney.controllers.expense;

import com.homemoney.model.expense.Expense;
import com.homemoney.services.expense.ExpenseService;
import com.homemoney.services.user.UserService;
import com.homemoney.model.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listExpenses(Model model) {
        List<Expense> expenses = expenseService.findAll();
        model.addAttribute("expenses", expenses);

        return "expense/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, Authentication authentication) {
        User currentUser = userService.findCurrentUserByUsername(authentication);
        
        List<User> residenceUsers = userService.findByResidenceId(currentUser.getResidence().getId());
        
        model.addAttribute("expense", new Expense());
        model.addAttribute("categories", Expense.Category.values());
        model.addAttribute("paymentMethods", Expense.PaymentMethod.values());
        model.addAttribute("residenceUsers", residenceUsers);
        
        return "expense/form";
    }

    @PostMapping("/{id}")
    public String saveExpense(@PathVariable Long id, 
                            @ModelAttribute Expense expense,
                            @RequestParam List<Long> participantIds,
                            @RequestParam List<BigDecimal> shareValues,
                            Authentication authentication) {
        
        expenseService.saveWithShares(id, expense, participantIds, shareValues, authentication);
        return "redirect:/expense";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Expense expense = expenseService.findById(id);
        model.addAttribute("expense", expense);
        model.addAttribute("categories", Expense.Category.values());
        model.addAttribute("paymentMethods", Expense.PaymentMethod.values());

        return "expense/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseService.delete(id);

        return "redirect:/expense";
    }

    @GetMapping("/show/{id}")
    public String showExpense(@PathVariable Long id, Model model) {
        Expense expense = expenseService.findById(id);
        model.addAttribute("expense", expense);
        return "expense/show";
    }
}
