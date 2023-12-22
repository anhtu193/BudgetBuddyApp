package com.example.budgetbuddyapp.categories;

import java.io.Serializable;

public class Category implements Serializable {
    private String categoryID;
    private String userID;
    private String categoryName;
    private String categoryType;
    private int categoryImage;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public Category() {};

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public int getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(int categoryImage) {
        this.categoryImage = categoryImage;
    }

    public Category(String categoryID, String userID, String categoryName, String categoryType, int categoryImage) {
        this.categoryID = categoryID;
        this.userID = userID;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.categoryImage = categoryImage;
        this.isSelected = false;
    }

    public Category(String categoryID, String userID, String categoryName, String categoryType, int categoryImage, Boolean isSelected) {
        this.categoryID = categoryID;
        this.userID = userID;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.categoryImage = categoryImage;
        this.isSelected = isSelected;
    }
}
