package com.example.firstapp.Alert;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Timer;

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
    public static String url="https://api.thingspeak.com/channels/816869/feeds.json?api_key=KLEZNXOV7EPHHEUT&results=1";
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
        timer.scheduleAtFixedRate(timerTask, 0, 30000);

        return START_NOT_STICKY;
    }

    public static void setvalue(EditText tempMin1,EditText tempMax1,EditText umidMin1,EditText umidMax1,EditText condMin1,EditText condMax1,EditText phMin1,EditText phMax1,EditText irraMin1,
                                EditText irraMax1,EditText pesMin1,EditText pesMax1,TextView temp1,
                                TextView umid1, TextView ph1, TextView cond1, TextView irra1, TextView peso1){
        try {
        tempMin = new Double(tempMin1.getText().toString());
        } catch (NumberFormatException e) {
            tempMin = 0.0; // your default value
        }
        try {
            tempMax = new Double(tempMax1.getText().toString());
        } catch (NumberFormatException e) {
            tempMax = 0.0; // your default value
        }
        try {
            umidMin = new Double(umidMin1.getText().toString());
        } catch (NumberFormatException e) {
            umidMin = 0.0; // your default value
        }
        try {
            umidMax = new Double(umidMax1.getText().toString());
        } catch (NumberFormatException e) {
            umidMax = 0.0; // your default value
        }
        try {
            condMin = new Double(condMin1.getText().toString());
        } catch (NumberFormatException e) {
            condMin = 0.0; // your default value
        }
        try {
            condMax = new Double(condMax1.getText().toString());
        } catch (NumberFormatException e) {
            condMax = 0.0; // your default value
        } try {
            phMin = new Double(phMin1.getText().toString());
        } catch (NumberFormatException e) {
            phMin = 0.0; // your default value
        }try {
            phMax = new Double(phMax1.getText().toString());
        } catch (NumberFormatException e) {
            phMax = 0.0; // your default value
        }try {
            irraMin = new Double(irraMin1.getText().toString());
        } catch (NumberFormatException e) {
            irraMin = 0.0; // your default value
        }try {
            irraMax = new Double(irraMax1.getText().toString());
        } catch (NumberFormatException e) {
            irraMax = 0.0; // your default value
        }try {
            pesMin = new Double(pesMin1.getText().toString());
        } catch (NumberFormatException e) {
            pesMin = 0.0; // your default value
        }try {
            pesMax = new Double(pesMax1.getText().toString());
        } catch (NumberFormatException e) {
            pesMax = 0.0; // your default value
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
