package com.homemoney;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.homemoney.services.expense.ExpenseService;

@Component
public class StartupRunner {

    @Autowired
    private ExpenseService expenseService;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        expenseService.updateExpiredExpenses();
    }
}