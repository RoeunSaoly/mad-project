package com.example.mad_project.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BagItem bagItem);

    @Query("SELECT * FROM bag_items")
    List<BagItem> getAll();

    @Query("SELECT * FROM bag_items WHERE name = :name LIMIT 1")
    BagItem getBagItemByName(String name);

    @Update
    void update(BagItem bagItem);

    @Delete
    void delete(BagItem bagItem);
}