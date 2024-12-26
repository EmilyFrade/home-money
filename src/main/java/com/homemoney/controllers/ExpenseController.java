package com.homemoney.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.homemoney.domain.expense.ExpenseRequestDTO;
import com.homemoney.services.ExpenseService;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping()
    public String create(@ModelAttribute ExpenseRequestDTO expenseRequestDTO) {
        expenseService.createExpense(expenseRequestDTO);

        return "ok";
    }

    @GetMapping("/test")
    public String getOk() {
        return "ok";
    }
}