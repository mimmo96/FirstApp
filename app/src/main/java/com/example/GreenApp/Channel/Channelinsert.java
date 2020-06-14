package com.example.GreenApp.Channel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.firstapp.R;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Channelinsert  extends AppCompatActivity {

    //lista di elemeenti associati
    private static Button annulla;
    private static Button salva;
    private static EditText IDlett;
    private static EditText IDscritt;
    private static EditText read_lett;
    private static EditText read_scritt;
    private static EditText write_scritt;

    /**
     * metodo eseguito alla creazione
     * @param savedInstanceState: elementi salvati
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_insert);

        //associo le componenti
        annulla=findViewById(R.id.buttonAnnul);
        salva=findViewById(R.id.buttonSal);
        IDlett=findViewById(R.id.channelID_Lett);
        IDscritt=findViewById(R.id.channelID_Scritt);
        read_lett=findViewById(R.id.channelRead_Lett);
        read_scritt=findViewById(R.id.channelRead_Scritt);
        write_scritt=findViewById(R.id.channelWrite_Scritt);
    }

    /**
     * azione eseguita quando premo il pulsante annulla
     * @param v:puntatore al pulsante annulla
     */
    public void annulla(View v) {
        //termino l'attività
        finish();
    }

    /**
     * azione eseguita quando premo il pulsante salva
     * @param v: puntatore al pulsante salva
     */
    public void salva(View v) {
        Log.d("ChannelInsert","\n\n IDlett:" + IDlett.getText().toString()+ "\tIDscritt:"+ IDscritt.getText().toString() + "\n read_lett:" + read_lett.getText().toString()
                + "\tread_scritt:" +read_scritt.getText().toString()+ "\n\t\t\t write_scritt:" +write_scritt.getText().toString());

        String id_lett=null;
        String readkey_lett=null;
        //verifico che ho effettivamente inserito i valori
        if(IDlett.getText().toString().equals("") )
            Toast.makeText(getApplicationContext(),"INSERISCI ID",Toast.LENGTH_SHORT).show();
        else{
            id_lett=IDlett.getText().toString();
            readkey_lett= read_lett.getText().toString();

                //verifico che ho inserito la chiave di scrittura altrimenti metto null
                if(!IDscritt.getText().toString().equals("") ){
                    Log.d("Channelinsert1","sono qui");
                    //verifico se esiste veramente la chiave di scrittura
                    if(testData(IDscritt.getText().toString(),read_scritt.getText().toString())){
                         //mando i valori settati all'activity precedente
                         ChannelActivity.Execute(id_lett,readkey_lett,IDscritt.getText().toString(),read_scritt.getText().toString(),write_scritt.getText().toString());
                        finish();
                     }
                     //se ho inserito un channel in scrittura errato
                    else Toast.makeText(getApplicationContext(),"Chiave di scrittura errata!",Toast.LENGTH_SHORT).show();
                }
                 else {
                    ChannelActivity.Execute(id_lett, readkey_lett, null, null, null);
                    finish();
                }
        }
    }

    /**
     *
     * @param context:context associato alla classe
     * @return restituisce l'intent
     */
    public static Intent getActivityintent(Context context){
        Intent intent=new Intent(context,Channelinsert.class);
        return intent;
    }

    /**
     * verifica se la chiave di scrittura esiste
     * @param valueID:id della chiave di scrittura
     * @param valueREADKEY:api lettura della chiave di scrittura
     * @return true se il dato è stato inserito
     */
    public static boolean testData(String valueID, String valueREADKEY) {

        BlockingQueue<Boolean> esito = new LinkedBlockingQueue<Boolean>();
        ExecutorService pes = Executors.newFixedThreadPool(1);
        pes.submit(new Task(esito, valueID, valueREADKEY));
        pes.shutdown();
        boolean esit=false;
        try {
            esit=esito.take();
        }catch (Exception e){
            e.printStackTrace();
        }
        return esit;
    }

    /**
     * Thread che si occuperà di gestire le richieste di connessione per verificarne l'esistenza del channel di scrittura
     */
    static class Task implements Runnable {
        private String id = null;
        private String key_read = null;
        private final BlockingQueue<Boolean> sharedQueue;

        //metodo costruttore
        public Task(BlockingQueue<Boolean> esito, String valueID, String valueREADKEY) {
            this.id = valueID;
            this.key_read = valueREADKEY;
            this.sharedQueue = esito;
        }

        /**
         * metodo eseguito all'avvio
         */
        @Override
        public void run() {
            try {
                URL url = new URL("https://api.thingspeak.com/channels/" + id + "/feeds.json?api_key=" + key_read);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode()==200) {
                    sharedQueue.put(true);
                } else sharedQueue.put(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}