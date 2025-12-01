package com.example.mad_project;

public class Shirt {
    private final String name;
    private final int price;
    private final int imageResId;

    public Shirt(String name, int price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getImageResId() { return imageResId; }
}
