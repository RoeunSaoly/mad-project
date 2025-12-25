package com.example.mad_project.db;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface BagDao {
    @Insert
    void insert(BagItem cartItem);
}