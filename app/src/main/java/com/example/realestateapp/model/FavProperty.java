package com.example.realestateapp.model;

public class FavProperty {
    private String id;
    private String title;
    private String imageuri;
    private String location;
    private String type;
    private String price;
    private String shortdescription;
    private String description;
    private String contactno;
    private String ownername;
    private boolean liked;

    public FavProperty() {
        // Empty constructor needed for Firestore
    }

    public FavProperty(String id, String title, String imageuri, String location, String type, String price, String shortdescription, String ownername, String description, String contactno ) {
        this.id = id;
        this.title = title;
        this.imageuri = imageuri;
        this.location = location;
        this.type = type;
        this.price = price;
        this.shortdescription = shortdescription;
        this.ownername = ownername;
        this.description = description;
        this.contactno = contactno;

    }


    public String getId() { return id;}

    public String getTitle() { return title;}
    public String getImageuri() {
        return imageuri;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public String getPrice() {
        return price;
    }

    public String getShortDescription() {
        return shortdescription;
    }

    public String getDescription() {
        return description;
    }
    public String getContactno() {
        return contactno;
    }

    public String getOwnername() {
        return ownername;
    }
    public boolean isLiked() {return liked;}



    public void setId(String id) {this.id = id;}

    public void setImageuri(String imageuri) { this.imageuri = imageuri;}
    public void setLiked(boolean liked) {this.liked = liked;}
}

