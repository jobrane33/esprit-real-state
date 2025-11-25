package com.example.realestateapp.model;

public class Property {

    private String title;
    private String location;
    private String price;
    private String category;
    private int imageResId;
    private String shortDescription; // optional
    private String description;      // optional
    private String ownerName;        // optional
    private String contactNo;        // optional

    // Constructor
    public Property(String title, String location, String price, String category, int imageResId) {
        this.title = title;
        this.location = location;
        this.price = price;
        this.category = category;
        this.imageResId = imageResId;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getContactNo() {
        return contactNo;
    }

    // Setters (optional, if you want to modify)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
}
