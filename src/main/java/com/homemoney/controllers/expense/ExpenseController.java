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
    public String showExpense(@PathVariable Long id, Model model) {
        Expense expense = expenseService.findById(id);
        model.addAttribute("expense", expense);
        return "expense/show";
    }

    @GetMapping("/report")
    public String showReport(Model model, Authentication authentication) {
        User currentUser = userService.findCurrentUserByUsername(authentication);

        if (currentUser == null) {
            return "redirect:/login"; // Redireciona para a página de login se o usuário não estiver autenticado
        }

        // Calcula o total gasto no mês
        BigDecimal totalExpensesMonth = expenseService.calculateTotalExpensesThisMonth(currentUser);
        System.out.println("Total gasto no mês: " + totalExpensesMonth); // Log para depuração

        // Calcula o gasto médio mensal
        BigDecimal averageMonthlyExpense = expenseService.calculateAverageMonthlyExpense(currentUser);
        System.out.println("Gasto médio mensal: " + averageMonthlyExpense); // Log para depuração

        // Calcula o total de despesas compartilhadas
        BigDecimal totalSharedExpenses = expenseService.calculateTotalSharedExpenses(currentUser);
        System.out.println("Despesas compartilhadas: " + totalSharedExpenses); // Log para depuração

        // Obtém despesas por categoria
        Map<Expense.Category, BigDecimal> expensesByCategory = expenseService.getExpensesByCategory(currentUser);
        System.out.println("Despesas por categoria: " + expensesByCategory); // Log para depuração

        // Obtém despesas mensais (últimos 6 meses)
        Map<String, BigDecimal> monthlyExpenses = expenseService.getMonthlyExpenses(currentUser);
        System.out.println("Despesas mensais: " + monthlyExpenses); // Log para depuração

        // Adiciona os dados ao modelo
        model.addAttribute("totalExpensesMonth", totalExpensesMonth);
        model.addAttribute("averageMonthlyExpense", averageMonthlyExpense);
        model.addAttribute("totalSharedExpenses", totalSharedExpenses);
        model.addAttribute("expensesByCategory", expensesByCategory);
        model.addAttribute("monthlyExpenses", monthlyExpenses);

        return "report"; // Nome da página de relatório (report.html)
    }
}
