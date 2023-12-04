package com.example.budgetbuddyapp;

public class ExpenseProgress extends Expense {

    public ExpenseProgress(String expenseID, String userID, String expenseName, int expenseImage, String expenseTime, int expenseLimit) {
        super(expenseID, userID, expenseName, expenseImage);
        this.expenseTime = expenseTime;
        this.expenseLimit = expenseLimit;
    }
}
