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
import com.example.firstapp.AppDatabase;
import com.example.firstapp.MainActivity;
import com.example.firstapp.MyTimerTask;
import com.example.firstapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ChannelActivity  extends AppCompatActivity {

    private static List<Channel> channel;
    private static AppDatabase db;
    private static RecyclerView recycleView;
    private static RecyclerViewAdapter adapter;
    private static Context BasicContext;
    private static View viewlayout;
    private static View defaultStar = null;
    private static int pos=0;
    private static String DEFAULT_ID = "816869";
    private static String DEFAULT_READ_KEY = "KLEZNXOV7EPHHEUT";

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
                                                //controllo se i parametri inseriti sono corretti
                                                DEFAULT_ID = taskEditText.getText().toString();
                                                DEFAULT_READ_KEY = taskEditText2.toString();
                                                //controllo se i dati inseriti corrispondono ad un channel
                                                if (testData(DEFAULT_ID, DEFAULT_READ_KEY)) {
                                                    Toast.makeText(BasicContext, "operazione eseguita correttamente!", Toast.LENGTH_SHORT).show();
                                                    //segnalo al thread principale i nuovi id,key
                                                    MyTimerTask.setDefaultSetting(DEFAULT_ID, DEFAULT_READ_KEY);

                                                }
                                                else  Toast.makeText(BasicContext, "operazione ERRATA!", Toast.LENGTH_SHORT).show();
                                                //segnalo eventuali modifiche
                                                adapter.notifyDataSetChanged();

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
              //controllo che ci sono altri canali
                if(channel.size()<2)   Toast.makeText(BasicContext,"UNICO CANALE RIMASTO,IMPOSSIBILE CANCELLARE!",Toast.LENGTH_SHORT).show();
                else {
                    db.ChannelDao().delete(channel.get(position));
                    channel.remove(position);

                    if(position-1<0){
                        setPosition(0);
                    }
                    else pos=position-1;

                    Channel nuovo=channel.get(pos);
                    MyTimerTask.setDefaultSetting(nuovo.getId(), nuovo.getRead_key());
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
        MyTimerTask.setDefaultSetting(DEFAULT_ID, DEFAULT_READ_KEY);
        MainActivity.setDefaultSetting(DEFAULT_ID, DEFAULT_READ_KEY,pos);
        stampa();

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
                    else id = 0;
                    //cambiare getUid con READ_KEY
                    add.setUid(id + 1);
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
