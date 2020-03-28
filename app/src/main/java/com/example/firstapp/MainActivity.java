package com.example.firstapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.firstapp.Alert.AlertActivity;
import com.example.firstapp.Channel.Channel;
import com.example.firstapp.Channel.ChannelActivity;
import com.example.firstapp.Channel.savedValues;

/*
 * Progetto: svilluppo App Android per Tirocinio interno
 *
 * Dipartimento di Informatica Università di Pisa
 *
 * Autore:Domenico Profumo matricola 533695
 * Si dichiara che il programma è in ogni sua parte, opera originale dell'autore
 *
 */

public class MainActivity extends AppCompatActivity {

    public static TextView textTemp;
    public static TextView textUmidity;
    public static TextView textPh;
    public static TextView textConducibilita;
    public static TextView textIrradianza;
    public static TextView textPeso;
    public static TextView textStato;
    public static TextView testo1;
    private static List<savedValues> channeldefault;
    private static String channelID = null;
    private static String READ_KEY = null;
    private static String url;
    private static AppDatabase database;
    private static TimerTask timerTask;
    private static Timer timer;
    private static Context cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ripristino valori salvati precedentemente se ci sono
        BackupValues(savedInstanceState);

        textTemp = findViewById(R.id.textTemp);
        textUmidity = findViewById(R.id.textUmidity);
        textPh = findViewById(R.id.textPh);
        textConducibilita = findViewById(R.id.textConducibility);
        textIrradianza = findViewById(R.id.textIrradiance);
        textPeso = findViewById(R.id.textPeso);
        textStato = findViewById(R.id.textViewON);
        testo1 = findViewById(R.id.textView1);
        cont = getApplicationContext();

        //controllo se ho almeno un chnnel inserito
        if (url == null) {
            textTemp.setText("- -");
            textUmidity.setText("- -");
            textPh.setText("- -");
            textConducibilita.setText("- -");
            textIrradianza.setText("- -");
            textPeso.setText("- -");
            testo1.setText("INSERISCI UN NUOVO CHANNEL");
        } else {
            startTimer(cont);
        }

       // stampa();
    }

    private void BackupValues(Bundle savedInstanceState) {
        //creo il database

        if (savedInstanceState == null) {
            database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "prodiction")
                    //consente l'aggiunta di richieste nel thred principale
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    //build mi serve per costruire il tutto
                    .build();
            channeldefault = database.SavedDao().getAll();
            //controllo se ho almeno un elemento inserito
            if (channeldefault.size() > 0) {
                //se avevo un elmento inserito imposto quest'ultimo come default
                channelID = channeldefault.get(0).getId();
                READ_KEY = channeldefault.get(0).getKey();
                url = "https://api.thingspeak.com/channels/" + channelID + "/feeds.json?api_key=" + READ_KEY + "&results=1";
                ChannelActivity.setPosition(channeldefault.get(0).getPosition());
            }
            //se non ho nessun elemento inserito setto a null i valori dei channel
            else {
                channelID = null;
                READ_KEY = null;
                url = null;
                ChannelActivity.setPosition(-1);
            }
        }
    }

    //settting Graph
    public void doAdd(View v) {
        final boolean[] checkedItems;
        final String[] listItems;

        //lista che mi setta la posizione degli elementi selezionati
        final ArrayList<Integer> mUserItems = new ArrayList<>();
        final ArrayList<String> list = new ArrayList<>();
        final List<Channel> allchannel=database.ChannelDao().getAll();
        //memorizza il channel
        final List<Channel> selectedChannel=new ArrayList<>();

        //lista che mi salva il nome di tutti gli elementi selezionati e la posizione di essi
        final ArrayList<String> name = new ArrayList<>();
        final ArrayList<Integer> posField = new ArrayList<>();

        //scandisco tutti i channel per trovare il primo
        for(int i=0;i<allchannel.size();i++) {
            Channel inUse = allchannel.get(i);
            System.out.println("ho premuto:" + inUse.getFiled1());
            if (inUse.getFiled1() != null){
                list.add(inUse.getFiled1().concat(" (id:").concat(inUse.getId()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(1);
            }
            if (inUse.getFiled2() != null){
                list.add(inUse.getFiled2().concat(" (id:").concat(inUse.getId()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(2);
            }
            if (inUse.getFiled3() != null) {
                list.add(inUse.getFiled3().concat(" (id:").concat(inUse.getId()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(3);
            }
            if (inUse.getFiled4() != null){
                list.add(inUse.getFiled4().concat(" (id:").concat(inUse.getId()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(4);
            }
            if (inUse.getFiled5() != null){
                list.add(inUse.getFiled5().concat(" (id:").concat(inUse.getId()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(5);
            }
            if (inUse.getFiled6() != null){
                list.add(inUse.getFiled6().concat(" (id:").concat(inUse.getId()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(6);
            }
            if (inUse.getFiled7() != null){
                list.add(inUse.getFiled7().concat(" (id:").concat(inUse.getId()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(7);
            }
            if (inUse.getFiled8() != null){
                list.add(inUse.getFiled8().concat(" (id:").concat(inUse.getId()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(8);
            }
        }

        listItems = list.toArray(new String[list.size()]);
        checkedItems = new boolean[list.size()];

        if (list.size() == 0)
            Toast.makeText(cont, "INSERISCI UN CHANNEL!", Toast.LENGTH_SHORT).show();
        else {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            mBuilder.setTitle("Seleziona i tipi di grafici");
            final ArrayList<Integer> selectedPos=new ArrayList<>();
            final ArrayList<Channel> selChan=new ArrayList<>();
            mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                    //salvo la selezione
                    if (isChecked) {
                        // mUserItems.add(position);
                        name.add(list.get(position).substring(0,list.get(position).indexOf("(")));
                        selectedPos.add(posField.get(position));
                        selChan.add(selectedChannel.get(position));
                    } else {
                        // mUserItems.remove((Integer.valueOf(position)));
                        name.remove(list.get(position).substring(0,list.get(position).indexOf("(")));
                        selectedPos.remove(posField.get(position));
                        selChan.remove(selectedChannel.get(position));
                    }
                }
            });

            mBuilder.setCancelable(false);
            //azione da svolgere quando premo sul pulsante visualizza
            mBuilder.setPositiveButton("VISUALIZZA", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {

                    if (name.size() == 0)
                        Toast.makeText(cont, "NESSUN GRAFICO SELEZIONATO!", Toast.LENGTH_SHORT).show();
                    else {
                        Intent intent = com.example.firstapp.Graphic.MainActivity.getActivityintent(MainActivity.this);
                        com.example.firstapp.Graphic.MainActivity.setGrapView(name, selChan,selectedPos);
                        startActivity(intent);
                    }
                }
            });

            //azione da svolgere quando premo sul pulsante cancella tutto
            mBuilder.setNeutralButton("ANNULLA", new DialogInterface.OnClickListener() {
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

    }

    //azione che devo eseguirequando premo il puksante impostazioni
    public void settingChannel(View v) {
        Intent intent = ChannelActivity.getActivityintent(MainActivity.this);
        startActivity(intent);
    }

    //azione che devo eseguirequando premo il puksante attenzione
    public void notifiche(View v) {
        Intent intent = AlertActivity.getActivityintent(MainActivity.this);

        List<Channel> channeList=database.ChannelDao().getAll();
        Channel trovato=null;
        for(int i=0;i<channeList.size();i++){
            if(channeldefault.get(0).getId().equals(channeList.get(i).getId())){
                trovato=channeList.get(i);
            }
        }
        AlertActivity.setUrl(trovato);
        startActivity(intent);
    }

    public static void setDefaultSetting(String id, String key, int pos) {
        System.out.println("SET DEFAULT SETTING");
        if (pos == -1) {
            channelID = null;
            READ_KEY = null;
            url = null;
            ChannelActivity.setPosition(-1);

            if (channeldefault.size() != 0) channeldefault.clear();
            //channeldefault.add(new savedValues(id, key, pos));
            database.SavedDao().deleteAll();

            textTemp.setText("- -");
            textUmidity.setText("- -");
            textPh.setText("- -");
            textConducibilita.setText("- -");
            textIrradianza.setText("- -");
            textPeso.setText("- -");

            textStato.setText("OFFLINE");
            textStato.setTextColor(Color.RED);
            testo1.setText("INSERISCI UN NUOVO CHANNEL");
            timer.cancel();
            timerTask.cancel();
        } else {
            //aggiungo alla lista channel default il nuovo
            if (channeldefault.size() != 0) channeldefault.clear();
            channeldefault.add(new savedValues(id, key, pos));
            database.SavedDao().deleteAll();
            database.SavedDao().insert(new savedValues(id, key, pos));
            List<savedValues> x = database.SavedDao().getAll();

            for (int i = 0; i < x.size(); i++) {
                System.out.println(i + ": " + x.get(i).getPosition());
            }
            System.out.println("FINE");

            channelID = id;
            READ_KEY = key;
            url = "https://api.thingspeak.com/channels/" + channelID + "/feeds.json?api_key=" + READ_KEY + "&results=1";
            restartTimer(cont);
        }

    }

    public static void restartTimer(Context cont) {

        if (timer != null) timer.cancel();
        if (timerTask != null) timerTask.cancel();
        timerTask = new MyTimerTask(channelID, READ_KEY, url, textTemp, textUmidity, textPh, textConducibilita, textIrradianza, textPeso, textStato, testo1, cont, database);
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 3000);
    }

    public static void startTimer(Context cont) {
        timerTask = new MyTimerTask(channelID, READ_KEY, url, textTemp, textUmidity, textPh, textConducibilita, textIrradianza, textPeso, textStato, testo1, cont, database);
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 3000);
    }

    public static void stampa() {

        List<Channel> arrayList = database.ChannelDao().getAll();
        System.out.println("stampo il database cannel");
        for (int i = 0; i < arrayList.size(); i++)
            System.out.println(arrayList.get(i).getId() + " --" + arrayList.get(i).getFiled1() + " --" + arrayList.get(i).getRead_key());

        System.out.println("FINE");

        List<savedValues> arrayList1 = database.SavedDao().getAll();
        System.out.println("stampo channel default");
        for (int i = 0; i < arrayList1.size(); i++)
            System.out.println(arrayList1.get(i).getId() + " --" + arrayList1.get(i).getPosition() + " --" + arrayList1.get(i).getKey());

        System.out.println("FINE");


    }

}

