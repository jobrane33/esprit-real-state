package com.example.realestateapp.model;

public class Item {
    private String title;
    private String location;
    private String shortDescription;
    private String price;
    private String category; // Apartment, Villa, etc.
    private String type;     // Sell / Rent
    private Integer imageResId; // drawable local
    private String imageUrl;    // Firebase URL
    private String ownerName;
    private String ownerContact;
    private String description;

    public Item() {} // Required for Firestore

    // Constructor for drawable static properties
    public Item(String title, String location, String shortDescription, String price, String category,
                String type, int imageResId, String ownerName, String ownerContact, String description) {
        this.title = title;
        this.location = location;
        this.shortDescription = shortDescription;
        this.price = price;
        this.category = category;
        this.type = type;
        this.imageResId = imageResId;
        this.imageUrl = null;
        this.ownerName = ownerName;
        this.ownerContact = ownerContact;
        this.description = description;
    }

    // Constructor for Firebase properties
    public Item(String title, String location, String shortDescription, String price, String category,
                String type, String imageUrl, String ownerName, String ownerContact, String description) {
        this.title = title;
        this.location = location;
        this.shortDescription = shortDescription;
        this.price = price;
        this.category = category;
        this.type = type;
        this.imageUrl = imageUrl;
        this.imageResId = null;
        this.ownerName = ownerName;
        this.ownerContact = ownerContact;
        this.description = description;
    }

    // Getters & Setters
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getShortDescription() { return shortDescription; }
    public String getPrice() { return price; }
    public String getCategory() { return category; }
    public String getType() { return type; }
    public Integer getImageResId() { return imageResId; }
    public String getImageUrl() { return imageUrl; }
    public String getOwnerName() { return ownerName; }
    public String getOwnerContact() { return ownerContact; }
    public String getDescription() { return description; }

    public void setTitle(String title) { this.title = title; }
    public void setLocation(String location) { this.location = location; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public void setPrice(String price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setType(String type) { this.type = type; }
    public void setImageResId(Integer imageResId) { this.imageResId = imageResId; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public void setOwnerContact(String ownerContact) { this.ownerContact = ownerContact; }
    public void setDescription(String description) { this.description = description; }
}
