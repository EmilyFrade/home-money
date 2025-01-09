package com.homemoney.repositories.expense;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homemoney.model.expense.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByStatus(Expense.Status status);
}