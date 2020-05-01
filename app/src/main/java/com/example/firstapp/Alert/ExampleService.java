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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    private static ArrayList<MyTimerTask> timerTasks=new ArrayList<>();
    private static ArrayList<Timer> timer=new ArrayList<>();
    private static ArrayList<Channel> channelArrayList=new ArrayList<>();
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
        isactive=true;
    }

    //funzione che devo fare all'avvio
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ExampleService","OnStartCommand");
        //setto una variabile per capire se devo lanciare almeno un servizio oppure no
        int k=0;
            //recupero dalla lista tutti i channel e se ne esiste almeno uno con le notifiche attive lo eseguo in background
            List<Channel> allchannel= database.ChannelDao().getAll();
            for(int i=0;i<allchannel.size();i++) {
                Channel actualchannel = allchannel.get(i);
                //se ho le notifiche abilitata lo avvio
                if (actualchannel.getNotification()) {
                    isactive=true;
                    MyTimerTask myTimerTask1=new MyTimerTask(actualchannel,context,database);
                    timerTasks.add(myTimerTask1);
                    Timer timer1 = new Timer();
                    timer1.scheduleAtFixedRate(myTimerTask1, 0, 3000);
                    timer.add(timer1);
                    channelArrayList.add(actualchannel);
                }
                k++;
            }

            //se non ho nessun channel con le notifiche attive interrompo il servizo
            if(!isactive){
                Log.d("ExampleServices","nessun channel da avviare");
                return START_NOT_STICKY;
            }
            else Log.d("ExampleServices"," ho avviato: " +k +" notifiche");

        return START_STICKY;
    }


    //quando devo distruggere il servizio
    public static void stoptimer(){
        //recupero il thread avviato precdentemente e li cancello
        for(int i=0;i<timerTasks.size();i++){
            if(timerTasks.get(i)!=null) timerTasks.get(i).cancel();
            if(timer.get(i)!=null)   timer.get(i).cancel();
            timerTasks=null;
            timer=null;
            channelArrayList.remove(i);
        }
    }

    public static void stopNotification(Channel x){
        int k=-1;
        for(int i=0;i<channelArrayList.size();i++){
            if(x.getId().equals(channelArrayList.get(i).getId())){
                timerTasks.get(i).cancel();
                timer.get(i).cancel();
                k=i;
            }
        }
        //se ho rimosso il channel libero le strutture
        if(k!=-1){
            Log.d("ExampleService","notifiche disativate correttamente");
        }
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
