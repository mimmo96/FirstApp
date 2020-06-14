package com.example.GreenApp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.example.GreenApp.Alert.AlertActivity;
import com.example.GreenApp.Alert.ExampleService;
import com.example.GreenApp.Channel.Channel;
import com.example.GreenApp.Channel.ChannelActivity;
import com.example.GreenApp.Channel.savedValues;
import com.example.GreenApp.Irrigation.IrrigationActivity;
import com.example.firstapp.R;

/*
 * Progetto: svilluppo App Android per Tirocinio interno
 *
 * Dipartimento di Informatica Università di Pisa
 *
 * Autore:Domenico Profumo matricola 533695
 * Si dichiara che il programma è in ogni sua parte, opera originale dell'autore
 */
public class MainActivity extends AppCompatActivity {

    //dichiaro componenti grafici
    public static TextView textTemp;
    public static ImageView image;
    public static TextView textUmidity;
    public static TextView textPh;
    public static TextView textConducibilita;
    public static TextView textIrradianza;
    public static TextView textPeso;
    public static TextView textStato;
    public static TextView testo1;

    //lista che conterrà il channel di default
    private static List<savedValues> channeldefault;

    //id e chiave di lettura del channel utilizzato
    private static String channelID = null;
    private static String READ_KEY = null;

    //url utilizzata per reperire le info del chaneel di default
    private static String url;
    private static AppDatabase database;
    private static TimerTask timerTask;
    private static Timer timer;
    private static Context cont;

    /**
     * metodo principale eseguito all'avvio
     * @param savedInstanceState:insieme di parametri precedentemente salvati
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ripristino valori salvati precedentemente se ci sono
        BackupValues(savedInstanceState);

        //database.ChannelDao().deleteAll();
        //database.SavedDao().deleteAll();
        //associo i riferimenti alle varie componenti
        textTemp = findViewById(R.id.textTemp);
        textUmidity = findViewById(R.id.textUmidity);
        textPh = findViewById(R.id.textPh);
        textConducibilita = findViewById(R.id.textConducibility);
        textIrradianza = findViewById(R.id.textIrradiance);
        textPeso = findViewById(R.id.textEvap);
        textStato = findViewById(R.id.textViewON);
        testo1 = findViewById(R.id.textView1);
        image=findViewById(R.id.imageView3);
        cont = getApplicationContext();

        //controllo se ho almeno un channel inserito
        if (url == null) {
            textTemp.setText("- -");
            textUmidity.setText("- -");
            textPh.setText("- -");
            textConducibilita.setText("- -");
            textIrradianza.setText("- -");
            textPeso.setText("- -");
            testo1.setText("INSERISCI UN NUOVO CHANNEL");
        } else {
            //se c'era almeno un channel avvio un nuovo task che si occuperà di gestire le richieste con il server ed avvio il service in background in caso di notifiche
            startTimer(cont);
            ExampleService.stoptimer();
            Intent intentservices=new Intent(cont,ExampleService.class);
            ContextCompat.startForegroundService(cont,intentservices);
        }
    }

    /**
     * funzione per ripristinare i dati precedentemente impostati dal database
     * @param savedInstanceState:insieme di parametri precedentemente salvati
     */
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
                READ_KEY = channeldefault.get(0).getRead_key();
                url = "https://api.thingspeak.com/channels/" + channelID + "/feeds.json?api_key=" + READ_KEY + "&results=100";
                ChannelActivity.setPosition(channeldefault.get(0).getPosition());
            }
            //se non ho nessun elemento inserito setto a null i valori dei channel e metto la posizione ad -1
            else {
                channelID = null;
                READ_KEY = null;
                url = null;
                ChannelActivity.setPosition(-1);
            }
        }
    }

    /**
     * funzione eseguita quando vado a cliccare sul pulsate dei grafici
     * @param v puntatore all'icona dei grafici
     */
    public void doAdd(View v) {
        //array che memorizza la posizione degli elementi selezionati e il nomed ell'elemento corrispondente
        final boolean[] checkedItems;
        final String[] listItems;

        //lista che mi setta la posizione degli elementi selezionati
        final ArrayList<Integer> mUserItems = new ArrayList<>();
        final ArrayList<String> list = new ArrayList<>();
        final List<Channel> allchannel=database.ChannelDao().getAll();

        //memorizza il channel associato al field selezionato
        final List<Channel> selectedChannel=new ArrayList<>();

        //lista che mi salva il nome di tutti gli elementi selezionati e la posizione di essi
        final ArrayList<String> name = new ArrayList<>();
        final ArrayList<Integer> posField = new ArrayList<>();

        //scandisco tutti i channel presnti nel database per trovare i loro field
        for(int i=0;i<allchannel.size();i++) {
            Channel inUse = allchannel.get(i);
            System.out.println("ho premuto:" + inUse.getFiled1());
            if (inUse.getFiled1() != null){
                list.add(inUse.getFiled1().concat(" (id:").concat(inUse.getLett_id()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(1);
            }
            if (inUse.getFiled2() != null){
                list.add(inUse.getFiled2().concat(" (id:").concat(inUse.getLett_id()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(2);
            }
            if (inUse.getFiled3() != null) {
                list.add(inUse.getFiled3().concat(" (id:").concat(inUse.getLett_id()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(3);
            }
            if (inUse.getFiled4() != null){
                list.add(inUse.getFiled4().concat(" (id:").concat(inUse.getLett_id()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(4);
            }
            if (inUse.getFiled5() != null){
                list.add(inUse.getFiled5().concat(" (id:").concat(inUse.getLett_id()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(5);
            }
            if (inUse.getFiled6() != null){
                list.add(inUse.getFiled6().concat(" (id:").concat(inUse.getLett_id()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(6);
            }
            if (inUse.getFiled7() != null){
                list.add(inUse.getFiled7().concat(" (id:").concat(inUse.getLett_id()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(7);
            }
            if (inUse.getFiled8() != null){
                list.add(inUse.getFiled8().concat(" (id:").concat(inUse.getLett_id()).concat(")"));
                selectedChannel.add(inUse);
                posField.add(8);
            }
        }

        //converto gli array in liste
        listItems = list.toArray(new String[list.size()]);
        checkedItems = new boolean[list.size()];

        //se non c'è nessun channel avviso l'utente
        if (list.size() == 0)
            Toast.makeText(cont, "INSERISCI UN CHANNEL!", Toast.LENGTH_SHORT).show();
        else {
            //se c'è almeno un channel presente apro la finestra di dialogo in cui l'utente può selezionare fino a 6 fields
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            mBuilder.setTitle("Seleziona i tipi di grafici(max 6)");
            final ArrayList<Integer> selectedPos=new ArrayList<>();
            final ArrayList<Channel> selChan=new ArrayList<>();
            mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                    //salvo la selezione
                    if (isChecked) {
                        name.add(list.get(position));
                        selectedPos.add(posField.get(position));
                        selChan.add(selectedChannel.get(position));
                    } else {
                        //se ho desezionato rimuovo l'elemento dalla lista
                        name.remove(list.get(position));
                        selectedPos.remove(posField.get(position));
                        selChan.remove(selectedChannel.get(position));
                    }

                    //se eccedo 6 elementi selezionati elimino l'ultimo
                    if(name.size()>6){
                        name.remove(list.get(position));
                        selectedPos.remove(posField.get(position));
                        selChan.remove(selectedChannel.get(position));

                        ((AlertDialog) dialogInterface).getListView().setItemChecked(position, false);
                        checkedItems[position]=false;
                        Toast.makeText(getApplicationContext(),"HAI SELEZIONATO PIU GRAFICI DEL PREVISTO, DESELEZIONA I PRECEDENTI",Toast.LENGTH_LONG).show();
                    }

                }
            });

            mBuilder.setCancelable(false);
            //azione da svolgere quando premo sul pulsante visualizza
            mBuilder.setPositiveButton("VISUALIZZA", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    //se non ho selezionato nessun grafico comunico un messaggio di errore
                    if (name.size() == 0)
                        Toast.makeText(cont, "NESSUN GRAFICO SELEZIONATO!", Toast.LENGTH_SHORT).show();
                    else {
                        Intent intent = com.example.GreenApp.Graphic.MainActivity.getActivityintent(MainActivity.this);
                        com.example.GreenApp.Graphic.MainActivity.setGrapView(name, selChan,selectedPos);
                        startActivity(intent);
                    }
                }
            });

            //azione da svolgere quando premo sul pulsante annulla
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

    /**
     * azione che devo eseguire quando premo il pulsante impostazioni
     * @param v puntatore all'icona delle impostazioni
     */
    public void settingChannel(View v) {
        //avvio la nuova activity channelactivity
        Intent intent = ChannelActivity.getActivityintent(MainActivity.this);
        startActivity(intent);
    }

    /**
     * azione che devo eseguire quando premo il pulsante attenzione
     * @param v puntatore all'icona attenzione
     */
    public void notifiche(View v) {
        Intent intent = AlertActivity.getActivityintent(MainActivity.this);

        List<Channel> channeList=database.ChannelDao().getAll();
        Channel trovato=null;
        //controllo che esiste almeno un channel
        for(int i=0;i<channeList.size();i++){
            if(channeldefault.get(0).getId().equals(channeList.get(i).getLett_id())){
                trovato=channeList.get(i);
            }
        }
        //se esiste almeno un channel lo avvio altrimenti mando un messaggio di errore
        if(trovato==null){
         Toast.makeText(cont,"INSERISCI UN CHANNEL",Toast.LENGTH_SHORT).show();
        }
        else {
            AlertActivity.setChannel(trovato);
            startActivity(intent);
        }
    }

    /**
     * azione eseguita quando premo pulsante refresh
     * @param v puntatore all'icona refresh
     */
    public  void refresh(View v){
        //riavvio i il timer in modo da ri-scaricare gli ultimi valori dal server
        restartTimer(cont);
    }

    /**
     * mi imposta i nuovi vlori di default nel caso in cui l'utente abbia aggiornato il channel predefinito
     * @param id:nuovo id
     * @param key_read:chiave di lettura
     * @param pos:posizione associata
     */
    public static void setDefaultSetting(String id, String key_read,int pos) {

        //se non ho nessun canale (pos=-1) cancello tutto
        if (pos == -1) {
            channelID = null;
            READ_KEY = null;
            url = null;
            ChannelActivity.setPosition(-1);

            if (channeldefault.size() != 0) channeldefault.clear();
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
            //aggiungo alla lista channel default il nuovo solo se è diverso dal precedente
                if (channeldefault.size() != 0) channeldefault.clear();
                channeldefault.add(new savedValues(id, key_read, pos));
                database.SavedDao().deleteAll();
                database.SavedDao().insert(new savedValues(id, key_read,pos));
                channelID = id;
                READ_KEY = key_read;
                url = "https://api.thingspeak.com/channels/" + channelID + "/feeds.json?api_key=" + READ_KEY + "&results=100";
                restartTimer(cont);
            }
    }

    /**
     * funzione che mi permette di riavviare il timer e scaricare i nuovi valori dal server
     * @param cont:context associato
     */
    public static void restartTimer(Context cont) {

        if (timer != null) timer.cancel();
        if (timerTask != null) timerTask.cancel();
        timerTask = new MyTimerTask(channelID, READ_KEY, url, textTemp, textUmidity, textPh, textConducibilita, textIrradianza, textPeso, textStato, testo1, cont, database,image);
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 60000);
    }

    /**
     * avvio il timer che mi crea un nuovo task passando come parametro tutti i valori impostati di default
     * @param cont:context associato
     */
    public static void startTimer(Context cont) {
        timerTask = new MyTimerTask(channelID, READ_KEY, url, textTemp, textUmidity, textPh, textConducibilita, textIrradianza, textPeso, textStato, testo1, cont, database,image);
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 60000);
    }

    /**
     * funzione per settare il valore del singolo field non appena lo premo (icona temperatura)
     * @param v:puntatore al riferimento dell'icona della temperatura
     */
    public void tempSettings(View v) {
        List<Channel> channeList=database.ChannelDao().getAll();
        Channel trovato=null;
        //controllo che esiste almeno un channel
        for(int i=0;i<channeList.size();i++){
            if(channeldefault.get(0).getId().equals(channeList.get(i).getLett_id())){
                trovato=channeList.get(i);
            }
        }
        if(trovato==null){
            Toast.makeText(cont,"INSERISCI UN CHANNEL",Toast.LENGTH_SHORT).show();
        }
        else {
            //reperisco il channel utilizzato di default e faccio il parsing per scoprire quale fields è stato settato(posizione)
            final List<savedValues> allchannel = database.SavedDao().getAll();
            Channel inUse = database.ChannelDao().findByName(allchannel.get(0).getId(), allchannel.get(0).getRead_key());
            int pos = 0;
            if (inUse.getImagetemp() != null)
                pos = Integer.parseInt(inUse.getImagetemp().substring(5));
            fieldssettings(0, pos);
        }
    }

    /**
     * funzione per settare il valore del singolo field non appena lo premo (icona ph)
     * @param v:puntatore al riferimento dell'icona del ph
     */
    public void phSettings(View v){
        List<Channel> channeList=database.ChannelDao().getAll();
        Channel trovato=null;
        //controllo che esiste almeno un channel
        for(int i=0;i<channeList.size();i++){
            if(channeldefault.get(0).getId().equals(channeList.get(i).getLett_id())){
                trovato=channeList.get(i);
            }
        }
        if(trovato==null){
            Toast.makeText(cont,"INSERISCI UN CHANNEL",Toast.LENGTH_SHORT).show();
        }
        else {
            final List<savedValues> allchannel = database.SavedDao().getAll();
            Channel inUse = database.ChannelDao().findByName(allchannel.get(0).getId(), allchannel.get(0).getRead_key());
            int pos = 0;
            if (inUse.getImageph() != null) pos = Integer.parseInt(inUse.getImageph().substring(5));
            fieldssettings(1, pos);
        }
    }

    /**
     * funzione per settare il valore del singolo field non appena lo premo (icona irradianza)
     * @param v:puntatore al riferimento dell'icona del irradianza
     */
    public void irraSettings(View v){
        List<Channel> channeList=database.ChannelDao().getAll();
        Channel trovato=null;
        //controllo che esiste almeno un channel
        for(int i=0;i<channeList.size();i++){
            if(channeldefault.get(0).getId().equals(channeList.get(i).getLett_id())){
                trovato=channeList.get(i);
            }
        }
        if(trovato==null){
            Toast.makeText(cont,"INSERISCI UN CHANNEL",Toast.LENGTH_SHORT).show();
        }
        else {
            final List<savedValues> allchannel = database.SavedDao().getAll();
            Channel inUse = database.ChannelDao().findByName(allchannel.get(0).getId(), allchannel.get(0).getRead_key());
            int pos = 0;
            if (inUse.getImageirra() != null)
                pos = Integer.parseInt(inUse.getImageirra().substring(5));
            fieldssettings(2, pos);
        }
    }

    /**
     * funzione per settare il valore del singolo field non appena lo premo (icona conducibilità elettrica)
     * @param v:puntatore al riferimento dell'icona della conducibilità
     */
    public void condSettings(View v){
        List<Channel> channeList=database.ChannelDao().getAll();
        Channel trovato=null;
        //controllo che esiste almeno un channel
        for(int i=0;i<channeList.size();i++){
            if(channeldefault.get(0).getId().equals(channeList.get(i).getLett_id())){
                trovato=channeList.get(i);
            }
        }
        if(trovato==null){
            Toast.makeText(cont,"INSERISCI UN CHANNEL",Toast.LENGTH_SHORT).show();
        }
        else {
            final List<savedValues> allchannel = database.SavedDao().getAll();
            Channel inUse = database.ChannelDao().findByName(allchannel.get(0).getId(), allchannel.get(0).getRead_key());
            int pos = 0;
            if (inUse.getImagecond() != null)
                pos = Integer.parseInt(inUse.getImagecond().substring(5));
            fieldssettings(3, pos);
        }
    }

    /**
     * funzione per settare il valore del singolo field non appena lo premo (icona evapotraspirazione)
     * @param v:puntatore al riferimento dell'icona del peso
     */
    public void pesoSettings(View v){
        List<Channel> channeList=database.ChannelDao().getAll();
        Channel trovato=null;
        //controllo che esiste almeno un channel
        for(int i=0;i<channeList.size();i++){
            if(channeldefault.get(0).getId().equals(channeList.get(i).getLett_id())){
                trovato=channeList.get(i);
            }
        }
        if(trovato==null){
            Toast.makeText(cont,"INSERISCI UN CHANNEL",Toast.LENGTH_SHORT).show();
        }
        else {
            final List<savedValues> allchannel = database.SavedDao().getAll();
            Channel inUse = database.ChannelDao().findByName(allchannel.get(0).getId(), allchannel.get(0).getRead_key());
            int pos = 0;
            if (inUse.getImagepeso() != null)
                pos = Integer.parseInt(inUse.getImagepeso().substring(5));
            fieldssettings(4, pos);
        }
    }

    /**
     * funzione per settare il valore del singolo field non appena lo premo (icona umidità)
     * @param v:puntatore al riferimento dell'icona dell umidità
     */
    public void umidSettings(View v){
        List<Channel> channeList=database.ChannelDao().getAll();
        Channel trovato=null;
        //controllo che esiste almeno un channel
        for(int i=0;i<channeList.size();i++){
            if(channeldefault.get(0).getId().equals(channeList.get(i).getLett_id())){
                trovato=channeList.get(i);
            }
        }
        if(trovato==null){
            Toast.makeText(cont,"INSERISCI UN CHANNEL",Toast.LENGTH_SHORT).show();
        }
        else {
            final List<savedValues> allchannel = database.SavedDao().getAll();
            Channel inUse = database.ChannelDao().findByName(allchannel.get(0).getId(), allchannel.get(0).getRead_key());
            int pos = 0;
            if (inUse.getImageumid() != null)
                pos = Integer.parseInt(inUse.getImageumid().substring(5));
            fieldssettings(5, pos);
        }
    }

    /**
     * mi permette di settare il field associato alla singola icona che è stata premuta
     * @param field:numero del field associato
     * @param posizione:posizione associata
     */
    public void fieldssettings(final int field,int posizione){
        final String[] listItems;
        final int[] pos = new int[1];
        //contiene i nomi dei fields
        final ArrayList<String> list = new ArrayList<>();

        //nome del fileds selezionato
        final String[] name = new String[1];

        //recupero il channel default con i relativi field
        final List<savedValues> allchannel = database.SavedDao().getAll();
        Channel inUse = database.ChannelDao().findByName(allchannel.get(0).getId(), allchannel.get(0).getRead_key());
        if (inUse.getFiled1() != null) {
            list.add(inUse.getFiled1());
        }
        if (inUse.getFiled2() != null) {
            list.add(inUse.getFiled2());
        }
        if (inUse.getFiled3() != null) {
            list.add(inUse.getFiled3());
        }
        if (inUse.getFiled4() != null) {
            list.add(inUse.getFiled4());
        }
        if (inUse.getFiled5() != null) {
            list.add(inUse.getFiled5());
        }
        if (inUse.getFiled6() != null) {
            list.add(inUse.getFiled6());
        }
        if (inUse.getFiled7() != null) {
            list.add(inUse.getFiled7());
        }
        if (inUse.getFiled8() != null) {
            list.add(inUse.getFiled8());
        }

        //converte la lista contenete i nomi in un array
        listItems = list.toArray(new String[list.size()]);


        if (list.size() == 0)
            Toast.makeText(cont, "INSERISCI UN CHANNEL!", Toast.LENGTH_SHORT).show();
        else {
            //avvio la schermata per selezionare il rispettivo channel
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            mBuilder.setTitle("Seleziona il field da visualizzare");
            mBuilder.setSingleChoiceItems(listItems, posizione-1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                   name[0]=listItems[i];
                   pos[0]=i;
                }
            });

            mBuilder.setCancelable(false);
            //azione da svolgere quando premo sul pulsante visualizza
            mBuilder.setPositiveButton("VISUALIZZA", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    Channel x=database.ChannelDao().findByName(channelID,READ_KEY);
                    database.ChannelDao().delete(x);
                    if(field==0) x.setImagetemp("field"+(pos[0]+1));
                    if(field==1) x.setImageph("field"+(pos[0]+1));
                    if(field==2) x.setImageirra("field"+(pos[0]+1));
                    if(field==3) x.setImagecond("field"+(pos[0]+1));
                    if(field==4) x.setImagepeso("field"+(pos[0]+1));
                    if(field==5) x.setImageumid("field"+(pos[0]+1));
                    database.ChannelDao().insert(x);
                    restartTimer(cont);
                }
            });

            //azione da svolgere quando premo sul pulsante cancella tutto
            mBuilder.setNegativeButton("RESET", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    Channel x=database.ChannelDao().findByName(channelID,READ_KEY);
                    database.ChannelDao().delete(x);
                    if(field==0) x.setImagetemp(null);
                    if(field==1) x.setImageph(null);
                    if(field==2) x.setImageirra(null);
                    if(field==3) x.setImagecond(null);
                    if(field==4) x.setImagepeso(null);
                    if(field==5) x.setImageumid(null);
                    database.ChannelDao().insert(x);
                    restartTimer(cont);
                    dialogInterface.dismiss();
                }
            });

            //azione da svolgere quando premo sul pulsante cancella tutto
            mBuilder.setNeutralButton("ANNULLA", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }

    }

    /**
     * azione svolta quando premo sul pulsante irrigazione
     * @param v:puntatore al riferimento dell'icona dell'irrigazione
     */
    public void irrigation(View v){
        List<Channel> channeList=database.ChannelDao().getAll();
        Channel trovato=null;
        //controllo che esiste almeno un channel
        for(int i=0;i<channeList.size();i++){
            if(channeldefault.get(0).getId().equals(channeList.get(i).getLett_id())){
                trovato=channeList.get(i);
            }
        }
        if(trovato==null){
            Toast.makeText(cont,"INSERISCI UN CHANNEL",Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = IrrigationActivity.getActivityintent(MainActivity.this);

            //cerco nel database il channel in uso e lo mando
            List<savedValues> allchannel = database.SavedDao().getAll();
            Channel inUse = database.ChannelDao().findByName(allchannel.get(0).getId(), allchannel.get(0).getRead_key());

            IrrigationActivity.setChannle(inUse);
            startActivity(intent);
        }
    }

}