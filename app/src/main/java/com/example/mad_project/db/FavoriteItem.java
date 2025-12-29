package com.example.mad_project.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "favorite_items")
public class FavoriteItem {
    @PrimaryKey
    @NonNull
    public String productId;

    public FavoriteItem(@NonNull String productId) {
        this.productId = productId;
    }
}
