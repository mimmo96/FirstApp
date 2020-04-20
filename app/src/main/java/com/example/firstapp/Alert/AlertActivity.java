package com.example.firstapp.Alert;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.example.firstapp.AppDatabase;
import com.example.firstapp.Channel.Channel;
import com.example.firstapp.Channel.savedValues;
import com.example.firstapp.Irrigation.MyNewIntentService;
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
    private static EditText minutes;
    private static Intent serviceIntent;
    private static Channel channel;             //channel usato
    private static AppDatabase database;
    private static Switch aSwitch;

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
        minutes=findViewById(R.id.editTextMinuti);
        aSwitch=findViewById(R.id.switch2);

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
        if (channel.getLastimevalues()!=0) minutes.setText(String.valueOf(channel.getLastimevalues()));
        if (channel.getNotification()){
            notifiche.setText("notifiche attive");
            aSwitch.setChecked(true);
        }
        else{
            aSwitch.setChecked(false);
            notifiche.setText("notifiche non attive");
        }


        database = Room.databaseBuilder(this, AppDatabase.class, "prodiction")
                //consente l'aggiunta di richieste nel thred principale
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                //build mi serve per costruire il tutto
                .build();

        serviceIntent = new Intent(this, ExampleService.class);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //appena l'irrigazione è attiva
                if (isChecked){
                    Log.d("AlertActivity","attivo notifiche");

                    //abilito le notifiche
                    Channel x=database.ChannelDao().findByName(channel.getId(),channel.getRead_key());
                    database.ChannelDao().delete(x);
                    x.setNotification(true);
                    database.ChannelDao().insert(x);
                    channel=x;
                    startService();
                    notifiche.setText("notifiche attive");
                }
                else{
                    Log.d("AlertActivity","fermo notifiche");

                    //disabilito le notifiche
                    Channel x=database.ChannelDao().findByName(channel.getId(),channel.getRead_key());
                    database.ChannelDao().delete(x);
                    x.setNotification(false);
                    database.ChannelDao().insert(x);

                    notifiche.setText("notifiche non attive");

                    stopService();
                }
            }
        });

    }

    public static Intent getActivityintent(Context context){
        Intent intent=new Intent(context, AlertActivity.class);
        return intent;
    }

    //se premo il pulsante save
    public void saveButton(View v) {
        Channel x = database.ChannelDao().findByName(channel.getId(), channel.getRead_key());

        if (x != null) {
            database.ChannelDao().delete(x);
            try {
                x.setTempMin(Double.valueOf(tempMin.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                x.setTempMax(Double.valueOf(tempMax.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                x.setUmidMin(Double.valueOf(umidMin.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                x.setUmidMax(Double.valueOf(umidMax.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                x.setCondMin(Double.valueOf(condMin.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                x.setCondMax(Double.valueOf(condMax.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                x.setPhMin(Double.valueOf(phMin.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                x.setPhMax(Double.valueOf(phMax.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                x.setIrraMin(Double.valueOf(irraMin.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                x.setIrraMax(Double.valueOf(irraMax.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                x.setPesMin(Double.valueOf(pesMin.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                x.setPesMax(Double.valueOf(pesMax.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                x.setLastimevalues(Integer.valueOf(minutes.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            database.ChannelDao().insert(x);
            Toast.makeText(cont,"VALORI SALVATI CORRETTAMENTE!",Toast.LENGTH_SHORT).show();
        }
    }

    //se premo il pulsante reset
    public void resetButton (View v) {
            Channel x=database.ChannelDao().findByName(channel.getId(),channel.getRead_key());
            database.ChannelDao().delete(x);
            x.setTempMin(null);
            x.setTempMax(null);
            x.setUmidMin(null);
            x.setUmidMax(null);
            x.setCondMin(null);
            x.setCondMax(null);
            x.setPhMin(null);
            x.setPhMax(null);
            x.setIrraMin(null);
            x.setIrraMax(null);
            x.setPesMin(null);
            x.setPesMax(null);
            x.setLastimevalues(0);
            database.ChannelDao().insert(x);

            //resetto i valori anche nei text
            tempMin.setText(" ");
            tempMax.setText(" ");
            umidMin.setText(" ");
            umidMax.setText(" ");
            condMin.setText(" ");
            condMax.setText(" ");
            phMin.setText(" ");
            phMax.setText(" ");
            irraMin.setText(" ");
            irraMax.setText(" ");
            pesMin.setText(" ");
            pesMax.setText(" ");
            minutes.setText(" ");

            if (channel.getLastimevalues()!=0) minutes.setText(String.valueOf(channel.getLastimevalues()));
            Toast.makeText(cont,"VALORI RESETTATI CORRETTAMENTE",Toast.LENGTH_SHORT).show();
    }

    //funzione eseguita quando attivo le notifiche
    public void startService() {
            //se era stata già avviata fermo la precedente
            if (channel.getTimer()!=null ) {
                Log.d("TERMINO","sto terminando");
                stopService();
            }

            //converto i minuti in Stringa;
            String min=minutes.getText().toString();

            //setto i parametri da me impostati al service
            ExampleService.setvalue(temp, umid, ph, cond, irra, peso, channel, database, notifiche,min);
            ContextCompat.startForegroundService(this, serviceIntent);
    }

    //fatta quando richiamo il pulsante di stop
    public static void stopService() {
        ExampleService.stoptimer();
    }

    public static void setChannel(Channel chan){
        channel=chan;
    }

    public static Context getContext(){
        return cont;
    }

}
