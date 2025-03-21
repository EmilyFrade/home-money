package com.homemoney.repositories.expense;

import com.homemoney.model.expense.Expense;
import com.homemoney.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT SUM(e.value) FROM Expense e WHERE MONTH(e.paymentDate) = MONTH(CURRENT_DATE) AND YEAR(e.paymentDate) = YEAR(CURRENT_DATE) AND e.status = 'Paga'")
    BigDecimal findTotalExpensesMonth();

    @Query("SELECT AVG(e.value) FROM Expense e WHERE e.status = 'Paga'")
    BigDecimal findAverageMonthlyExpense();

    @Query("SELECT e.category, SUM(e.value) FROM Expense e WHERE e.status = 'Paga' GROUP BY e.category")
    List<Object[]> findExpensesByCategory();

    @Query(value = "SELECT e.category, SUM(e.value) AS total " +
    "FROM expense e " +
    "WHERE e.creator_id = :userId " +
    "AND MONTH(e.payment_date) = MONTH(CURRENT_DATE) " +
    "AND YEAR(e.payment_date) = YEAR(CURRENT_DATE) " +
    "GROUP BY e.category", nativeQuery = true)

    List<Object[]> findCurrentMonthExpensesByCategory(@Param("userId") Long userId);

    List<Expense> findByStatus(Expense.Status status);

    List<Expense> findByCreatorAndPaymentDateBetween(User creator, LocalDate startDate, LocalDate endDate);

    List<Expense> findByExpenseSharesUser(User user);

    List<Expense> findByCreator(User user);
}