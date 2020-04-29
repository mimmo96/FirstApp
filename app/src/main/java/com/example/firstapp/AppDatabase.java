package com.example.firstapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.firstapp.Channel.Channel;
import com.example.firstapp.Channel.ChannelDao;
import com.example.firstapp.Channel.SavedDao;
import com.example.firstapp.Channel.savedValues;

/*
 * Progetto: svilluppo App Android per Tirocinio interno
 *
 * Dipartimento di Informatica Università di Pisa
 *
 * Autore:Domenico Profumo matricola 533695
 * Si dichiara che il programma è in ogni sua parte, opera originale dell'autore
 *
 */

@Database(entities = {Channel.class, savedValues.class}, version = 2,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ChannelDao ChannelDao();
    public abstract SavedDao SavedDao();
}
