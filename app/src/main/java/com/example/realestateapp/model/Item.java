package com.example.realestateapp.model;

public class Item {
    private String title;
    private String location;
    private String shortDescription;
    private String price;
    private String category;
    private int imageResId;

    public Item(String title, String location, String shortDescription, String price, String category, int imageResId) {
        this.title = title;
        this.location = location;
        this.shortDescription = shortDescription;
        this.price = price;
        this.category = category;
        this.imageResId = imageResId;
    }

    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getShortDescription() { return shortDescription; }
    public String getPrice() { return price; }
    public String getCategory() { return category; }
    public int getImageResId() { return imageResId; }
}
