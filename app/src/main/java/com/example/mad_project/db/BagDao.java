package com.example.mad_project.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BagDao {
    @Insert
    void insert(BagItem bagItem);

@Query("SELECT * FROM bag_items")
List<BagItem> getAll();
}