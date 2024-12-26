package com.homemoney.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.homemoney.domain.expense.Expense;
import com.homemoney.domain.expense.ExpenseRequestDTO;
import com.homemoney.repositories.ExpenseRepository;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;
    
    public Expense createExpense(ExpenseRequestDTO expenseRequestDTO) {
        Expense expense = new Expense();
        expense.setValue(expenseRequestDTO.value);
        expense.setCategory(expenseRequestDTO.category);
        expense.setExpirationDate(expenseRequestDTO.expirationDate);
        expense.setPaymentDate(expenseRequestDTO.paymentDate);
        expense.setDescription(expenseRequestDTO.description);
        expense.setPaymentMethod(expenseRequestDTO.paymentMethod);

        return expenseRepository.save(expense);
    }
}