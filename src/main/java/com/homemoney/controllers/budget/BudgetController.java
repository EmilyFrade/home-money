package com.homemoney.controllers.budget;

import com.homemoney.model.budget.Budget;
import com.homemoney.services.budget.BudgetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @GetMapping
    public String listBudgets(Model model) {
        List<Budget> budgets = budgetService.findAll();
        model.addAttribute("budgets", budgets);

        return "budget/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("budget", new Budget());
        model.addAttribute("categories", Budget.Category.values());

        return "budget/form";
    }

    @PostMapping
    public String saveBudget(@ModelAttribute Budget budget) {
        budgetService.save(budget);

        return "redirect:/budget";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Budget budget = budgetService.findById(id);
        model.addAttribute("budget", budget);
        model.addAttribute("categories", Budget.Category.values());
        model.addAttribute("currentDate", LocalDate.now());

        return "budget/form";
    }

    @PostMapping("/edit/{id}")
    public String updateBudget(@PathVariable Long id, @ModelAttribute Budget budget) {
        budget.setId(id);
        budgetService.save(budget);

        return "redirect:/budget";
    }

    @GetMapping("/delete/{id}")
    public String deleteBudget(@PathVariable Long id) {
        budgetService.delete(id);

        return "redirect:/budget";
    }
}
