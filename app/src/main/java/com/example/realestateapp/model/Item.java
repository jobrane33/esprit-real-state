package com.example.realestateapp.model;

public class Item {
    private String title;
    private String location;
    private String shortDescription;
    private String price;
    private String category;
    private Integer imageResId; // drawable local
    private String imageUrl;    // Firebase URL
    private String ownerName;
    private String ownerContact;
    private String description;

    public Item() {} // Nécessaire pour Firestore

    // Constructeur drawable local
    public Item(String title, String location, String shortDescription, String price, String category, int imageResId, String ownerName, String ownerContact, String description) {
        this.title = title;
        this.location = location;
        this.shortDescription = shortDescription;
        this.price = price;
        this.category = category;
        this.imageResId = imageResId;
        this.imageUrl = null;
        this.ownerName = ownerName;
        this.ownerContact = ownerContact;
        this.description = description;
    }

    // Constructeur Firebase URL
    public Item(String title, String location, String shortDescription, String price, String category, String imageUrl, String ownerName, String ownerContact, String description) {
        this.title = title;
        this.location = location;
        this.shortDescription = shortDescription;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.imageResId = null;
        this.ownerName = ownerName;
        this.ownerContact = ownerContact;
        this.description = description;
    }

    // --- Getters & Setters ---

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getImageResId() { return imageResId; }
    public void setImageResId(Integer imageResId) { this.imageResId = imageResId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerContact() { return ownerContact; }
    public void setOwnerContact(String ownerContact) { this.ownerContact = ownerContact; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
