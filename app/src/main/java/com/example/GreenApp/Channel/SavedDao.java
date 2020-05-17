package com.example.GreenApp.Channel;

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