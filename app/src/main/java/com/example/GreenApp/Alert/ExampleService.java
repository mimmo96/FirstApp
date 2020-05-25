package com.example.GreenApp.Alert;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.example.GreenApp.Channel.Channel;
import com.example.GreenApp.AppDatabase;
import com.example.GreenApp.MainActivity;
import com.example.firstapp.R;

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


    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();

        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel"+ 12345)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("green App")
                    .setContentText("Notifiche attive");

            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
            startForeground(2111, new Notification());
        }

        Log.d("ExampleService","servizio background creato");
        //recupero il database dei channel
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "prodiction")
                //consente l'aggiunta di richieste nel thred principale
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                //build mi serve per costruire il tutto
                .build();
    }

    @NonNull
    @TargetApi(26)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.firstApp";

        String channelName = "Green App background service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notifiche attive")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2111, notification);
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

        //faccio una scansione di tutti i canali per vedere se c'è qualcuno con le notifiche attive
        for(int i=0;i<allchannel.size();i++) {
            Channel actualchannel = allchannel.get(i);
            //se ho le notifiche abilitata lo avvio
            if (actualchannel.getNotification()) {
                //aumento il numero di notifiche
                k++;
                //inserisco i channel in un array
                channelNotification.add(actualchannel);
            }
        }

        if(k==0)
            //se non ho nessun channel con le notifiche attive interrompo il servizo
            stopForeground(true);
        else {
            onCreate();
            myTimerTask = new MyTimerTask(channelNotification, context, database);
            timer = new Timer();
            //ogni 10 minuti
            timer.scheduleAtFixedRate(myTimerTask, 0, 600000);
            Log.d("ExampleServices", " ho avviato: " + k + " notifiche");
        }
        return START_NOT_STICKY;
    }

    //quando devo distruggere il servizio
    public static void stoptimer(){
        //recupero i thread avviati precdentemente e li cancello
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
        stopForeground(true);
        Log.d("ExampleServices","distruggo");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}