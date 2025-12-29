package com.example.mad_project.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert
    void insert(FavoriteItem favoriteItem);

    @Delete
    void delete(FavoriteItem favoriteItem);

    @Query("SELECT * FROM favorite_items")
    List<FavoriteItem> getAll();

    @Query("SELECT * FROM favorite_items WHERE productId = :productId")
    FavoriteItem getFavoriteById(String productId);
}
