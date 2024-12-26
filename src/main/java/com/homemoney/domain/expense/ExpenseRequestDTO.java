package com.homemoney.domain.expense;

import java.math.BigDecimal;
import java.util.Date;

public class ExpenseRequestDTO {

    public BigDecimal value;

    public String category;

    public Date expirationDate;

    public Date paymentDate;

    public String description;

    public String paymentMethod;

    public ExpenseRequestDTO(BigDecimal value, String category, Date expirationDate, Date paymentDate, String description, String paymentMethod) {
        this.value = value;
        this.category = category;
        this.expirationDate = expirationDate;
        this.paymentDate = paymentDate;
        this.description = description;
        this.paymentMethod = paymentMethod;
    }
}