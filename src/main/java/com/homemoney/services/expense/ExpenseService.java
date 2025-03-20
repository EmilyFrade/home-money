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
import java.time.LocalDate;
import java.util.List;

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
