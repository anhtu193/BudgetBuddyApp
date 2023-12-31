package com.example.budgetbuddyapp.goal;

import java.io.Serializable;

public class Goal implements Serializable {
    private String goalID;
    private String userID;
    private String goalName;

    private Long goalCurrent;
    private Long goalNumber;
    private int goalImage;

    private String date;


    public String getGoalID() {
        return goalID;
    }

    public Goal() {};

    public void setGoalID(String goalID) {
        this.goalID = goalID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public Long getGoalNumber() {
        return goalNumber;
    }

    public void setGoalNumber(Long goalNumber) {
        this.goalNumber = goalNumber;
    }

    public Long getGoalCurrent() {
        return goalCurrent;
    }

    public void setGoalCurrent(Long goalCurrent) {
        this.goalCurrent = goalCurrent;
    }

    public int getGoalImage() {
        return goalImage;
    }

    public void setGoalImage(int goalImage) {
        this.goalImage = goalImage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        date = date;
    }

    public Goal(String goalID, String userID, String goalName, Long goalCurrent, Long goalNumber, int goalImage, String date) {
        this.goalID = goalID;
        this.userID = userID;
        this.goalName = goalName;
        this.goalCurrent = goalCurrent;
        this.goalNumber = goalNumber;
        this.goalImage = goalImage;
        this.date = date;
    }
}
