package com.homemoney.repositories.budget;

import com.homemoney.model.budget.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
}
