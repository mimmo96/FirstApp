package com.example.firstapp.Alert;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.firstapp.MainActivity;
import com.example.firstapp.R;
import java.util.Timer;

import static com.example.firstapp.Alert.App.CHANNEL_1_ID;


public class AlertActivity extends AppCompatActivity {
    private static NotificationManagerCompat notificationManager;
    private EditText editTextinput;
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
    private TextView temp;
    private TextView umid;
    private TextView ph;
    private TextView cond;
    private TextView irra;
    private TextView peso;
    private static MyTimerTask timerTask;
    private static Timer timer;
    private static Boolean go=false;

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
        Intent serviceIntent = new Intent(this, ExampleService.class);
        ExampleService.setvalue(tempMin, tempMax, umidMin, umidMax, condMin, condMax,
                phMin, phMax, irraMin, irraMax, pesMin, pesMax,temp,umid,ph,cond,irra,peso);
        go = true;
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public static Intent getActivityintent(Context context){
        Intent intent=new Intent(context, AlertActivity.class);
        return intent;
    }

    public void startService(View v) {
        //se già non è stata avviata l'avvio ora
        if(go) {
            Intent serviceIntent = new Intent(this, ExampleService.class);
            ExampleService.stoptimer();
            stopService(serviceIntent);
        }

        Intent serviceIntent = new Intent(this, ExampleService.class);
        ExampleService.setvalue(tempMin, tempMax, umidMin, umidMax, condMin, condMax,
                phMin, phMax, irraMin, irraMax, pesMin, pesMax,temp,umid,ph,cond,irra,peso);
        go = true;
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    //fatta quando richiamo il pulsante di stop
    public void stopService(View v) {
        Intent serviceIntent = new Intent(this, ExampleService.class);
        ExampleService.stoptimer();
        stopService(serviceIntent);
    }

    public static void printnotify(String text,int i){
        Intent notificationIntent = new Intent(cont, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(cont,0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(cont, CHANNEL_1_ID)
                .setContentTitle("Example Service")
                .setContentText(text)
                .setSmallIcon(R.drawable.pianta)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        notification.flags = Notification.FLAG_INSISTENT | Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(i,notification);
    }

    public static Context getContext(){
        return cont;
    }

    public static void setTimer(MyTimerTask myt,Timer tim){
        timerTask=myt;
        timer=tim;
    }


}
