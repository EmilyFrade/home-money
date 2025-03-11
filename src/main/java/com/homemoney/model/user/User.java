package com.homemoney.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

import com.homemoney.model.residence.Residence;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(max = 100)
    private String name;

    @ManyToOne
    @JoinColumn(name = "residence_id", nullable = true)
    private Residence residence;

    @NotNull
    @Email
    @Size(max = 255)
    @Column(unique = true)
    private String username;

    @NotNull
    private String password;

    @Size(max = 255)
    private String address;

    private Boolean isActive;

    @NotNull
    private LocalDate registrationDate;
}
