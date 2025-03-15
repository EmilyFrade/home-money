package com.homemoney.model.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import com.homemoney.model.residence.Residence;
import com.homemoney.model.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal value;

    @Size(max = 255)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category; 

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate; 

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; 

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status; 

    @ManyToOne
    @NotNull
    private Residence residence;

    @ManyToOne
    @NotNull
    private User creator;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExpenseShare> expenseShares = new HashSet<>();

    public enum Category {
        Alimentação, 
        Casa,
        Entretenimento, 
        Saúde, 
        Transporte,
        Outro; 
    }

    public enum PaymentMethod {
        Boleto,
        Crédito, 
        Débito,
        Dinheiro,
        Pix, 
        Outro; 
    }

    public enum Status {
        Pendente, 
        Paga, 
        Vencida;
    }

    public void addParticipant(User user, BigDecimal valueShare) {
        ExpenseShare share = new ExpenseShare();
        share.setExpense(this);
        share.setUser(user);
        share.setValueShare(valueShare);
        share.setStatus(ExpenseShare.Status.PENDENTE);
        share.setValuePaid(BigDecimal.ZERO);
        this.expenseShares.add(share);
    }

    public void clearShares() {
        this.expenseShares.clear();
    }

    public void registerPayment(User user, BigDecimal value, User paidBy) {
        expenseShares.stream()
            .filter(share -> share.getUser().equals(user))
            .findFirst()
            .ifPresent(share -> {
                share.setValuePaid(value);
                share.setPaidBy(paidBy);
                share.setPaymentDate(LocalDate.now());
                share.setStatus(paidBy.equals(user) ? 
                    ExpenseShare.Status.PAGO : 
                    ExpenseShare.Status.REEMBOLSADO);
            });
    }
}
