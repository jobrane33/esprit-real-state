package com.example.realestateapp.model;

public class Property {
    private String type;
    private String title;
    private String location;
    private String price;
    private String category;
    private String ownername;
    private String contactno;
    private String shortdescription;
    private String description;    private String imageUrl;


    // Image options
    private Integer imageResId;   // Local drawable
    private String imageBase64;   // Firebase Base64

    public Property() {} // Required for Firestore

    // Constructors
    public Property(String title, String location, String price, String category, int imageResId) {
        this.title = title;
        this.location = location;
        this.price = price;
        this.category = category;
        this.imageResId = imageResId;
        this.imageBase64 = null;
    }

    public Property(String title, String location, String price, String category, String imageBase64) {
        this.title = title;
        this.location = location;
        this.price = price;
        this.category = category;
        this.imageBase64 = imageBase64;
        this.imageResId = null;
    }

    public Property(String title, String category, String type, String price, String location, String imageUrl) { this.title = title; this.type=type; this.location = location; this.price = price; this.category = category; this.imageUrl = imageUrl; this.imageResId = null; }
    // Getters & Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getOwnername() { return ownername; }
    public void setOwnername(String ownername) { this.ownername = ownername; }

    public String getContactno() { return contactno; }
    public void setContactno(String contactno) { this.contactno = contactno; }

    public String getShortdescription() { return shortdescription; }
    public void setShortdescription(String shortdescription) { this.shortdescription = shortdescription; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getImageResId() { return imageResId; }
    public void setImageResId(Integer imageResId) { this.imageResId = imageResId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String ImageUrl) { this.imageUrl = ImageUrl; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
}
