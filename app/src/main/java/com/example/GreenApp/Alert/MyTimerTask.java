package com.example.GreenApp.Alert;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.GreenApp.Channel.Channel;
import com.example.GreenApp.AppDatabase;
import com.example.GreenApp.MainActivity;
import com.example.firstapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.TimerTask;

import static com.example.GreenApp.Alert.App.CHANNEL_1_ID;

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
    private static List<Channel> channel;
    private static Context cont;
    private static NotificationManagerCompat notificationManager;
    private static AppDatabase db;
    private int minuti=0;

    public MyTimerTask(List<Channel> chan, Context context, AppDatabase database) {
        channel=chan;
        db=database;
        cont=context;
        notificationManager=NotificationManagerCompat.from(cont);
    }

    @Override
    public void run() {
        //recupero la lista e controllo lo stato dei channel con il database
        for(int i=0;i<channel.size();i++) {

            Channel actualchannel = db.ChannelDao().findByName(channel.get(i).getLett_id(), channel.get(i).getLett_read_key());
            int dist = 0;
            //se l'utente non ha settato il range di tempo per la media conto come distanza il tempo dall'ultimo valore
            if (actualchannel.getMinutes() != 0) minuti = actualchannel.getMinutes().intValue();
            if (actualchannel.getLastimevalues() == 0) dist = minuti;
            else dist = actualchannel.getLastimevalues() + minuti;
            Log.d("MyTimerTask", "Channel id : " + actualchannel.getLett_id());
            Log.d("MyTimerTask", "minuti : " + actualchannel.getMinutes());
            Log.d("MyTimerTask", "lasttime è: " + actualchannel.getLastimevalues());
            Log.d("MyTimerTask", "Distanza è:" + dist);
            String urlString;
            //se la distanza è 0 recupero solo l'ultimo valore
            if (dist == 0) {
                urlString = "https://api.thingspeak.com/channels/" + actualchannel.getLett_id() + "/feeds.json?api_key=" + actualchannel.getLett_read_key()
                        + "&results=1" + "&offset=" + getCurrentTimezoneOffset();
            } else
                urlString = "https://api.thingspeak.com/channels/" + actualchannel.getLett_id() + "/feeds.json?api_key=" + actualchannel.getLett_read_key()
                        + "&minutes=" + dist + "&offset=" + getCurrentTimezoneOffset();
            if(actualchannel.getNotification()){
                Log.d("MYTIMERTASK","AVVIO CHANNEL: "+ actualchannel.getLett_id());
                getJsonResponse(urlString, actualchannel);
            }
            Log.d("URL", urlString);
        }
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
                            Double ev = 0.0;
                            Double somev=0.0;
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
                                    else if (fields.get(2).equals("pH_value")){
                                        p = p + (Math.round(Double.parseDouble(String.format(ph1)) * 100.0) / 100.0);
                                        somp++;
                                    }
                                }catch (Exception e){
                                }
                                try {
                                    String conducibilita = value.getString("field4");
                                    //se ho impostato un valore, inserisci quello,altrimenti se già c'è uno standard prendilo in automatico altrimenti non scrivo nulla
                                    if(v.getImagecond()!=null){
                                        String field=value.getString(v.getImagecond());
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
                                        somir++;
                                    }
                                    else  if (fields.get(4).equals("Irradiance")) {
                                        ir=ir+(Math.round(Double.parseDouble(String.format(irradianza)) * 100.0) / 100.0);
                                        somir++;
                                    }
                                }catch (Exception e){
                                }

                                try {
                                    //se ho impostato un valore, inserisci quello,altrimenti se già c'è uno standard prendilo in automatico altrimenti non scrivo nulla
                                    if(v.getImagepeso()!=null){
                                        String field=value.getString(v.getImagepeso());
                                        ev=ev+(Math.round(Double.parseDouble(String.format(field)) * 100.0) / 100.0);
                                        somev++;
                                    }
                                    //altrimenti faccio una richiesta degli ultimi 100 valori e tra questi prendo quest'ultima
                                    else {
                                        String url= "https://api.thingspeak.com/channels/"+channel.getLett_id()+"/fields/7-8.json?api_key=" + channel.getLett_read_key();
                                        downloadEvapotraspirazione(url,channel);
                                    }

                                }catch (Exception e){
                                }

                                try {
                                    cretime = value.getString("created_at");
                                    minuti=(distanza(cretime)/60)+2;
                                }catch (Exception e){ }
                            }

                            //setto la distanza in minuti approssimata ad un minuto in più nel database del channel utilizzato
                            db.ChannelDao().delete(v);
                            v.setMinutes((double)minuti);
                            db.ChannelDao().insert(v);
                            Log.d("ALERTACTIVITY/MINUTES:",String.valueOf((double) minuti));

                            //calcolo la media di tutti i valori e la confronto con i miei valori,se la supera invio la notifica
                            t=Math.round(t/somt * 100.0) / 100.0;
                            u=Math.round(u/somu * 100.0) / 100.0;
                            p=Math.round(p/somp * 100.0) / 100.0;
                            c=Math.round(c/somc * 100.0) / 100.0;
                            ir=Math.round(ir/somir * 100.0) / 100.0;
                            ev=Math.round(ev/somev * 100.0) / 100.0;

                            Log.d("SOMMA VALORI: ","t:"+somt+" u:"+ somu +" ph:"+ somp +" c:"+ somc +" ir:"+ somir +" ev:"+ somev);
                            Log.d("MEDIA VALORI: ","t:"+t+" u:"+ u +" ph:"+ p +" c:"+ c +" ir:"+ ir  +" ev:"+ ev);

                            //invio le notifiche se i valori non rispettano le soglie imposte
                            if(channel.getNotification()) {
                                //controllo se ho letto effettivamente dei valori
                                if(somt!=0) notification(t,channel.getImagetemp(),channel.getTempMin(),channel.getTempMax(),channel,1,"temperatura");
                                if(somu!=0) notification(u,channel.getImageumid(),channel.getUmidMin(),channel.getUmidMax(),channel,2,"umidità");
                                if(somp!=0) notification(p,channel.getImageph(),channel.getPhMin(),channel.getPhMax(),channel,3,"ph");
                                if(somc!=0) notification(c,channel.getImagecond(),channel.getCondMin(),channel.getCondMax(),channel,4,"conducibilità");
                                if(somir!=0) notification(ir,channel.getImageirra(),channel.getIrraMin(),channel.getIrraMax(),channel,5,"irradianza");
                                if(somev!=0) notification(ev,channel.getImagepeso(),channel.getPesMin(),channel.getPesMax(),channel,6,"evapotraspirazione");

                                try{
                                    Log.d("TEMPO:","distanza settata: "+channel.getTempomax()*60+" distanza attuale: "+ distanza(cretime));
                                    if (channel.getTempomax()!= 0 && distanza(cretime) > channel.getTempomax()*60)
                                         printnotify("Channel(" + channel.getLett_id() + ") tempo alto!", 13*Integer.valueOf(channel.getLett_id()));
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

        NotificationManager notificationManager = (NotificationManager) cont.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "channel"+ i;
            CharSequence name = "channel"+ i;
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(cont, "channel"+ i)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("green App")
                .setContentText(text);

        Intent resultIntent = new Intent(cont, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(cont);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(i, builder.build());

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

       // Log.d("DATE","DATA ORA: "+ date_now.getTime().toString() +"DATA CLOUD: "+ date_value.getTime().toString());
        //durata in secondi dall'ultimo aggiornamento
        long durata= (date_now.getTimeInMillis()/1000 - date_value.getTimeInMillis()/1000);

        return (int) durata;
    }

    private void notification(Double t, String getimage,Double getmin,Double getmacx,Channel channel,int i,String defaultvalue) {
        try {
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
                    if(value==null) printnotify("Channel(" + channel.getLett_id() + ") " + defaultvalue + " low!", i+Integer.valueOf(channel.getLett_id()));
                    else  printnotify("Channel(" + channel.getLett_id() + ") " + value + " low!", i+Integer.valueOf(channel.getLett_id()));
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
                if(t > getmacx)
                    if(value==null) printnotify("Channel(" + channel.getLett_id() + ") " + defaultvalue+ " high!", (i+10)+Integer.valueOf(channel.getLett_id()));
                    else  printnotify("Channel (" + channel.getLett_id() + ") "+value +" high!", (i+10)+Integer.valueOf(channel.getLett_id()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void remove(Channel x){
        if(channel!=null){
            for(int i=0;i<channel.size();i++){
                if(channel.get(i).getLett_id().equals(x.getLett_id()))
                    channel.remove(i);
            }
        }
    }

    //notifiche dedicata all'evapotraspirazione
    private void downloadEvapotraspirazione(String urlString,final Channel channel) {
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

                                Boolean ok=false;
                                Boolean ok1=false;
                                Double irrigazione =0.0;
                                Double drainaggio = 0.0;

                                //scandisco tutti i 100 valori per trovare i valori di irrigazione e drenaggio
                                for (int k = 0; k < jsonArray.length(); k++) {
                                    JSONObject valori = jsonArray.getJSONObject(k);
                                    try {
                                        if (!valori.getString("field7").equals("") && !valori.getString("field7").equals("null")) {
                                            ok=true;
                                            irrigazione=Double.parseDouble(valori.getString("field7"));
                                        }
                                    }catch (Exception e){ }

                                    try {
                                        if (!valori.getString("field8").equals("") && !valori.getString("field8").equals("null")) {
                                            ok1=true;
                                            drainaggio=Double.parseDouble(valori.getString("field8"));
                                        }
                                    }catch (Exception e){ }
                                }

                                if(channel.getNotification()) {
                                    Double ev=null;
                                    if(ok && ok1){
                                        ev=Math.round((irrigazione - drainaggio) * 100.0) / 100.0;
                                        notification(ev,channel.getImagepeso(),channel.getPesMin(),channel.getPesMax(),channel,6,"evapotraspirazione");
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

}


