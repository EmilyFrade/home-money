package com.homemoney.services.expense;

import com.homemoney.model.expense.Expense;
import com.homemoney.model.expense.ExpenseShare;
import com.homemoney.model.expense.PaymentMethod;
import com.homemoney.model.user.User;
import com.homemoney.repositories.expense.ExpenseRepository;
import com.homemoney.repositories.expense.ExpenseShareRepository;
import com.homemoney.services.user.UserService;
import com.homemoney.utils.DateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseShareRepository expenseShareRepository;

    @Autowired
    private UserService userService;

    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    public Expense findById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    public void save(Expense expense, List<Long> participantIds, List<BigDecimal> shareValues, Authentication authentication) {
        User user = userService.findCurrentUserByUsername(authentication);
        expense.setCreator(user);
        expense.setResidence(user.getResidence());

        if (expense.getExpirationDate() != null) expense.setExpirationDate(DateUtils.parseToLocalDate(expense.getExpirationDate().toString()));
        if (expense.getPaymentDate() != null) expense.setPaymentDate(DateUtils.parseToLocalDate(expense.getPaymentDate().toString()));

        setExpenseStatus(expense);

        expense.getExpenseShares().clear();

        for (int i = 0; i < participantIds.size(); i++) {
            if (shareValues.get(i).compareTo(BigDecimal.ZERO) > 0) {
                User participant = userService.findById(participantIds.get(i));
                addParticipant(expense, participant, shareValues.get(i), user);
            }
        }

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

    private void addParticipant(Expense expense, User participant, BigDecimal valueShare, User currentUser) {
        ExpenseShare share = new ExpenseShare();
        share.setExpense(expense);
        share.setUser(participant);
        share.setValueShare(valueShare);

        if (participant.getId() == currentUser.getId() && expense.getStatus() == Expense.Status.Paga) {
            share.setStatus(ExpenseShare.Status.Pago);
            share.setPaymentMethod(expense.getPaymentMethod());
        } else {
            share.setStatus(ExpenseShare.Status.Pendente);
        }

        expense.getExpenseShares().add(share);
    }

    public BigDecimal calculateTotalExpensesThisMonth(User user) {
    LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
    LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

    List<Expense> expenses = expenseRepository.findByCreatorAndPaymentDateBetween(user, startOfMonth, endOfMonth);

    return expenses.stream()
            .map(Expense::getValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateAverageMonthlyExpense(User user) {
        LocalDate startDate = LocalDate.now().minusMonths(6);
        LocalDate endDate = LocalDate.now();

        List<Expense> expenses = expenseRepository.findByCreatorAndPaymentDateBetween(user, startDate, endDate);
        BigDecimal total = expenses.stream()
                .map(Expense::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(6), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalSharedExpenses(User user) {
        List<Expense> sharedExpenses = expenseRepository.findByExpenseSharesUser(user);
        return sharedExpenses.stream()
                .map(Expense::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<Expense.Category, BigDecimal> getExpensesByCategory(User user) {
        List<Expense> expenses = expenseRepository.findByCreator(user);
        return expenses.stream()
                .collect(Collectors.groupingBy(
                    Expense::getCategory,
                    Collectors.reducing(BigDecimal.ZERO, Expense::getValue, BigDecimal::add)
                ));
    }

    public Map<String, BigDecimal> getMonthlyExpenses(User user) {
        LocalDate startDate = LocalDate.now().minusMonths(6);
        LocalDate endDate = LocalDate.now();

        List<Expense> expenses = expenseRepository.findByCreatorAndPaymentDateBetween(user, startDate, endDate);
        Map<String, BigDecimal> monthlyExpenses = new LinkedHashMap<>();

        for (int i = 0; i < 6; i++) {
            LocalDate monthStart = startDate.plusMonths(i);
            LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

            BigDecimal total = expenses.stream()
                    .filter(expense -> !expense.getPaymentDate().isBefore(monthStart) &&
                                    !expense.getPaymentDate().isAfter(monthEnd))
                    .map(Expense::getValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            monthlyExpenses.put(monthStart.getMonth().toString(), total);
        }

        return monthlyExpenses;
    }

    public Map<String, BigDecimal> getCurrentMonthExpensesByCategory(User user) {
    List<Object[]> results = expenseRepository.findCurrentMonthExpensesByCategory(user.getId());

    Map<String, BigDecimal> currentMonthExpensesByCategory = new HashMap<>();

    for (Object[] result : results) {
        String category = (String) result[0];
        BigDecimal total = (BigDecimal) result[1];

        currentMonthExpensesByCategory.put(category, total);
    }

    return currentMonthExpensesByCategory;
}
    public void payExpense(Long id, PaymentMethod paymentMethod) {
        Expense expense = findById(id);
        expense.setPaymentMethod(paymentMethod);
        expense.setStatus(Expense.Status.Paga);
        expense.setPaymentDate(LocalDate.now());
        expenseRepository.save(expense);

        ExpenseShare share = expenseShareRepository.findByExpenseAndUser(expense, expense.getCreator());
        share.setPaymentMethod(paymentMethod);
        share.setStatus(ExpenseShare.Status.Pago);
        expenseShareRepository.save(share);
    }

    public void reimburseExpense(Long id, PaymentMethod paymentMethod, User currentUser) {
        Expense expense = findById(id);
        for (ExpenseShare share : expense.getExpenseShares()) {
            if (share.getUser().getId() == currentUser.getId()) {
                share.setPaymentMethod(paymentMethod);
                share.setStatus(ExpenseShare.Status.Reembolsado);
            }
        }
        expenseRepository.save(expense);
    }
}
