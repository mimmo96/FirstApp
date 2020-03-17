package com.example.firstapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.firstapp.Channel.Channel;
import com.example.firstapp.Channel.ChannelDao;
import com.example.firstapp.Channel.SavedDao;
import com.example.firstapp.Channel.savedValues;


@Database(entities = {Channel.class, savedValues.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ChannelDao ChannelDao();
    public abstract SavedDao SavedDao();
}
