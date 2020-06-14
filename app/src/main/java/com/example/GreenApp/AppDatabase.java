package com.example.GreenApp;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.GreenApp.Channel.Channel;
import com.example.GreenApp.Channel.ChannelDao;
import com.example.GreenApp.Channel.SavedDao;
import com.example.GreenApp.Channel.savedValues;

/*
 * Progetto: svilluppo App Android per Tirocinio interno
 *
 * Dipartimento di Informatica Università di Pisa
 *
 * Autore:Domenico Profumo matricola 533695
 * Si dichiara che il programma è in ogni sua parte, opera originale dell'autore
 */

/**
 * dichiarazione del database con le due classi associate (channel,savedvalues)
 */
@Database(entities = {Channel.class, savedValues.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ChannelDao ChannelDao();
    public abstract SavedDao SavedDao();
}
