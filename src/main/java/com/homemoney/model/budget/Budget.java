package com.homemoney.model.budget;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.homemoney.model.residence.Residence;
import com.homemoney.model.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal value;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Status status; 

    @ManyToOne
    @NotNull
    private Residence residence;

    @ManyToOne
    @NotNull
    private User creator;

    public enum Category {
        Alimentação, 
        Casa,
        Entretenimento, 
        Saúde, 
        Transporte,
        Outro; 
    }

    public enum Status {
        Ativo, 
        Inativo;
    }

    public void setStatusBasedOnDate() {
        LocalDate currentDate = LocalDate.now();
        if (startDate != null && endDate != null) {
            status = (currentDate.isAfter(startDate) && currentDate.isBefore(endDate)) ? Status.Ativo : Status.Inativo;
        } else {
            status = Status.Inativo;
        }
    }
}
