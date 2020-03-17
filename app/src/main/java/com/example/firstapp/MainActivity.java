package com.example.firstapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.example.firstapp.Channel.ChannelActivity;
import com.example.firstapp.Channel.savedValues;


public class MainActivity extends AppCompatActivity {

    TextView textTemp;
    TextView textUmidity;
    TextView textPh;
    TextView textConducibilita;
    TextView textIrradianza;
    TextView textPeso;
    TextView textStato;
    TextView testo1;
    private static  List<savedValues> channeldefault;
    private static String channelID="816869";
    private static String READ_KEY="KLEZNXOV7EPHHEUT";
    private static  AppDatabase database;
    Context cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ripristino valori salvati precedentemente se ci sono
        BackupValues(savedInstanceState);

        textTemp=findViewById(R.id.textTemp);
        textUmidity=findViewById(R.id.textUmidity);
        textPh=findViewById(R.id.textPh);
        textConducibilita=findViewById(R.id.textConducibility);
        textIrradianza=findViewById(R.id.textIrradiance);
        textPeso=findViewById(R.id.textPeso);
        textStato=findViewById(R.id.textViewON);

        testo1=findViewById(R.id.textView1);

        String url="https://api.thingspeak.com/channels/"+channelID+ "/feeds.json?api_key=" + READ_KEY + "&results=1";
        cont=getApplicationContext();

        TimerTask timerTask=new MyTimerTask(url,textTemp,textUmidity,textPh,textConducibilita,textIrradianza,textPeso,textStato,testo1,cont);
        Timer timer=new Timer(true);
        timer.scheduleAtFixedRate(timerTask,0,3000);
    }

    private void BackupValues(Bundle savedInstanceState) {
        //creo il database

        if(savedInstanceState==null) {
            database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "prodiction")
                    //consente l'aggiunta di richieste nel thred principale
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    //build mi serve per costruire il tutto
                    .build();
                channeldefault = database.SavedDao().getAll();

            if(channeldefault.size()>0){
                channelID=channeldefault.get(0).getId();
                READ_KEY=channeldefault.get(0).getKey();
                ChannelActivity.setPosition(channeldefault.get(0).getPosition());
            }

        }
    }

    //settting Graph
    public void doAdd(View v) {
        final boolean[] checkedItems;
        final String[] listItems;
        final ArrayList<Integer> mUserItems = new ArrayList<>();
        final ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> name = new ArrayList<String>();

        list.add("temperature");
        list.add("umidità");
        list.add("ph");
        list.add("conducibilità elettrica");
        list.add("irradianza");
        list.add("peso");

        listItems= list.toArray(new String[list.size()]);
        checkedItems = new boolean[list.size()];

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Seleziona i tipi di grafici");
        mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                //salvo la selezione
                if (isChecked) {
                    mUserItems.add(position);
                    name.add(list.get(position));
                } else {
                    mUserItems.remove((Integer.valueOf(position)));
                }
            }
        });

        mBuilder.setCancelable(false);
        //azione da svolgere quando premo sul pulsante visualizza
        mBuilder.setPositiveButton("VISUALIZZA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Intent intent = com.example.firstapp.Graphic.MainActivity.getActivityintent(MainActivity.this);
                com.example.firstapp.Graphic.MainActivity.setGrapView(name,channelID,READ_KEY);
                startActivity(intent);
            }
        });

        //azione da svolgere quando premo sul pulsante cancella tutto
        mBuilder.setNeutralButton("Cancella tutto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //elimino tutte le selezioni
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    mUserItems.clear();
                    name.clear();

                }
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void settingChannel(View v) {
        Intent intent = ChannelActivity.getActivityintent(MainActivity.this);
        startActivity(intent);
    }

    public static void setDefaultSetting(String id, String key, int pos) {
        System.out.println("SET DEFAULT SETTING");
        //aggiungo alla lista channeldefault il nuovo
           if(channeldefault.size()!=0)channeldefault.clear();
           channeldefault.add(new savedValues(id,key,pos));
           database.SavedDao().deleteAll();
           database.SavedDao().insert(new savedValues(id,key,pos));
            List<savedValues> x=database.SavedDao().getAll();

            for(int i=0;i<x.size();i++){
                    System.out.println(i+": " + x.get(i).getPosition());
            }
        System.out.println("FINE");

        channelID=id;
        READ_KEY=key;

    }



}
