package com.homemoney.services.expense;

import com.homemoney.model.expense.Expense;
import com.homemoney.model.user.User;
import com.homemoney.repositories.expense.ExpenseRepository;
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
import java.util.Optional;

@Service
@Transactional
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserService userService;

    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    public Expense findById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    public void save(Expense expense, Authentication authentication) {
        Optional<User> user = userService.findCurrentUserByUsername(authentication);
        expense.setCreator(user.get());
        expense.setResidence(user.get().getResidence());

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

    @Transactional
    public void saveWithShares(Long id, Expense expense, List<Long> participantIds, 
                             List<BigDecimal> shareValues, Authentication authentication) {
        User creator = userService.findCurrentUserByUsername(authentication)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Expense expenseToSave;
        if (id != null && id > 0) {
            expenseToSave = findById(id);
            // Atualiza os campos da despesa existente
            expenseToSave.setDescription(expense.getDescription());
            expenseToSave.setValue(expense.getValue());
            expenseToSave.setCategory(expense.getCategory());
            expenseToSave.setPaymentMethod(expense.getPaymentMethod());
            expenseToSave.setExpirationDate(expense.getExpirationDate());
            expenseToSave.setPaymentDate(expense.getPaymentDate());
        } else {
            expenseToSave = expense;
            expenseToSave.setStatus(Expense.Status.Pendente);
            expenseToSave.setCreator(creator);
            expenseToSave.setResidence(creator.getResidence());
        }

        // Limpa todas as shares existentes
        expenseToSave.getExpenseShares().clear();

        // Adiciona as novas shares
        for (int i = 0; i < participantIds.size(); i++) {
            if (shareValues.get(i).compareTo(BigDecimal.ZERO) > 0) {
                User participant = userService.findById(participantIds.get(i));
                
                expenseToSave.addParticipant(participant, shareValues.get(i));
            }
        }

        expenseRepository.save(expenseToSave);
    }
}
