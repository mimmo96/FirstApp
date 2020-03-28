package com.example.firstapp.Channel;

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
public class Channel {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "id_key")
    private String id;

    @ColumnInfo(name = "read_key")
    private String read_key;

    @ColumnInfo(name = "position_key")
    private int position=0;
    private String filed1=null;
    private String filed2=null;
    private String filed3=null;
    private String filed4=null;
    private String filed5=null;
    private String filed6=null;
    private String filed7=null;
    private String filed8=null;
    private  Double tempMin=null;
    private  Double tempMax=null;
    private  Double umidMin=null;
    private  Double umidMax=null;
    private  Double condMin=null;
    private  Double condMax=null;
    private  Double phMin=null;
    private  Double phMax=null;
    private  Double irraMin=null;
    private  Double irraMax=null;
    private  Double pesMin=null;
    private  Double pesMax=null;

    public Channel(String id, String read_key) {
        this.id = id;
        this.read_key=read_key;
    }

    public String getFiled1() {
        return filed1;
    }

    public void setFiled1(String filed1) {
        this.filed1 = filed1;
    }

    public String getFiled2() {
        return filed2;
    }

    public void setFiled2(String filed2) {
        this.filed2 = filed2;
    }

    public String getFiled3() {
        return filed3;
    }

    public void setFiled3(String filed3) {
        this.filed3 = filed3;
    }

    public String getFiled4() {
        return filed4;
    }

    public void setFiled4(String filed4) {
        this.filed4 = filed4;
    }

    public String getFiled5() {
        return filed5;
    }

    public void setFiled5(String fild5) {
        this.filed5 = fild5;
    }

    public String getFiled6() {
        return filed6;
    }

    public void setFiled6(String filed6) {
        this.filed6 = filed6;
    }

    public String getFiled7() {
        return filed7;
    }

    public void setFiled7(String filed7) {
        this.filed7 = filed7;
    }

    public String getFiled8() {
        return filed8;
    }

    public void setFiled8(String filed8) {
        this.filed8 = filed8;
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

    public void setRead_key(String read_key) {
        this.read_key = read_key;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int pos) {
        this.position = pos;
    }

    public Double getTempMin() {
        return tempMin;
    }

    public void setTempMin(Double tempMin) {
        this.tempMin = tempMin;
    }

    public Double getTempMax() {
        return tempMax;
    }

    public void setTempMax(Double tempMax) {
        this.tempMax = tempMax;
    }

    public Double getUmidMin() {
        return umidMin;
    }

    public void setUmidMin(Double umidMin) {
        this.umidMin = umidMin;
    }

    public Double getUmidMax() {
        return umidMax;
    }

    public void setUmidMax(Double umidMax) {
        this.umidMax = umidMax;
    }

    public Double getCondMin() {
        return condMin;
    }

    public void setCondMin(Double condMin) {
        this.condMin = condMin;
    }

    public Double getCondMax() {
        return condMax;
    }

    public void setCondMax(Double condMax) {
        this.condMax = condMax;
    }

    public Double getPhMin() {
        return phMin;
    }

    public void setPhMin(Double phMin) {
        this.phMin = phMin;
    }

    public Double getPhMax() {
        return phMax;
    }

    public void setPhMax(Double phMax) {
        this.phMax = phMax;
    }

    public Double getIrraMin() {
        return irraMin;
    }

    public void setIrraMin(Double irraMin) {
        this.irraMin = irraMin;
    }

    public Double getIrraMax() {
        return irraMax;
    }

    public void setIrraMax(Double irraMax) {
        this.irraMax = irraMax;
    }

    public Double getPesMin() {
        return pesMin;
    }

    public void setPesMin(Double pesMin) {
        this.pesMin = pesMin;
    }

    public Double getPesMax() {
        return pesMax;
    }

    public void setPesMax(Double pesMax) {
        this.pesMax = pesMax;
    }
}
