package com.example.firstapp.Alert;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.example.firstapp.AppDatabase;
import com.example.firstapp.Channel.Channel;
import com.example.firstapp.Channel.savedValues;
import com.example.firstapp.MainActivity;
import com.example.firstapp.R;

import java.util.List;

import static com.example.firstapp.Alert.App.CHANNEL_1_ID;

/*
 * Progetto: svilluppo App Android per Tirocinio interno
 *
 * Dipartimento di Informatica Università di Pisa
 *
 * Autore:Domenico Profumo matricola 533695
 * Si dichiara che il programma è in ogni sua parte, opera originale dell'autore
 *
 */


public class AlertActivity extends AppCompatActivity {
    private static NotificationManagerCompat notificationManager;
    private static Context cont;
    private EditText tempMin;
    private EditText tempMax;
    private EditText umidMin;
    private EditText umidMax;
    private EditText condMin;
    private EditText condMax;
    private EditText phMin;
    private EditText phMax;
    private EditText irraMin;
    private EditText irraMax;
    private EditText pesMin;
    private EditText pesMax;
    private static TextView temp;
    private static TextView umid;
    private static TextView ph;
    private static TextView cond;
    private static TextView irra;
    private static TextView peso;
    private static TextView notifiche;
    public static String url=null;
    private static Intent serviceIntent;
    private static Channel channel;             //channel usato
    private static AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_settings);

        cont=getApplication();
        notificationManager=NotificationManagerCompat.from(this);
        tempMin=findViewById(R.id.Tempmin);
        tempMax=findViewById(R.id.tempmax);
        umidMin=findViewById(R.id.umidmin);
        umidMax=findViewById(R.id.umidmax);
        condMin=findViewById(R.id.condmin);
        condMax=findViewById(R.id.condmax);
        phMin=findViewById(R.id.phmin);
        phMax=findViewById(R.id.phmax);
        irraMin=findViewById(R.id.irramin);
        irraMax=findViewById(R.id.irramax);
        pesMin=findViewById(R.id.pesomin);
        pesMax=findViewById(R.id.pesomax);
        temp=findViewById(R.id.textViewtemp);
        umid=findViewById(R.id.textViewUmid);
        ph=findViewById(R.id.textViewPh);
        cond=findViewById(R.id.textViewcond);
        irra=findViewById(R.id.textViewirra);
        peso=findViewById(R.id.textViewPes);
        notifiche=findViewById(R.id.textNotifiche);

        //ripristino i valori relativi al channel precedentemente salvati
        if (channel.getTempMin()!= null ) tempMin.setText(String.format(channel.getTempMin().toString()));
        if (channel.getTempMax()!=null) tempMax.setText(String.format(channel.getTempMax().toString()));
        if (channel.getUmidMin()!=null) umidMin.setText(String.format(channel.getUmidMin().toString()));
        if (channel.getUmidMax()!=null) umidMax.setText(String.format(channel.getUmidMax().toString()));
        if (channel.getCondMin()!=null) condMin.setText(String.format(channel.getCondMin().toString()));
        if (channel.getCondMax()!=null) condMax.setText(String.format(channel.getCondMax().toString()));
        if (channel.getPhMin()!=null) phMin.setText(String.format(channel.getPhMin().toString()));
        if (channel.getPhMax()!=null) phMax.setText(String.format(channel.getPhMax().toString()));
        if (channel.getIrraMin()!=null) irraMin.setText(String.format(channel.getIrraMin().toString()));
        if (channel.getIrraMax()!=null) irraMax.setText(String.format(channel.getIrraMax().toString()));
        if (channel.getPesMin()!=null) pesMin.setText(String.format(channel.getPesMin().toString()));
        if (channel.getPesMax()!=null) pesMax.setText(String.format(channel.getPesMax().toString()));
        if (channel.getNotification()) notifiche.setText("notifiche attive");
        else notifiche.setText("notifiche non attive");
      /*  if (channel.getTempMin()== null && channel.getTempMax()==null && channel.getUmidMin()==null && channel.getUmidMax()==null && channel.getCondMax()==null
                && channel.getPhMin()==null  && channel.getPhMax()==null && channel.getIrraMin()==null && channel.getIrraMax()==null
                && channel.getPesMin()==null && channel.getPesMax()==null){
            canstart=false;
        }
*/
        database = Room.databaseBuilder(this, AppDatabase.class, "prodiction")
                //consente l'aggiunta di richieste nel thred principale
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                //build mi serve per costruire il tutto
                .build();

        serviceIntent = new Intent(this, ExampleService.class);
    }

    public static Intent getActivityintent(Context context){
        Intent intent=new Intent(context, AlertActivity.class);
        return intent;
    }

    public void startButton(View v) {
        startService();
    }

    public void stopButton(View v) {
        stopService();
    }

    public void startService() {
            //se era stata già avviata fermo la precedente
            if (channel.getTimer()!=null ) {
                Log.d("TERMINO","sto terminando");
                stopService();
            }
            //se già non è stata avviata l'avvio ora
            ExampleService.setvalue(tempMin, tempMax, umidMin, umidMax, condMin, condMax, phMin, phMax,
                                    irraMin, irraMax, pesMin, pesMax, temp, umid, ph, cond, irra, peso,
                                    url, channel, database, notifiche);
            ContextCompat.startForegroundService(this, serviceIntent);
            notifiche.setText("notifiche attive");
    }

    //fatta quando richiamo il pulsante di stop
    public static void stopService() {
        ExampleService.stoptimer();
    }

    public static void setUrl(Channel chan){
        channel=chan;
        url = "https://api.thingspeak.com/channels/" + channel.getId() + "/feeds.json?api_key=" + channel.getRead_key() + "&results=1";
    }

    public static Context getContext(){
        return cont;
    }
/*
    public static void setvalues(String temp1,  String umid1, String ph1, String cond1, String irra1, String peso1){
        temp.setText(temp1);
        umid.setText(umid1);
        ph.setText(ph1);
        cond.setText(cond1);
        irra.setText(irra1);
        peso.setText(peso1);
    }
*/

}
