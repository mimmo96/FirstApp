package com.example.firstapp.Channel;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/*
 * Progetto: svilluppo App Android per Tirocinio interno
 *
 * Dipartimento di Informatica Università di Pisa
 *
 * Autore:Domenico Profumo matricola 533695
 * Si dichiara che il programma è in ogni sua parte, opera originale dell'autore
 *
 */

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