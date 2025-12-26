package com.example.mad_project.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {BagItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BagDao bagDao();
}