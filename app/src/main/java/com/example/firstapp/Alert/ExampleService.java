package com.example.firstapp.Alert;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Timer;

/*
 * Progetto: svilluppo App Android per Tirocinio interno
 *
 * Dipartimento di Informatica Università di Pisa
 *
 * Autore:Domenico Profumo matricola 533695
 * Si dichiara che il programma è in ogni sua parte, opera originale dell'autore
 *
 */

public class ExampleService extends Service {

    private static Notification notification;
    private static Double tempMin;
    private static Double tempMax;
    private static Double umidMin;
    private static Double umidMax;
    private static Double condMin;
    private static Double condMax;
    private static Double phMin;
    private static Double phMax;
    private static Double irraMin;
    private static Double irraMax;
    private static Double pesMin;
    private static Double pesMax;
    private static TextView temp;
    private static TextView umid;
    private static TextView ph;
    private static TextView cond;
    private static TextView irra;
    private static TextView peso;
    private static String url=null;
    private static MyTimerTask timerTask=null;
    private static Timer timer=null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //funzione che devo fare all'avvio
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        timerTask = new MyTimerTask(url, tempMin,tempMax, umidMin, umidMax, condMin, condMax,
                phMin, phMax, irraMin, irraMax, pesMin , pesMax, temp,
                umid,  ph,  cond,  irra,  peso);
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 3000);

        return START_REDELIVER_INTENT;
    }

    public static void setvalue(EditText tempMin1,EditText tempMax1,EditText umidMin1,EditText umidMax1,EditText condMin1,EditText condMax1,EditText phMin1,EditText phMax1,EditText irraMin1,
                                EditText irraMax1,EditText pesMin1,EditText pesMax1,TextView temp1,
                                TextView umid1, TextView ph1, TextView cond1, TextView irra1, TextView peso1,String url1){
        url=url1;
        try {
        tempMin = Double.valueOf(tempMin1.getText().toString());
        } catch (NumberFormatException e) {
            tempMin = null;
        }
        try {
            tempMax = Double.valueOf(tempMax1.getText().toString());
        } catch (NumberFormatException e) {
            tempMax = null;
        }
        try {
            umidMin = Double.valueOf(umidMin1.getText().toString());
        } catch (NumberFormatException e) {
            umidMin= null;
        }
        try {
            umidMax = Double.valueOf(umidMax1.getText().toString());
        } catch (NumberFormatException e) {
            umidMax = null;
        }
        try {
            condMin = Double.valueOf(condMin1.getText().toString());
        } catch (NumberFormatException e) {
            condMin = null;
        }
        try {
            condMax = Double.valueOf(condMax1.getText().toString());
        } catch (NumberFormatException e) {
            condMax = null;
        } try {
            phMin = Double.valueOf(phMin1.getText().toString());
        } catch (NumberFormatException e) {
            phMin = null;
        }try {
            phMax = Double.valueOf(phMax1.getText().toString());
        } catch (NumberFormatException e) {
            phMax = null;
        }try {
            irraMin = Double.valueOf(irraMin1.getText().toString());
        } catch (NumberFormatException e) {
            irraMin = null;
        }try {
            irraMax = Double.valueOf(irraMax1.getText().toString());
        } catch (NumberFormatException e) {
            irraMax = null;
        }try {
            pesMin = Double.valueOf(pesMin1.getText().toString());
        } catch (NumberFormatException e) {
            pesMin = null;
        }try {
            pesMax = Double.valueOf(pesMax1.getText().toString());
        } catch (NumberFormatException e) {
            pesMax = null;
        }

        temp=temp1;
        umid=umid1;
        irra=irra1;
        peso=peso1;
        ph=ph1;
        cond=cond1;
    }

    public static void stoptimer(){
        if(timer!=null) timer.cancel();
        if(timerTask!=null) timerTask.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
