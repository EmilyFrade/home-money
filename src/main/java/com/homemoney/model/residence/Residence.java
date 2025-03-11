package com.homemoney.model.residence;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @NotNull
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
}