package com.example.realestateapp.model;

public class Item {

    private String title;
    private String location;
    private String shortdescription;
    private String price;
    private String category;       // Villa, Office, etc.
    private String type;           // Sell or Rent
    private String imageUrl;       // Firebase URL
    private int imageResId;        // Local drawable
    private String ownerName;
    private String ownerContact;
    private String description;

    public Item(){}

    // Constructor for Firebase image
    public Item(String title, String location, String shortDescription, String price,
                String category, String type, String imageUrl,
                String ownerName, String ownerContact, String description) {
        this.title = title;
        this.location = location;
        this.shortdescription = shortDescription;
        this.price = price;
        this.category = category;
        this.type = type;
        this.imageUrl = imageUrl;
        this.imageResId = 0;  // 0 means no drawable
        this.ownerName = ownerName;
        this.ownerContact = ownerContact;
        this.description = description;
    }

    // Constructor for local drawable
    public Item(String title, String location, String shortDescription, String price,
                String category, String type, String imageUrl,
                String ownerName, String ownerContact, String description, int imageResId) {
        this.title = title;
        this.location = location;
        this.shortdescription = shortDescription;
        this.price = price;
        this.category = category;
        this.type = type;
        this.imageUrl = imageUrl;
        this.imageResId = imageResId;
        this.ownerName = ownerName;
        this.ownerContact = ownerContact;
        this.description = description;
    }

    // Getters
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getShortDescription() { return shortdescription; }
    public String getPrice() { return price; }
    public String getCategory() { return category; }
    public String getType() { return type; }
    public String getImageUrl() { return imageUrl; }
    public Integer  getImageResId() { return imageResId; }
    public String getOwnerName() { return ownerName; }
    public String getOwnerContact() { return ownerContact; }
    public String getDescription() { return description; }

    // Setters (if needed)
    public void setTitle(String title) { this.title = title; }
    public void setLocation(String location) { this.location = location; }
    public void setShortDescription(String shortDescription) { this.shortdescription = shortDescription; }
    public void setPrice(String price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setType(String type) { this.type = type; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public void setOwnerContact(String ownerContact) { this.ownerContact = ownerContact; }
    public void setDescription(String description) { this.description = description; }
}
