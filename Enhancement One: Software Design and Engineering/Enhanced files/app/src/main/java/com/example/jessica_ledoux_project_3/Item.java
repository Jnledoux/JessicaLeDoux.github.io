package com.example.jessica_ledoux_project_3;

public class Item {
    private int itemId;
    private String userEmail;
    private String description;
    private String quantity;
    private String category;

    // Default constructor
    public Item() {
    }

    // Full constructor when the ID is provided
    public Item(int itemId, String userEmail, String description, String quantity, String category) {
        this.itemId = itemId;
        this.userEmail = userEmail;
        this.description = description;
        this.quantity = quantity;
        this.category = category;
    }

    // Constructor when the ID is not provided (before it is enter into the database)
    public Item(String userEmail, String description, String quantity, String category) {
        this.userEmail = userEmail;
        this.description = description;
        this.quantity = quantity;
        this.category = category;
    }

    // Getters and Setters
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}