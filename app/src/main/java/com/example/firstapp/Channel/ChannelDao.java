package com.example.firstapp.Channel;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;


@Dao
public interface ChannelDao {
    @Query("SELECT * FROM channel")
    List<Channel> getAll();

    @Query("SELECT * FROM channel WHERE id_key LIKE :first AND "
            + "read_key LIKE :last LIMIT 1")
    Channel findByName(String first, String last);

    @Insert
    void insertAll(Channel... channels);

    @Insert
    void insert(Channel channel);

    @Query("DELETE FROM Channel")
    void deleteAll();

    @Delete
    void delete(Channel channel);
}