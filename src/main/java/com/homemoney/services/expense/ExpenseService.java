package com.homemoney.services.expense;

import com.homemoney.model.expense.Expense;
import com.homemoney.repositories.expense.ExpenseRepository;
import com.homemoney.utils.DateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
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

    @Scheduled(cron = "0 0 0 * * ?") // Um job que executa todos os dias à meia-noite
    public void updateExpiredExpenses() {
        List<Expense> pendingExpenses = expenseRepository.findByStatus(Expense.Status.Pendente);

        for (Expense expense : pendingExpenses) {
            if (expense.getExpirationDate() != null && expense.getExpirationDate().isBefore(LocalDate.now())) {
                expense.setStatus(Expense.Status.Vencida);
                // TO DO: Enviar notificação para o usuário
            }
        }

        expenseRepository.saveAll(pendingExpenses);
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

    private LocalDate getRelevantDate(Expense expense) {
        return expense.getPaymentDate() != null ? expense.getPaymentDate() : expense.getExpirationDate();
    }

    private boolean isInCurrentMonth(Expense expense) {
        LocalDate date = getRelevantDate(expense);
        LocalDate now = LocalDate.now();
        return date != null && date.getMonth() == now.getMonth() && date.getYear() == now.getYear();
    }

    public double calculateTotalExpenses(boolean currentMonthOnly) {
        return expenseRepository.findAll().stream()
                .filter(expense -> !currentMonthOnly || isInCurrentMonth(expense))
                .map(expense -> expense.getValue().doubleValue())
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public Map<String, Double> calculateExpensesByCategory(boolean currentMonthOnly) {
        return expenseRepository.findAll().stream()
                .filter(expense -> !currentMonthOnly || isInCurrentMonth(expense))
                .collect(Collectors.groupingBy(
                    expense -> expense.getCategory().toString(),
                    Collectors.summingDouble(expense -> expense.getValue().doubleValue())
                ));
    }

    public Map<String, Map<String, Double>> calculateMonthlyExpensesByCategory() {
        return expenseRepository.findAll().stream()
                .filter(expense -> getRelevantDate(expense) != null)
                .collect(Collectors.groupingBy(
                    expense -> expense.getCategory().toString(),
                    Collectors.groupingBy(
                        expense -> getRelevantDate(expense)
                                   .getMonth()
                                   .getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-BR")),
                        Collectors.summingDouble(expense -> expense.getValue().doubleValue())
                    )
                ));
    }

    public Map<Month, Double> calculateTotalExpensesByMonth() {
        return expenseRepository.findAll().stream()
                .filter(expense -> getRelevantDate(expense) != null)
                .collect(Collectors.groupingBy(
                    expense -> getRelevantDate(expense).getMonth(),
                    Collectors.summingDouble(expense -> expense.getValue().doubleValue())
                ));
    }

    public double calculateAverageMonthlyExpense() {
        Map<Month, Double> totalExpensesByMonth = calculateTotalExpensesByMonth();

        return totalExpensesByMonth.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }
}
