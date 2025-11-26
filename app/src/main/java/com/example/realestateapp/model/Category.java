package com.example.realestateapp.model;

import java.io.Serializable;

public class Category implements Serializable {
    private int imageResId;
    private String title;

    public Category(int imageResId, String title) {
        this.imageResId = imageResId;
        this.title = title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }
}
