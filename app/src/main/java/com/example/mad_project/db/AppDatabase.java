package com.example.mad_project.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {BagItem.class, FavoriteItem.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BagDao bagDao();
    public abstract FavoriteDao favoriteDao();
}