package com.example.mad_project.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BagDao {
    @Insert
    void insert(BagItem bagItem);

    @Query("SELECT * FROM bag_items")
    List<BagItem> getAll();

    @Update
    void update(BagItem bagItem);

    @Delete
    void delete(BagItem bagItem);
}