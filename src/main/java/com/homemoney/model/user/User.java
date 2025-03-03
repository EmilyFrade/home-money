package com.homemoney.model.user;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.homemoney.model.budget.Budget;
import com.homemoney.model.expense.Expense;
import com.homemoney.model.expense.ExpenseShare;
import com.homemoney.model.residence.Residence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    @Email
    @Size(max = 255)
    @Column(unique = true)
    private String username;

    @NotNull
    private String password;

    @Size(max = 255)
    private String address;

    private Boolean isActive = true;

    @NotNull
    private LocalDate registrationDate = LocalDate.now();

    @ManyToOne
    @NotNull
    private Residence residence;

    @OneToMany(mappedBy = "creator")
    private Set<Expense> createdExpenses = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<ExpenseShare> expenseShares = new HashSet<>();

    @OneToMany(mappedBy = "creator")
    private Set<Budget> createdBudgets = new HashSet<>();
}
