package com.homemoney.model.residence;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.homemoney.model.budget.Budget;
import com.homemoney.model.expense.Expense;
import com.homemoney.model.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Residence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(max = 255)
    private String nome;

    @NotNull
    @Size(max = 255)
    private String logradouro;

    @NotNull
    @Size(max = 100)
    private String bairro;

    @NotNull
    @Size(max = 20)
    private String cep;

    @NotNull
    @Size(max = 100)
    private String cidade;

    @NotNull
    @Size(max = 2)
    private String estado;

    @NotNull
    private int capacidadeMaxima;

    @NotNull
    private BigDecimal valorAluguel;

    @NotNull
    @Size(max = 255)
    @Column(unique = true)
    private String inviteCode;

    @OneToMany(mappedBy = "residence")
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "residence")
    private Set<Expense> expenses = new HashSet<>();

    @OneToMany(mappedBy = "residence")
    private Set<Budget> budgets = new HashSet<>();
}