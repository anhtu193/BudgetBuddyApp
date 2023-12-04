package com.example.budgetbuddyapp;

public class Expense {
    protected String expenseID;
    protected String userID;
    protected String expenseName;
    protected int expenseImage;
    protected String expenseTime;
    protected int expenseLimit;
    protected int expenseBudget;

    public String getExpenseID() {
        return expenseID;
    }

    public void setExpenseID(String expenseID) {
        this.expenseID = expenseID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public int getExpenseImage() {
        return expenseImage;
    }

    public void setExpenseImage(int expenseImage) {
        this.expenseImage = expenseImage;
    }

    public String getExpenseTime() {
        return expenseTime;
    }

    public void setExpenseTime(String expenseTime) {
        this.expenseTime = expenseTime;
    }

    public int getExpenseLimit() {
        return expenseLimit;
    }

    public void setExpenseLimit(int expenseLimit) {
        this.expenseLimit = expenseLimit;
    }

    public int getExpenseBudget() {
        return expenseBudget;
    }

    public void setExpenseBudget(int expenseBudget) {
        this.expenseBudget = expenseBudget;
    }

    public Expense(String expenseID, String userID, String expenseName, int expenseImage) {
        this.expenseID = expenseID;
        this.userID = userID;
        this.expenseName = expenseName;
        this.expenseImage = expenseImage;
    }
}
