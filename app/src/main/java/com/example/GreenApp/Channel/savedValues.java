package com.example.GreenApp.Channel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/*
 * Progetto: svilluppo App Android per Tirocinio interno
 *
 * Dipartimento di Informatica Università di Pisa
 *
 * Autore:Domenico Profumo matricola 533695
 * Si dichiara che il programma è in ogni sua parte, opera originale dell'autore
 *
 */

@Entity
public class savedValues {
        @PrimaryKey(autoGenerate = true)
        private int uid;

        @ColumnInfo(name = "id_key")
        private String id;

        @ColumnInfo(name = "read_key")
        private String read_key;


        @ColumnInfo(name = "position_key")
        private int position;

        public savedValues(String id,String read_key,int position) {
            this.id = id;
            this.read_key=read_key;
            this.position=position;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRead_key() {
        return read_key;
    }

        public int getPosition() {
            return position;
        }

        public void setPosition(int pos) {
            this.position = pos;
        }

    }

