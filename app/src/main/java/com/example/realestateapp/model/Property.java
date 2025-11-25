package com.example.realestateapp.model;

public class Property {
    private String title;
    private String location;
    private String price;
    private String category;
    private int imageResId;

    public Property(String title, String location, String price, String category, int imageResId) {
        this.title = title;
        this.location = location;
        this.price = price;
        this.category = category;
        this.imageResId = imageResId;
    }

    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getPrice() { return price; }
    public String getCategory() { return category; }
    public int getImageResId() { return imageResId; }
}
