package com.example.firstapp.Channel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.example.firstapp.Alert.AlertActivity;
import com.example.firstapp.AppDatabase;
import com.example.firstapp.MainActivity;
import com.example.firstapp.MyTimerTask;
import com.example.firstapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * Progetto: svilluppo App Android per Tirocinio interno
 *
 * Dipartimento di Informatica Università di Pisa
 *
 * Autore:Domenico Profumo matricola 533695
 * Si dichiara che il programma è in ogni sua parte, opera originale dell'autore
 *
 */

public class ChannelActivity  extends AppCompatActivity {

    private static List<Channel> channel;
    private static AppDatabase db;
    private static RecyclerView recycleView;
    private static RecyclerViewAdapter adapter;
    private static Context BasicContext;
    private static View viewlayout;
    private static View defaultStar = null;
    private static int pos=0;
    private static String DEFAULT_ID =null;
    private static String DEFAULT_READ_KEY = null;

    public static void setPosition(int position) {
        pos=position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewlayout=findViewById(R.id.view_layout);
        BasicContext=ChannelActivity.this;

        //creo il database
        if(savedInstanceState==null) {
            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "prodiction")
                    //consente l'aggiunta di richieste nel thred principale
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    //build mi serve per costruire il tutto
                    .build();
            getValue();
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        //azione quando premo il pulsante "+"
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText taskEditText =new EditText(BasicContext);
                //specifico che devo prendere solo numeri interi
                taskEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                taskEditText.setRawInputType(Configuration.KEYBOARD_12KEY);

                final EditText taskEditText2 =new EditText(BasicContext);
                AlertDialog.Builder dialog=new AlertDialog.Builder(BasicContext)
                        .setTitle("NUOVO CANALE")
                        .setMessage("INSERISCI ID")
                        .setView(taskEditText)
                        .setPositiveButton("AVANTI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlertDialog.Builder dialog2=new AlertDialog.Builder(BasicContext)
                                        .setTitle("NUOVO CANALE")
                                        .setMessage("INSERISCI READ_KEY")
                                        .setView(taskEditText2)
                                        .setPositiveButton("CONFERMA", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //controllo se i dati inseriti corrispondono ad un channel esistente
                                                if(db.ChannelDao().findByName(taskEditText.getText().toString(),taskEditText2.getText().toString())!=null) Toast.makeText(BasicContext,"channel già esistente!",Toast.LENGTH_SHORT).show();
                                                else {
                                                    // controllo se i parametri inseriti sono corretti
                                                    DEFAULT_ID = taskEditText.getText().toString();
                                                    DEFAULT_READ_KEY = taskEditText2.getText().toString();
                                                    if (testData(DEFAULT_ID, DEFAULT_READ_KEY)) {
                                                        //comunico il database aggiornato al thread
                                                        MyTimerTask.updateDatabase(db);
                                                        Toast.makeText(BasicContext, "operazione eseguita correttamente!", Toast.LENGTH_SHORT).show();
                                                        AlertActivity.stopService();
                                                        //segnalo al thread principale i nuovi id,key
                                                        if (pos == -1) pos = 0;
                                                        MainActivity.setDefaultSetting(DEFAULT_ID, DEFAULT_READ_KEY, pos);
                                                    } else
                                                        Toast.makeText(BasicContext, "operazione ERRATA!", Toast.LENGTH_SHORT).show();
                                                    //segnalo eventuali modifiche
                                                    adapter.notifyDataSetChanged();
                                                }

                                            }
                                        })
                                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(BasicContext,"operazione annullata!",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                AlertDialog allert2=dialog2.create();
                                allert2.show();
                                //azione che devo fare
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(BasicContext,"operazione annullata!",Toast.LENGTH_SHORT).show();
                            }
                        });

                AlertDialog allert=dialog.create();
                allert.show();
            }
        });

        recycleView=findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        recycleView.setLayoutManager(linearLayoutManager);
        adapter=new RecyclerViewAdapter(channel,BasicContext,db);
        recycleView.setAdapter(adapter);
        recycleView.setHasFixedSize(true);

    }


    private void getValue() {
        channel=db.ChannelDao().getAll();

        recycleView=viewlayout.findViewById(R.id.recyclerview);
        final LinearLayoutManager linearLayoutManager= new LinearLayoutManager(BasicContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleView.setLayoutManager(linearLayoutManager);
        adapter=new RecyclerViewAdapter(channel,BasicContext,db);
        recycleView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    //azione da svolgere dopo che ho ciccato sul pulsante "delete"
    public static void sendObjcet(View v, Context context, final int position) {
        //  Toast.makeText(BasicContext,"hai cliccato su " + position,Toast.LENGTH_SHORT).show();

        //avviso di cancellazione
        AlertDialog.Builder builder=new AlertDialog.Builder(BasicContext);
        builder.setTitle("Sei sicuro di voler eliminare il canale?");
        builder.setCancelable(true);
        builder.setPositiveButton("si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              //se non ci sono più canali cancello tutto
                if(channel.size()<2){
                    Toast.makeText(BasicContext,"CANCELLO TUTTO",Toast.LENGTH_SHORT).show();
                    //cancello i database
                    db.SavedDao().deleteAll();
                    //cancello lista channel
                    channel.clear();
                    //cancello il database dei canali
                    db.ChannelDao().deleteAll();
                    //invio agli altri che adesso è tutto null!
                    MainActivity.setDefaultSetting(null, null,-1);
                }
                else {
                    db.ChannelDao().delete(channel.get(position));
                    channel.remove(position);

                    if(position-1<0){
                        setPosition(0);
                    }
                    else{
                        //se c'è almeno un canale rimasto metto quello come default
                        if(channel.size()<2) setPosition(0);
                        else pos=position-1;
                    }

                    Channel nuovo=channel.get(pos);
                    MainActivity.setDefaultSetting(nuovo.getId(), nuovo.getRead_key(), position-1);
                    Toast.makeText(BasicContext, "canale " + position + " cancellato!", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(BasicContext,"operazione annullata!",Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog allert=builder.create();
        allert.show();

    }

    //eseguito quando premo sul pulsante per i preferiti
    public static void sendPrefer(View v, Context context, final int position) {
        System.out.println("aumenta: " + position);
        //setto il nuovo channel come quello di default
        Channel chan = channel.get(position);
        DEFAULT_ID = chan.getId();
        DEFAULT_READ_KEY = chan.getRead_key();
        chan.setPosition(1);
        db.ChannelDao().findByName(chan.getId(),chan.getRead_key()).setPosition(1);
        if(pos==-1) pos=0;
        //cancello il channel precedente come default se è diverso dal precedente
        Channel prec=channel.get(pos);
        if(chan.getId()!=prec.getId()) {
            prec.setPosition(0);
            db.ChannelDao().findByName(prec.getId(), prec.getRead_key()).setPosition(0);

            //setto la nuova posizione
            pos = position;
        }

        if(position==pos) {
            if (defaultStar == null) {
                //salvo il bottone corrente e il background
                defaultStar = v;
                //cambio la disposizione
                v.setBackgroundResource(R.drawable.ic_star);

            } else {
                //salvo il bottone corrente e il background
                defaultStar.setBackground(v.getBackground());
                defaultStar = v;
                //cambio la disposizione
                v.setBackgroundResource(R.drawable.ic_star);
            }

        }
        //invio i nuovi dati di default
        MainActivity.setDefaultSetting(DEFAULT_ID, DEFAULT_READ_KEY,pos);

    }

    public static int getposition(){
        return pos;
    }


    public static Intent getActivityintent(Context context){
        Intent intent=new Intent(context,ChannelActivity.class);
        return intent;
    }


        public boolean testData(String valueID, String valueKEY) {

        BlockingQueue<Boolean> esito = new LinkedBlockingQueue<Boolean>();
        ExecutorService pes = Executors.newFixedThreadPool(1);
        pes.submit(new Task(esito, valueID, valueKEY));
        pes.shutdown();
        boolean esit=false;
        try {
            esit=esito.take();
        }catch (Exception e){
            e.printStackTrace();
        }
        return esit;
    }

    public static void stampa() {
        for (int i = 0; i < channel.size(); i++) {
            System.out.println(i + ": " + channel.get(i).getPosition());
        }
        System.out.println(pos);
    }

    class Task implements Runnable {
        private String id = null;
        private String key = null;
        private final BlockingQueue<Boolean> sharedQueue;

        public Task(BlockingQueue<Boolean> esito, String valueID, String valueKEY) {
            this.id = valueID;
            this.key = valueKEY;
            this.sharedQueue = esito;
        }

        @Override
        public void run() {
            try {
                URL url = new URL("https://api.thingspeak.com/channels/" + id + "/feeds.json?api_key=" + key);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    Channel add = new Channel(id, key);
                    int id = -1;
                    //prendo uid dell'ultimo elemento inserito
                    if (channel.size() - 1 >= 0)
                        id = channel.get(channel.size() - 1).getUid();
                    else  id = 0;
                    //cambiare getUid con READ_KEY
                    add.setUid(id + 1);

                    //settare i fields qui appena inserisco il mio channel
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));

                    String JSON_DATA=br.readLine();
                    JSONObject obj = new JSONObject(JSON_DATA);

                        try {
                           add.setFiled1(String.valueOf(obj.getJSONObject("channel").get("field1")));
                        } catch (Exception e){
                            add.setFiled1(null);
                        }

                        try {
                            add.setFiled2(String.valueOf(obj.getJSONObject("channel").get("field2")));
                        } catch (Exception e){
                            add.setFiled2(null);
                        }

                        try {
                          add.setFiled3(String.valueOf(obj.getJSONObject("channel").get("field3")));
                        } catch (Exception e){
                            add.setFiled3(null);
                        }

                        try {
                            add.setFiled4(String.valueOf(obj.getJSONObject("channel").get("field4")));
                        } catch (Exception e){
                            add.setFiled4(null);
                        }
                        try {
                           add.setFiled5(String.valueOf(obj.getJSONObject("channel").get("field5")));
                        } catch (Exception e){
                            add.setFiled5(null);
                        }
                        try {
                            add.setFiled6(String.valueOf(obj.getJSONObject("channel").get("field6")));
                        } catch (Exception e){
                            add.setFiled6(null);
                        }
                        try {
                          add.setFiled7(String.valueOf(obj.getJSONObject("channel").get("field7")));
                        } catch (Exception e){
                            add.setFiled7(null);
                        }
                        try {
                            add.setFiled8(String.valueOf(obj.getJSONObject("channel").get("field8")));
                        } catch (Exception e){
                            add.setFiled8(null);
                        }

                    db.ChannelDao().insert(add);
                    channel.add(add);
                    sharedQueue.put(true);
                } else sharedQueue.put(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
