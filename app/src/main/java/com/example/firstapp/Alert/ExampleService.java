package com.example.firstapp.Alert;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.room.Room;

import com.example.firstapp.AppDatabase;
import com.example.firstapp.Channel.Channel;

import java.util.List;
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
    private static Channel channel;
    private static AppDatabase database;
    private static TextView notification;
    private static Context context;
    private static String minutes=null;
    private static boolean go=false;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        Log.d("ExampleService","servizio background creato");
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "prodiction")
                //consente l'aggiunta di richieste nel thred principale
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                //build mi serve per costruire il tutto
                .build();
    }

    //funzione che devo fare all'avvio
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ExampleService","OnStartCommand");
            //recupero la lista del channel attuale
            String id = database.SavedDao().getAll().get(0).getId();
            String key = database.SavedDao().getAll().get(0).getRead_key();
            channel = database.ChannelDao().findByName(id, key);
        if(channel.getNotification()) {
            database.ChannelDao().delete(channel);
            if(minutes==null) minutes="";
            url = "https://api.thingspeak.com/channels/" + channel.getId() + "/feeds.json?api_key=" + channel.getRead_key() +"&minutes="+minutes +"&offset=2";
            channel.setTimerTask(new MyTimerTask(url, channel, temp, umid, ph, cond, irra, peso, context));
            timer = new Timer();
            timer.scheduleAtFixedRate(channel.getTimerTask(), 0, 3000);
            channel.setTimer(timer);
            database.ChannelDao().insert(channel);
        }
        else{
            this.onDestroy();
        }
        return START_STICKY;
    }

    public static void setvalue(TextView temp1, TextView umid1, TextView ph1, TextView cond1, TextView irra1, TextView peso1, Channel chan, AppDatabase db, TextView notifiche, String minutes1){
        database=db;
        minutes=minutes1;
        channel=chan;
        notification=notifiche;
        temp=temp1;
        umid=umid1;
        irra=irra1;
        peso=peso1;
        ph=ph1;
        cond=cond1;
    }

    public static void stoptimer(){
        if(channel!=null) {
            timer = channel.getTimer();
            timerTask = channel.getTimerTask();
            if (timer != null) {
                timer.cancel();
                timerTask.cancel();
                Channel x = database.ChannelDao().findByName(channel.getId(), channel.getRead_key());
                database.ChannelDao().delete(x);
                x.setNotification(false);
                x.setTimer(null);
                x.setTimerTask(null);
                database.ChannelDao().insert(x);
                notification.setText("notifiche non attive");
                Log.d("Background service", "Servizio interrotto!");
            }
        }
    }

    @Override
    public void onDestroy() { super.onDestroy(); stoptimer(); Log.d("ExampleServices","distruggo");}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
