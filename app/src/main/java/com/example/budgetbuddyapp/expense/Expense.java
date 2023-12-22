package com.example.budgetbuddyapp.expense;

public class Expense {
    protected String expenseID;
    protected String userID;
    protected String expenseName;
    protected String categoryID;
    protected int expenseImage;
    protected String expenseTime;
    protected int expenseLimit;
    protected int expenseCurrent;

    public String getExpenseID() {
        return expenseID;
    }

    public void setExpenseID(String expenseID) {
        this.expenseID = expenseID;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
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

    public int getExpenseCurrent() {
        return expenseCurrent;
    }

    public void setExpenseCurrent(int expenseCurrent) {
        this.expenseCurrent = expenseCurrent;
    }

    public Expense(String expenseID, String userID, String expenseName, int expenseImage, String categoryID) {
        this.expenseID = expenseID;
        this.userID = userID;
        this.expenseName = expenseName;
        this.expenseImage = expenseImage;
        this.categoryID = categoryID;
    }
}
