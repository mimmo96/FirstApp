package com.example.firstapp.Alert;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.EditText;

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
    public static String url="https://api.thingspeak.com/channels/816869/feeds.json?api_key=KLEZNXOV7EPHHEUT&results=1";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //funzione che devo fare all'avvio
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MyTimerTask timerTask = new MyTimerTask(url, tempMin,tempMax, umidMin, umidMax, condMin, condMax,
                phMin, phMax, irraMin, irraMax, pesMin , pesMax);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 30000);
        AlertActivity.setTimer(timerTask,timer);

        return START_NOT_STICKY;
    }

    public static void setvalue(EditText tempMin1,EditText tempMax1,EditText umidMin1,EditText umidMax1,EditText condMin1,EditText condMax1,EditText phMin1,EditText phMax1,EditText irraMin1,
                                EditText irraMax1,EditText pesMin1,EditText pesMax1){
        tempMin=Double.parseDouble(tempMin1.getText().toString());
        tempMax=Double.parseDouble(tempMax1.getText().toString());
        umidMin=Double.parseDouble(umidMin1.getText().toString());
        umidMax=Double.parseDouble(umidMax1.getText().toString());
        condMin=Double.parseDouble(condMin1.getText().toString());
        condMax=Double.parseDouble(condMax1.getText().toString());
        phMin=Double.parseDouble(phMin1.getText().toString());
        phMax=Double.parseDouble(phMax1.getText().toString());
        irraMin=Double.parseDouble(irraMin1.getText().toString());
        irraMax=Double.parseDouble(irraMax1.getText().toString());
        pesMin=Double.parseDouble(pesMin1.getText().toString());
        pesMax=Double.parseDouble(pesMax1.getText().toString());
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
