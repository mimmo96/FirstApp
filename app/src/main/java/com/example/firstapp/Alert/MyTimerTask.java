package com.example.firstapp.Alert;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.firstapp.AppDatabase;
import com.example.firstapp.Channel.Channel;
import com.example.firstapp.MainActivity;
import com.example.firstapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

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

public class MyTimerTask extends TimerTask {

    private Channel channel;
    private TextView temp;
    private TextView umid;
    private TextView ph;
    private TextView cond;
    private TextView irra;
    private TextView peso;
    private static Context cont;
    private static NotificationManagerCompat notificationManager;
    private static String urlString;

    public MyTimerTask(String url,Channel chan,TextView temp,  TextView umid, TextView ph, TextView cond, TextView irra, TextView peso,Context context) {
        channel=chan;
        urlString = url;
        this.temp = temp;
        this.umid = umid;
        this.irra = irra;
        this.peso = peso;
        this.ph = ph;
        this.cond = cond;
        cont=context;
        notificationManager=NotificationManagerCompat.from(cont);
    }

    @Override
    public void run() {
        Log.d("MyTimerTask","run");
        getJsonResponse(urlString);
        Log.d("URL",urlString);
    }

    //metodo per reperire le risposte json
    private void getJsonResponse(String urlString) {

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlString, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //recupero l'array feeds
                            JSONArray jsonArray = response.getJSONArray("feeds");

                            //recupero i fields associati al channel
                            ArrayList<String> fields = new ArrayList<String>();
                            int dim = response.getJSONObject("channel").length();
                            Log.d("Thread background", "donwload eseguito");
                            //salvo tutti i field nell'array
                            try {
                                for (int i = 0; i < dim; i++) {
                                    fields.add(String.valueOf(response.getJSONObject("channel").get("field" + (i + 1))));
                                }
                            } catch (Exception e) {

                            }

                            //scorro tutto l'array e stampo a schermo il valore di field1
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //recupero il primo oggetto dell'array
                                final JSONObject value = jsonArray.getJSONObject(i);

                                Double t = 0.0;
                                Double u = 0.0;
                                Double p = 0.0;
                                Double c = 0.0;
                                Double ir = 0.0;
                                Double pe = 0.0;

                                try {
                                    String temperature = value.getString("field1");
                                    if (fields.get(0).equals("Temperature")){
                                        t = Math.round(Double.parseDouble(String.format(temperature)) * 100.0) / 100.0;
                                    if(temp!=null)    temp.setText(String.valueOf(t));
                                    if (channel.getTempMin() != null && t < channel.getTempMin())
                                        printnotify("Channel("+channel.getId()+") temperatura bassa!", 1);
                                    if (channel.getTempMax() != null && t > channel.getTempMax())
                                        printnotify("Channel ("+channel.getId()+") temperatura alta!", 2);
                                     } else temp.setText("- -");
                                }catch (Exception e){
                                    if(temp!=null)     temp.setText("- -");
                                }
                                try{
                                    String umidity = value.getString("field2");
                                    if (fields.get(1).equals("Humidity")) {
                                        u = Math.round(Double.parseDouble(String.format(umidity)) * 100.0) / 100.0;
                                        if(umid!=null)    umid.setText(String.valueOf(u));
                                        if (channel.getUmidMin() != null && u < channel.getUmidMin())
                                            printnotify("Channel("+channel.getId()+") umidità bassa!", 3);
                                        if (channel.getUmidMax()  != null && u > channel.getUmidMax())
                                            printnotify("Channel("+channel.getId()+") umidità alta!", 4);
                                    } else umid.setText("- -");
                                }catch (Exception e){
                                    if(umid!=null)      umid.setText("- -");
                                }
                                try {
                                    String ph1 = value.getString("field3");
                                    if (fields.get(2).equals("pH_value")) {
                                        p = Math.round(Double.parseDouble(String.format(ph1)) * 100.0) / 100.0;
                                        if(ph!=null) ph.setText(String.valueOf(p));
                                        if (channel.getPhMin() != null && p < channel.getPhMin())
                                            printnotify("Channel("+channel.getId()+") ph basso!", 7);
                                        if (channel.getPhMax() != null && p > channel.getPhMax())
                                            printnotify("Channel("+channel.getId()+") ph alto!", 8);

                                    } else ph.setText("- -");

                                }catch (Exception e){
                                      if(ph!=null)         ph.setText("- -");
                                }
                                try {
                                    String conducibilita = value.getString("field4");
                                    if (fields.get(3).equals("electric_conductivity")) {
                                        c = Math.round(Double.parseDouble(String.format(conducibilita)) * 100.0) / 100.0;
                                        if(cond!=null) cond.setText(String.valueOf(c));
                                        if (channel.getCondMin() != null && c < channel.getCondMin())
                                            printnotify("Channel("+channel.getId()+") conducibilità bassa!", 5);
                                        if (channel.getCondMax() != null && c > channel.getCondMax())
                                            printnotify("Channel("+channel.getId()+") conducibilità alta!", 6);

                                    } else cond.setText("- -");
                                }catch (Exception e){
                                  if(cond!=null)  cond.setText("- -");
                                }
                                try {
                                    String irradianza = value.getString("field5");
                                    if (fields.get(4).equals("Irradiance")) {
                                        ir = Math.round(Double.parseDouble(String.format(irradianza)) * 100.0) / 100.0;
                                        if(irra!=null) irra.setText(String.valueOf(ir));
                                        if (channel.getIrraMin() != null && ir < channel.getIrraMin())
                                            printnotify("Channel("+channel.getId()+") irradianza bassa!", 9);
                                        if (channel.getIrraMax() != null && ir > channel.getIrraMax())
                                            printnotify("Channel("+channel.getId()+") irradianza alta!", 10);

                                    } else irra.setText("- -");
                                }catch (Exception e){
                                   if(irra!=null) irra.setText("- -");
                                }
                                try {
                                    String peso1 = value.getString("field6");
                                    if (fields.get(5).equals("P0")) {
                                        pe = Math.round(Double.parseDouble(String.format(peso1)) * 100.0) / 100.0;
                                        if(peso!=null) peso.setText(String.valueOf(pe).concat(" g"));
                                        if (channel.getPesMin() != null && pe < channel.getPesMin())
                                            printnotify("Channel("+channel.getId()+") peso basso!", 11);
                                        if (channel.getPesMax() != null && pe > channel.getPesMax())
                                            printnotify("Channel("+channel.getId()+") peso alto!", 12);
                                    } else peso.setText("- -");
                                }catch (Exception e){
                                   if(peso!=null) peso.setText("- -");
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Thread background", "errore donwload");
            }
        });
        Volley.newRequestQueue(cont).add(jsonObjectRequest);
        Log.d("MyTimerTask", "aggiunto alla coda");
    }

    public void printnotify(String text,int i){
        Intent notificationIntent = new Intent(cont, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(cont,0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(cont, CHANNEL_1_ID)
                .setContentTitle("Green App")
                .setContentText(text)
                .setSmallIcon(R.drawable.pianta)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        notification.flags = Notification.FLAG_INSISTENT | Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(i,notification);
    }
}


