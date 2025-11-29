package com.example.mad_project;

import java.util.List;

public class Product {
    private String name;
    private double price;
    private List<String> images;

    // Required empty public constructor for Firestore
    public Product() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    // Helper to get the first image URL
    public String getFirstImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }
}
