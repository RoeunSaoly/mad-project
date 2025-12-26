package com.example.mad_project.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bag_items")
public class BagItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String price;
    public String imageUrl;
    public int amount;
}