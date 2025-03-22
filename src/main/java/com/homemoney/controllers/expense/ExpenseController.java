package com.homemoney.controllers.expense;

import com.homemoney.model.expense.Expense;
import com.homemoney.model.expense.PaymentMethod;
import com.homemoney.services.expense.ExpenseService;
import com.homemoney.services.user.UserService;
import com.homemoney.model.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listExpenses(Model model, Authentication authentication) {
        User currentUser = userService.findCurrentUserByUsername(authentication);
        
        List<Expense> allExpenses = expenseService.findAll();
        
        List<Expense> myExpenses = allExpenses.stream()
            .filter(expense -> expense.getCreator().getId() == currentUser.getId())
            .collect(Collectors.toList());
        
        List<Expense> sharedExpenses = allExpenses.stream()
            .filter(expense -> expense.getCreator().getId() != currentUser.getId() &&
                expense.getExpenseShares().stream()
                .anyMatch(share -> share.getUser().getId() == currentUser.getId()))
            .collect(Collectors.toList());

        model.addAttribute("myExpenses", myExpenses);
        model.addAttribute("sharedExpenses", sharedExpenses);
        model.addAttribute("currentUser", currentUser);

        return "expense/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, Authentication authentication) {
        User currentUser = userService.findCurrentUserByUsername(authentication);
        
        List<User> residenceUsers = userService.findByResidenceId(currentUser.getResidence().getId());
        
        model.addAttribute("expense", new Expense());
        model.addAttribute("categories", Expense.Category.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("residenceUsers", residenceUsers);
        model.addAttribute("currentUser", currentUser);
        
        return "expense/form";
    }

    @PostMapping("/{id}")
    public String saveExpense(@PathVariable Long id, 
                            @ModelAttribute Expense expense,
                            @RequestParam List<Long> participantIds,
                            @RequestParam List<BigDecimal> shareValues,
                            Authentication authentication) {

        expense.setId(id);
        expenseService.save(expense, participantIds, shareValues, authentication);

        return "redirect:/expense";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Expense expense = expenseService.findById(id);
        model.addAttribute("expense", expense);
        model.addAttribute("categories", Expense.Category.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());

        return "expense/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseService.delete(id);

        return "redirect:/expense";
    }

    @GetMapping("/show/{id}")
    public String showExpense(@PathVariable Long id, Model model, Authentication authentication) {
        User currentUser = userService.findCurrentUserByUsername(authentication);
        Expense expense = expenseService.findById(id);
        model.addAttribute("expense", expense);
        model.addAttribute("currentUser", currentUser);
        return "expense/show";
    }

    @GetMapping("/report")
    public String showReport(Model model, Authentication authentication) {
        User currentUser = userService.findCurrentUserByUsername(authentication);

        if (currentUser == null) {
            return "redirect:/login";
        }

        BigDecimal totalExpensesMonth = expenseService.calculateTotalExpensesThisMonth(currentUser);
        BigDecimal averageMonthlyExpense = expenseService.calculateAverageMonthlyExpense(currentUser);
        BigDecimal totalSharedExpenses = expenseService.calculateTotalSharedExpenses(currentUser);

        Map<Expense.Category, BigDecimal> expensesByCategory = expenseService.getExpensesByCategory(currentUser);
        Map<String, BigDecimal> monthlyExpenses = expenseService.getMonthlyExpenses(currentUser);

        Map<String, BigDecimal> currentMonthExpensesByCategory = expenseService.getCurrentMonthExpensesByCategory(currentUser);

        model.addAttribute("totalExpensesMonth", totalExpensesMonth);
        model.addAttribute("averageMonthlyExpense", averageMonthlyExpense);
        model.addAttribute("totalSharedExpenses", totalSharedExpenses);
        model.addAttribute("expensesByCategory", expensesByCategory);
        model.addAttribute("monthlyExpenses", monthlyExpenses);
        model.addAttribute("currentMonthExpensesByCategory", currentMonthExpensesByCategory); //erro

        return "report";
    }
    @PostMapping("/pay/{id}")
    public String payExpense(@PathVariable Long id, @RequestParam PaymentMethod paymentMethod) {
        expenseService.payExpense(id, paymentMethod);
        return "redirect:/expense";
    }

    @PostMapping("/reimburse/{id}")
    public String reimburseExpense(@PathVariable Long id, @RequestParam PaymentMethod paymentMethod, Authentication authentication) {
        User currentUser = userService.findCurrentUserByUsername(authentication);
        expenseService.reimburseExpense(id, paymentMethod, currentUser);
        return "redirect:/expense";
    }
}
