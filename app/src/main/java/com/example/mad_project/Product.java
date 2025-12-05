package com.example.mad_project;

import com.google.firebase.firestore.Exclude;

import java.util.List;

// FINAL, FORCED UPDATE (v3): The overloaded setPrice(String) method, which was the direct cause of the crash,
// has been definitively removed. This comment forces a re-compile.
public class Product {
    @Exclude
    private String id; // To store the document ID

    private String name;
    private String description; // For the subtitle
    private double price;
    private List<String> images;

    @Exclude
    private boolean isFavorited = false; // To track favorite status

    // Required empty public constructor for Firestore
    public Product() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    // This is the ONLY setter for price. The overloaded String setter was removed.
    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    // Helper to get the first image URL
    public String getFirstImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }
}
