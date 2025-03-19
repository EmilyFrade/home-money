package com.homemoney.model.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.homemoney.model.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class ExpenseShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private User user;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal valueShare;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal valuePaid = BigDecimal.ZERO;

    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    private User paidBy;

    public enum Status {
        Pendente, 
        Pago,       
        Reembolsado 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpenseShare)) return false;
        ExpenseShare that = (ExpenseShare) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 