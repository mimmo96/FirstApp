package com.example.firstapp.Channel;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.firstapp.Channel.Channel;

import java.util.List;


@Dao
public interface SavedDao {
    @Query("SELECT * FROM savedValues")
    List<savedValues> getAll();

    @Insert
    void insert(savedValues savedValues);

    @Query("DELETE FROM savedValues")
    void deleteAll();

    @Delete
    void delete(savedValues savedValues);
}