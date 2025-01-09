package com.homemoney.services.expense;

import com.homemoney.model.expense.Expense;
import com.homemoney.repositories.expense.ExpenseRepository;
import com.homemoney.utils.DateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    public Expense findById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    public void save(Expense expense) {
        if (expense.getExpirationDate() != null) expense.setExpirationDate(DateUtils.parseToLocalDate(expense.getExpirationDate().toString()));
        if (expense.getPaymentDate() != null) expense.setPaymentDate(DateUtils.parseToLocalDate(expense.getPaymentDate().toString()));

        setExpenseStatus(expense);
        expenseRepository.save(expense);
    }

    public void delete(Long id) {
        expenseRepository.deleteById(id);
    }

    private void setExpenseStatus(Expense expense) {
        if (expense.getPaymentDate() != null) {
            expense.setStatus(Expense.Status.Paga);
            return;
        } 
        
        if (expense.getExpirationDate() != null && expense.getExpirationDate().isBefore(LocalDate.now())) {
            expense.setStatus(Expense.Status.Vencida);
            return;
        } 
        
        expense.setStatus(Expense.Status.Pendente);
    }

    public double calculateTotalExpenses() {
        return expenseRepository.findAll().stream()
                .map(expense -> expense.getValue().doubleValue())
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public Map<String, Double> calculateExpensesByCategory() {
        return expenseRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    expense -> expense.getCategory().toString(),
                    Collectors.summingDouble(expense -> expense.getValue().doubleValue())
                ));
    }
}
