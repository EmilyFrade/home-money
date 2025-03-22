package com.homemoney.repositories.expense;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homemoney.model.expense.Expense;
import com.homemoney.model.expense.ExpenseShare;
import com.homemoney.model.user.User;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {
    ExpenseShare findByExpenseAndUser(Expense expense, User user);
}