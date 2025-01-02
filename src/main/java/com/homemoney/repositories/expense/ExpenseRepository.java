package com.homemoney.repositories.expense;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homemoney.model.expense.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}