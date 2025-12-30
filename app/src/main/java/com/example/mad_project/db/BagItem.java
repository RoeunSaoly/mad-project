package com.example.mad_project.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bag_items")
public class BagItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public int price; // Price in cents
    public String imageUrl;
    public int amount;
}
