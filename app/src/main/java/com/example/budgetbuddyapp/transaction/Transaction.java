package com.example.budgetbuddyapp.transaction;

import com.google.type.DateTime;

public class Transaction {
    private String TransactionId;
    private String UserId;
    private String CategoryId;
    private String Note;
    private String Date;
    private String Time;
    private Long Amount;

    public Long getAmount() {
        return Amount;
    }

    public void setAmount(Long amount) {
        Amount = amount;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public Transaction(String transactionId, String userId, String categoryId, String note, String date, String time, Long amount) {
        TransactionId = transactionId;
        UserId = userId;
        CategoryId = categoryId;
        Note = note;
        Date = date;
        Time = time;
        Amount = amount;
    }
}
