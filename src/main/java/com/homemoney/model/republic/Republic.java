package com.homemoney.model.republic;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor // Construtor sem argumentos
public class Republic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(max = 100)
    private String nome;

    @NotNull
    @Size(max = 255)
    private String endereco;

    @NotNull
    private int numeroDeQuartos;

    @NotNull
    private int capacidadeMaxima;

    private Boolean ativa = false; // Define o valor padrão como false

    @NotNull
    private double valorAluguel;
}
