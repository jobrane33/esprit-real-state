package com.example.realestateapp.model;

public class Property {
    private String title;
    private String location;
    private String price;
    private String category;

    // On garde les deux options pour l'image
    private Integer imageResId; // Pour drawable local
    private String imageUrl;    // Pour image Firebase

    public Property() {} // Nécessaire pour Firestore

    // Constructeur pour drawable local
    public Property(String title, String location, String price, String category, int imageResId) {
        this.title = title;
        this.location = location;
        this.price = price;
        this.category = category;
        this.imageResId = imageResId;
        this.imageUrl = null;
    }

    // Constructeur pour image Firebase
    public Property(String title, String location, String price, String category, String imageUrl) {
        this.title = title;
        this.location = location;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.imageResId = null;
    }

    // Getters
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getPrice() { return price; }
    public String getCategory() { return category; }
    public Integer getImageResId() { return imageResId; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setLocation(String location) { this.location = location; }
    public void setPrice(String price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setImageResId(Integer imageResId) { this.imageResId = imageResId; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
