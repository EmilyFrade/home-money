package com.homemoney.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homemoney.domain.expense.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
}