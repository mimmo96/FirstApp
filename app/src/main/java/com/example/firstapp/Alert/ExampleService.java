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
        //setto una variabile per capire se devo lanciare almeno un servizio oppure no
        Boolean ok=false;

            //recupero dalla lista tutti i channel e se hanno le notifiche attive li eseguo in background
            List<Channel> allchannel= database.ChannelDao().getAll();

            for(int i=0;i<allchannel.size();i++) {
                Channel actualchannel = allchannel.get(i);
                //se ho le notifiche abilitata lo avvio
                if (actualchannel.getNotification()) {
                    database.ChannelDao().delete(actualchannel);
                    int minuti= actualchannel.getLastimevalues();
                    if(minuti==0) minuti=60;
                    url = "https://api.thingspeak.com/channels/" + actualchannel.getId() + "/feeds.json?api_key=" + actualchannel.getRead_key() + "&minutes=" +
                           minuti + "&offset=2";
                    actualchannel.setTimerTask(new MyTimerTask(url, channel, temp, umid, ph, cond, irra, peso, context));
                    timer = new Timer();
                    timer.scheduleAtFixedRate(actualchannel.getTimerTask(), 0, 3000);
                    actualchannel.setTimer(timer);
                    database.ChannelDao().insert(actualchannel);
                    ok=true;
                }
            }
            //se non ho nessun channel con le notifiche abilitate interrompo il servizio
            if(!ok) this.onDestroy();
        return START_STICKY;
    }

    //setto i valori
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

    //quando disattivo la ricezione delle notifiche
    public static void stoptimer(){

        //recupero i dati del channel e disattivo
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
