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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
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
    private static Channel channel1;
    private TextView temp;
    private TextView umid;
    private TextView ph;
    private TextView cond;
    private TextView irra;
    private TextView peso;
    private static Context cont;
    private static NotificationManagerCompat notificationManager;
    private static AppDatabase db;

    public MyTimerTask(Channel chan,TextView temp,  TextView umid, TextView ph, TextView cond, TextView irra, TextView peso,Context context,AppDatabase database) {
        if(chan!=null){
            Log.d("CHAN",chan.getId());
            channel1=chan;
        }
        db=database;
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
        Boolean ok=false;
        //recupero dalla lista tutti i channel e se hanno le notifiche attive li eseguo in background
        List<Channel> allchannel= db.ChannelDao().getAll();

        for(int i=0;i<allchannel.size();i++) {
            Channel actualchannel = allchannel.get(i);
            //se ho le notifiche abilitata lo avvio
            if (actualchannel.getNotification()) {
                int minuti;
                try {        //se i minuti sono 0 metto di default 60
                    minuti = actualchannel.getLastimevalues();
                    if (minuti == 0) minuti = 60;
                } catch (Exception e) {
                    minuti = 60;
                }
                String urlString = "https://api.thingspeak.com/channels/" + actualchannel.getId() + "/feeds.json?api_key=" + actualchannel.getRead_key()
                        + "&minutes=" + minuti + "&offset="+getCurrentTimezoneOffset();
                getJsonResponse(urlString,actualchannel);
                Log.d("URL", urlString);
                ok=true;
            }
        }
        //se non ho nessun channel con le notifiche abilitate interrompo il servizio
        if(!ok) ExampleService.stoptimer();
    }

    //metodo per reperire le risposte json
    private void getJsonResponse(String urlString,final Channel channel) {
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

                            Double t = 0.0;
                            Double somt=0.0;
                            Double u = 0.0;
                            Double somu=0.0;
                            Double p = 0.0;
                            Double somp=0.0;
                            Double c = 0.0;
                            Double somc=0.0;
                            Double ir = 0.0;
                            Double somir=0.0;
                            //stringa che mi salva l'ultimo data di aggiornamento dei valori
                            String cretime=null;
                            Channel v=channel;
                            //scorro tutto l'array
                            for (int i = 0; i < jsonArray.length(); i++) {

                                //recupero il primo oggetto dell'array
                                final JSONObject value = jsonArray.getJSONObject(i);

                                try {
                                    String temperature = value.getString("field1");
                                    //se ho impostato un valore, inserisci quello,altrimenti se già c'è uno standard prendilo in automatico altrimenti non scrivo nulla
                                    if(v.getImagetemp()!=null){
                                        String field=value.getString(v.getImagetemp());
                                        t=t+(Math.round(Double.parseDouble(String.format(field)) * 100.0) / 100.0);
                                        somt++;
                                    }
                                    else if (fields.get(0).equals("Temperature")){
                                        t = t + (Math.round(Double.parseDouble(String.format(temperature)) * 100.0) / 100.0);
                                        somt++;
                                    }
                                }catch (Exception e){
                                }
                                try {
                                    String umidity = value.getString("field2");
                                    //se ho impostato un valore, inserisci quello,altrimenti se già c'è uno standard prendilo in automatico altrimenti non scrivo nulla
                                    if(v.getImageumid()!=null){
                                        String field=value.getString(v.getImageumid());
                                        u=u+(Math.round(Double.parseDouble(String.format(field)) * 100.0) / 100.0);
                                        somu++;
                                    }
                                    else if (fields.get(1).equals("Humidity")){
                                        u = u + (Math.round(Double.parseDouble(String.format(umidity)) * 100.0) / 100.0);
                                        somu++;
                                    }
                                }catch (Exception e){
                                }
                                try {
                                    String ph1 = value.getString("field3");
                                    //se ho impostato un valore, inserisci quello,altrimenti se già c'è uno standard prendilo in automatico altrimenti non scrivo nulla
                                    if(v.getImageph()!=null){
                                        String field=value.getString(v.getImageph());
                                        p=p+(Math.round(Double.parseDouble(String.format(field)) * 100.0) / 100.0);
                                        somp++;
                                    }
                                    else if (fields.get(1).equals("pH_value")){
                                        p = p + (Math.round(Double.parseDouble(String.format(ph1)) * 100.0) / 100.0);
                                        somp++;
                                    }
                                }catch (Exception e){
                                }
                                try {
                                    String conducibilita = value.getString("field4");
                                    //se ho impostato un valore, inserisci quello,altrimenti se già c'è uno standard prendilo in automatico altrimenti non scrivo nulla
                                    if(v.getImageph()!=null){
                                        String field=value.getString(v.getImageph());
                                        c=c+(Math.round(Double.parseDouble(String.format(field)) * 100.0) / 100.0);
                                        somc++;
                                    }
                                    else if (fields.get(3).equals("electric_conductivity")) {
                                        c=c+(Math.round(Double.parseDouble(String.format(conducibilita)) * 100.0) / 100.0);
                                        somc++;
                                    }
                                }catch (Exception e){
                                }
                                try {
                                    String irradianza = value.getString("field5");
                                    //se ho impostato un valore, inserisci quello,altrimenti se già c'è uno standard prendilo in automatico altrimenti non scrivo nulla
                                    if(v.getImageph()!=null){
                                        String field=value.getString(v.getImageph());
                                        ir=ir+(Math.round(Double.parseDouble(String.format(field)) * 100.0) / 100.0);
                                        somc++;
                                    }
                                    else  if (fields.get(4).equals("Irradiance")) {
                                        ir=ir+(Math.round(Double.parseDouble(String.format(irradianza)) * 100.0) / 100.0);
                                        somir++;
                                    }
                                }catch (Exception e){
                                }

                                try {
                                    cretime = value.getString("created_at");
                                }catch (Exception e){ }
                            }

                            //calcolo la media di tutti i valori e la confronto con i miei valori,se la supera invio la notifica
                            t=Math.round(t/somt * 100.0) / 100.0;
                            u=Math.round(u/somu * 100.0) / 100.0;
                            p=Math.round(p/somp * 100.0) / 100.0;
                            c=Math.round(c/somc * 100.0) / 100.0;
                            ir=Math.round(ir/somir * 100.0) / 100.0;
                            Log.d("SOMMA VALORI: ","t:"+somt+" u:"+ somu +" p:"+ somp +" c:"+ somc +" ir:"+ somir);
                            Log.d("MEDIA VALORI: ","t:"+t+" u:"+ u +" p:"+ p +" c:"+ c +" ir:"+ ir);

                            //invio le notifiche se i valori non rispettano le soglie imposte
                            if(channel.getNotification()) {
                                notification(temp,t,channel.getImagetemp(),channel.getTempMin(),channel.getTempMax(),channel,1,"temperatura");
                                notification(umid,u,channel.getImageumid(),channel.getUmidMin(),channel.getUmidMax(),channel,2,"umidità");
                                notification(ph,p,channel.getImageph(),channel.getPhMin(),channel.getPhMax(),channel,3,"ph");
                                notification(cond,c,channel.getImagecond(),channel.getCondMin(),channel.getCondMax(),channel,4,"conducibilità");
                                notification(irra,ir,channel.getImageirra(),channel.getIrraMin(),channel.getIrraMax(),channel,5,"irradianza");
                              //notification(peso, Double.valueOf(peso.getText().toString().substring(0,peso.getText().toString().indexOf("g"))),channel.getImagepeso(),channel.getPesMin(),channel.getPesMax(),channel,6,"evapotraspirazione");

                                try {
                                    String evap=peso.getText().toString();
                                    if (channel.getPesMin() != null && Integer.valueOf(evap.substring(0,evap.indexOf("g"))) < channel.getPesMin())
                                        printnotify("Channel(" + channel.getId() + ") evapotraspirazione bassa!", 11*Integer.valueOf(channel.getId()));
                                    if (channel.getPesMax() != null && Integer.valueOf(peso.getText().toString()) > channel.getPesMax())
                                        printnotify("Channel(" + channel.getId() + ")  evapotraspirazione alta!", 12*Integer.valueOf(channel.getId()));
                                } catch (Exception e) {
                                    if (peso != null) peso.setText("- -");
                                }
                                try{
                                    Log.d("TEMPO:","distanza settata: "+channel.getTempomax()*60+" distanza attuale: "+ distanza(cretime));
                                    if (channel.getTempomax()!= 0 && distanza(cretime) > channel.getTempomax()*60)
                                         printnotify("Channel(" + channel.getId() + ") tempo alto!", 13*Integer.valueOf(channel.getId()));
                                }catch (Exception e) {
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

    public static String getCurrentTimezoneOffset() {

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());


        return String.valueOf((offsetInMillis/(1000*3600))-1);
    }
    //restituisce la distanza in secondi dall'ultimo aggiornamento
    private int distanza(String data) {
        Calendar date_now= Calendar.getInstance ();
        date_now.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar date_value = Calendar.getInstance ();

        //parsing della data
        int giorno=Integer.valueOf(data.substring(8, 10));
        int mese=Integer.valueOf(data.substring(5, 7));
        int anno=Integer.valueOf(data.substring(0, 4));
        int ore=Integer.valueOf(data.substring(11, 13));
        int minuti=Integer.valueOf(data.substring(14, 16));
        int secondi=Integer.valueOf(data.substring(17, 19));

        //setto le impostazioni relative alla data
        date_value.set (Calendar.YEAR,anno);
        date_value.set (Calendar.MONTH,mese-1);
        date_value.set (Calendar.DAY_OF_MONTH,giorno);
        date_value.set (Calendar.HOUR_OF_DAY,ore);
        date_value.set (Calendar.MINUTE,minuti);
        date_value.set (Calendar.SECOND, secondi);

        Log.d("DATE","DATA ORA: "+ date_now.getTime().toString() +"DATA CLOUD: "+ date_value.getTime().toString());
        //durata in secondi dall'ultimo aggiornamento
        long durata= (date_now.getTimeInMillis()/1000 - date_value.getTimeInMillis()/1000);

        return (int) durata;
    }

    private void notification(TextView text,Double t, String getimage,Double getmin,Double getmacx,Channel channel,int i,String defaultvalue) {
        try {
            //controllo che temp esiste ancora (nel caso dovessi rappresentarlo a schermo)
            if (text != null) text.setText(String.valueOf(t));
            //controllo che ho inserito un valore nella temperatura minima
            if (getmin != null){
                String value = null;
                //controllo se ho settato un valore manualmete
                if (getimage!=null) {
                    //associo il valore settato al relativo nome del campo field
                    if (getimage.equals("field1")) value = channel.getFiled1();
                    if (getimage.equals("field2")) value = channel.getFiled2();
                    if (getimage.equals("field3")) value = channel.getFiled3();
                    if (getimage.equals("field4")) value = channel.getFiled4();
                    if (getimage.equals("field5")) value = channel.getFiled5();
                    if (getimage.equals("field6")) value = channel.getFiled6();
                    if (getimage.equals("field7")) value = channel.getFiled7();
                    if (getimage.equals("field8")) value = channel.getFiled8();
                }
                if(t < getmin)
                    if(value==null) printnotify("Channel(" + channel.getId() + ") " + defaultvalue + " low!", i+Integer.valueOf(channel.getId()));
                    else  printnotify("Channel(" + channel.getId() + ") " + value + " low!", i+Integer.valueOf(channel.getId()));
            }
            if (getmacx != null){
                String value = null;
                //controllo se ho settato un valore manualmete
                if (getimage!=null) {
                    //associo il valore settato al relativo nome del campo field
                    if (getimage.equals("field1")) value = channel.getFiled1();
                    if (getimage.equals("field2")) value = channel.getFiled2();
                    if (getimage.equals("field3")) value = channel.getFiled3();
                    if (getimage.equals("field4")) value = channel.getFiled4();
                    if (getimage.equals("field5")) value = channel.getFiled5();
                    if (getimage.equals("field6")) value = channel.getFiled6();
                    if (getimage.equals("field7")) value = channel.getFiled7();
                    if (getimage.equals("field8")) value = channel.getFiled8();
                }
                if(t < getmacx)
                    if(value==null) printnotify("Channel(" + channel.getId() + ") " + defaultvalue+ " high!", (i+10)+Integer.valueOf(channel.getId()));
                    else  printnotify("Channel (" + channel.getId() + ") "+value +" high!", (i+10)+Integer.valueOf(channel.getId()));
            }
        } catch (Exception e) {
            if (text != null) text.setText("- -");
        }
    }
}


