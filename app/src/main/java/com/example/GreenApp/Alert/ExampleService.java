package com.example.GreenApp.Alert;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.Room;

import com.example.GreenApp.Channel.Channel;
import com.example.GreenApp.AppDatabase;

import java.util.ArrayList;
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
    private static AppDatabase database;
    private static MyTimerTask myTimerTask;
    private static Timer timer;
    private static Context context;
    private static boolean isactive=false;

    @Override
    public void onCreate() {
        if(isactive) return;
        super.onCreate();
        context=getApplicationContext();

        Log.d("ExampleService","servizio background creato");
        //recupero il database dei channel
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
        int k=0;
            //recupero dalla lista tutti i channel e se ne esiste almeno uno con le notifiche attive lo eseguo in background
            List<Channel> allchannel= database.ChannelDao().getAll();
            List<Channel> channelNotification=new ArrayList<>();

            for(int i=0;i<allchannel.size();i++) {
                Channel actualchannel = allchannel.get(i);
                //se ho le notifiche abilitata lo avvio
                if (actualchannel.getNotification()) {
                    //aumento il numero di notifiche
                    k++;
                    //inserisco i channel in un array
                    channelNotification.add(actualchannel);
                    isactive=true;
                }
            }

            //se non ho nessun channel con le notifiche attive interrompo il servizo
            if(!isactive){
                Log.d("ExampleServices","nessun channel da avviare");
                return START_NOT_STICKY;
            }
            else{
                myTimerTask=new MyTimerTask(channelNotification,context,database);
                timer = new Timer();
                //ogni 10 minuti
                timer.scheduleAtFixedRate(myTimerTask, 0, 600000);
                Log.d("ExampleServices"," ho avviato: " +k +" notifiche");
            }

        return START_STICKY;
    }


    //quando devo distruggere il servizio
    public static void stoptimer(){
        //recupero il thread avviato precdentemente e li cancello
        if(myTimerTask!=null) myTimerTask.cancel();
        if(timer!=null) timer.cancel();
        myTimerTask=null;
        timer=null;
    }

    //nel caso in cui il service viene distrutto per cause di "forze maggiori"
    @Override
    public void onDestroy() {
        super.onDestroy();
        //elimito tutte le strutture precedentemente create
        stoptimer();
        Log.d("ExampleServices","distruggo");
        isactive=false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
