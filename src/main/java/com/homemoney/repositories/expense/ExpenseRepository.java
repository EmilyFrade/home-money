package com.homemoney.repositories.expense;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homemoney.model.expense.Expense;
import com.homemoney.model.user.User;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByStatus(Expense.Status status);

    List<Expense> findByCreatorAndPaymentDateBetween(User creator, LocalDate startDate, LocalDate endDate);
    List<Expense> findByExpenseSharesUser(User user);

    List<Expense> findByCreator(User user);
}