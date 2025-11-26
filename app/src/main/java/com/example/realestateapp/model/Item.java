package com.example.realestateapp.model;

public class Item {
    private String title;
    private String location;
    private String shortDescription;
    private String price;
    private String category;
    private int imageResId;
    private String ownerName;
    private String ownerContact;
    private String description;

    public Item(String title, String location, String shortDescription, String price,
                String category, int imageResId, String ownerName, String ownerContact, String description) {
        this.title = title;
        this.location = location;
        this.shortDescription = shortDescription;
        this.price = price;
        this.category = category;
        this.imageResId = imageResId;
        this.ownerName = ownerName;
        this.ownerContact = ownerContact;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getShortDescription() { return shortDescription; }
    public String getPrice() { return price; }
    public String getCategory() { return category; }
    public int getImageResId() { return imageResId; }
    public String getOwnerName() { return ownerName; }
    public String getOwnerContact() { return ownerContact; }
    public String getDescription() { return description; }
}
