package com.example.GreenApp;


import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.GreenApp.Channel.Channel;
import com.example.GreenApp.Channel.savedValues;
import com.example.GreenApp.Graphic.MainActivity;
import com.example.firstapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.TimerTask;

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

    TextView textTemp;
    TextView textUmidity;
    TextView textPh;
    TextView textConducibilita;
    TextView textIrradianza;
    TextView textPeso;
    TextView text1;
    TextView stato;
    ImageView image;
    String url;
    Context context;
    private static String channelID=null;
    private static String READ_KEY=null;
    private static AppDatabase database;


    public MyTimerTask(String id, String key,String url, TextView textTemp1,TextView textUmidity1, TextView textPh1, TextView textConducibilita1,
                       TextView textIrradianza1,TextView textPO1,TextView stato,TextView testo1, Context cont,AppDatabase database,ImageView imm) {
        textTemp=textTemp1;
        textUmidity=textUmidity1;
        textPh=textPh1;
        textConducibilita=textConducibilita1;
        textIrradianza=textIrradianza1;
        textPeso=textPO1;
        this.stato=stato;
        text1=testo1;
        channelID=id;
        READ_KEY=key;
        this.url=url;
        this.database=database;
        context=cont;
        image=imm;
    }

    @Override
    public void run() {
        //reperisco i valori channel lettura
       getJsonResponse(url);

       //reperisco valori channel scrittura
       donwload();
    }

    //metodo per reperire le risposte json
     private void getJsonResponse (final String url){
        //se non ho nessun url inserita setto i valori a 0

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //recupero l'array feeds
                                JSONArray jsonArray = response.getJSONArray("feeds");

                                //recupero i fields associati al channel
                                ArrayList<String> fields = new ArrayList<String>();
                                int dim = response.getJSONObject("channel").length();

                                //salvo tutti i nomi dei field nell'array
                                try {
                                    for (int i = 0; i < dim; i++) {
                                        fields.add(String.valueOf(response.getJSONObject("channel").get("field" + (i + 1))));
                                    }
                                } catch (Exception e) {
                                }

                                //recupero il canale e lo cancello, dopo aver settato i valori lo reinserisco
                                Channel v = database.ChannelDao().findByName(channelID, READ_KEY);
                                if (v != null) database.ChannelDao().delete(v);

                                if (0 < fields.size() && fields.get(0) != null)
                                    v.setFiled1(fields.get(0));
                                else v.setFiled1(null);
                                if (1 < fields.size() && fields.get(1) != null)
                                    v.setFiled2(fields.get(1));
                                else v.setFiled2(null);
                                if (2 < fields.size() && fields.get(2) != null)
                                    v.setFiled3(fields.get(2));
                                else v.setFiled3(null);
                                if (3 < fields.size() && fields.get(3) != null)
                                    v.setFiled4(fields.get(3));
                                else v.setFiled4(null);
                                if (4 < fields.size() && fields.get(4) != null)
                                    v.setFiled5(fields.get(4));
                                else v.setFiled5(null);
                                if (5 < fields.size() && fields.get(5) != null)
                                    v.setFiled6(fields.get(5));
                                else v.setFiled6(null);
                                if (6 < fields.size() && fields.get(6) != null)
                                    v.setFiled7(fields.get(6));
                                else v.setFiled7(null);
                                if (7 < fields.size() && fields.get(7) != null)
                                    v.setFiled8(fields.get(7));
                                else v.setFiled8(null);
                                database.ChannelDao().insert(v);

                                Boolean ok = false;
                                Double irrigazione = 0.0;
                                Double drainaggio = 0.0;
                                String temperature = null;
                                String umidity = null;
                                String ph = null;
                                String conducibilita = null;
                                String irradianza = null;
                                String cretime=null;
                                String evapotraspirazione=null;

                                //scandisco tutti i 100 valori per trovare i valodi di irrigazione e il drenaggio
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject valori = jsonArray.getJSONObject(i);
                                    try {
                                        if (!valori.getString("field7").equals("") && !valori.getString("field7").equals("null") && fields.get(6).equals("Irrigation")) {
                                            ok = true;
                                            irrigazione = Double.parseDouble(valori.getString("field7"));
                                        }
                                    } catch (Exception e) {
                                    }

                                    try {
                                        if (!valori.getString("field8").equals("") && !valori.getString("field8").equals("null") && fields.get(7).equals("Drainage")) {
                                            ok = true;
                                            drainaggio = Double.parseDouble(valori.getString("field8"));
                                        }
                                    } catch (Exception e) {
                                    }

                                    try {
                                        //se ho impostato un valore, inserisci quello,altrimenti se già c'è uno standard prendilo in automatico altrimenti non scrivo nulla
                                        if (v.getImagetemp() != null && !valori.getString(v.getImagetemp()).equals("") && !valori.getString(v.getImagetemp()).equals("null"))
                                            temperature = valori.getString(v.getImagetemp());
                                        else if (fields.get(0).equals("Temperature") && !valori.getString("field1").equals("") && !valori.getString("field1").equals("null")) {
                                            temperature = valori.getString("field1");
                                        }
                                    } catch (Exception e) {
                                    }

                                    try {
                                        //se ho impostato un valore, inserisci quello,altrimenti se già c'è uno standard prendilo in automatico altrimenti non scrivo nulla
                                        if (v.getImageumid() != null && !valori.getString(v.getImageumid()).equals("") && !valori.getString(v.getImageumid()).equals("null"))
                                            umidity = valori.getString(v.getImageumid());
                                        else if (fields.get(1).equals("Humidity") && !valori.getString("field2").equals("") && !valori.getString("field2").equals("null")) {
                                            umidity = valori.getString("field2");
                                        }
                                    } catch (Exception e) {
                                    }

                                    try {
                                        //se ho impostato un valore, inserisci quello,altrimenti se già c'è uno standard prendilo in automatico altrimenti non scrivo nulla
                                        if (v.getImageph() != null && !valori.getString(v.getImageph()).equals("") && !valori.getString(v.getImageph()).equals("null"))
                                            ph = valori.getString(v.getImageph());
                                        else if (fields.get(2).equals("pH_value") && !valori.getString("field3").equals("") && !valori.getString("field3").equals("null")) {
                                            ph = valori.getString("field3");
                                        }
                                    } catch (Exception e) {
                                    }

                                    try {
                                        //se ho impostato un valore, inserisci quello,altrimenti se già c'è uno standard prendilo in automatico altrimenti non scrivo nulla
                                        if (v.getImagecond() != null && !valori.getString(v.getImagecond()).equals("") && !valori.getString(v.getImagecond()).equals("null"))
                                            conducibilita = valori.getString(v.getImagecond());
                                        else if (fields.get(3).equals("electric_conductivity") && !valori.getString("field4").equals("") && !valori.getString("field4").equals("null")) {
                                            conducibilita = valori.getString("field4");
                                        }
                                    } catch (Exception e) {
                                    }

                                    try {
                                        //se ho impostato un valore, inserisci quello,altrimenti se già c'è uno standard prendilo in automatico altrimenti non scrivo nulla
                                        if (v.getImageirra() != null && !valori.getString(v.getImageirra()).equals("") && !valori.getString(v.getImageirra()).equals("null"))
                                            irradianza = valori.getString(v.getImageirra());
                                        else if (fields.get(4).equals("Irradiance") && !valori.getString("field5").equals("") && !valori.getString("field5").equals("null")) {
                                            irradianza = valori.getString("field5");
                                        }
                                    } catch (Exception e) {
                                    }

                                    try {
                                        //se ho impostato un valore, inserisci quello,altrimenti se già c'è uno standard prendilo in automatico altrimenti non scrivo nulla
                                        if (v.getImagepeso() != null && !valori.getString(v.getImagepeso()).equals("") && !valori.getString(v.getImagepeso()).equals("null"))
                                            evapotraspirazione = valori.getString(v.getImagepeso());
                                        else if (ok) {
                                            evapotraspirazione=String.valueOf(Math.round((irrigazione - drainaggio) * 100.0) / 100.0);
                                            irradianza = valori.getString("field5");
                                        }
                                    } catch (Exception e) {
                                    }

                                    cretime = valori.getString("created_at");
                                }

                                //mostro a schermo gli ultimi valori
                                if (temperature != null){
                                    if (v.getImagetemp() != null) {
                                        textTemp.setText(String.valueOf(Math.round(Double.parseDouble(String.format(temperature)) * 100.0) / 100.0));
                                    } else if (fields.get(0).equals("Temperature")) {
                                        textTemp.setText(String.valueOf(Math.round(Double.parseDouble(String.format(temperature)) * 100.0) / 100.0).concat(" °C"));
                                    }
                                }
                                else textTemp.setText("- -");
                                if (umidity != null){
                                    if (v.getImageumid() != null) {
                                        textUmidity.setText(String.valueOf(Math.round(Double.parseDouble(String.format(umidity)) * 100.0) / 100.0));
                                    } else if (fields.get(1).equals("Humidity")) {
                                        textUmidity.setText(String.valueOf(Math.round(Double.parseDouble(String.format(umidity)) * 100.0) / 100.0));
                                    }
                                }
                                else textUmidity.setText("- -");
                                if (ph != null){
                                    if (v.getImageph() != null) {
                                        textPh.setText(String.valueOf(Math.round(Double.parseDouble(String.format(ph)) * 100.0) / 100.0));
                                    } else if (fields.get(2).equals("pH_value")) {
                                        textPh.setText(String.valueOf(Math.round(Double.parseDouble(String.format(ph)) * 100.0) / 100.0));
                                    }
                                }
                                else textPh.setText("- -");
                                if (conducibilita != null){
                                    if (v.getImagecond() != null) {
                                        textConducibilita.setText(String.valueOf(Math.round(Double.parseDouble(String.format(conducibilita)) * 100.0) / 100.0));
                                    } else if (fields.get(3).equals("electric_conductivity")) {
                                        textConducibilita.setText(String.valueOf(Math.round(Double.parseDouble(String.format(conducibilita)) * 100.0) / 100.0).concat(" dS/m"));
                                    }
                                }
                                else textConducibilita.setText("- -");
                                if (irradianza != null){
                                    if (v.getImageirra() != null) {
                                        textIrradianza.setText(String.valueOf(Math.round(Double.parseDouble(String.format(irradianza)) * 100.0) / 100.0));
                                    } else if (fields.get(4).equals("Irradiance")) {
                                        textIrradianza.setText(String.valueOf(Math.round(Double.parseDouble(String.format(irradianza)) * 100.0) / 100.0).concat(" w/m²"));
                                    }
                                }
                                else textIrradianza.setText("- -");
                                if (evapotraspirazione != null){
                                    if (v.getImagepeso() != null) {
                                        textPeso.setText(String.valueOf(Math.round(Double.parseDouble(String.format(evapotraspirazione)) * 100.0) / 100.0));
                                    } else if (ok) {
                                        textPeso.setText(evapotraspirazione);
                                    }
                                }
                                else textPeso.setText("- -");

                                distanza(cretime);

                                stato.setText("ONLINE");
                                stato.setTextColor(Color.GREEN);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                stato.setText("OFFLINE");
                                stato.setTextColor(Color.RED);
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    stato.setText("OFFLINE");
                    stato.setTextColor(Color.RED);
                }
            });
            Volley.newRequestQueue(context).add(jsonObjectRequest);

    }

    //scarico i dati dal server riguardante la configurazione dell'irrigazione
    private void donwload() {
        List<savedValues> lista=database.SavedDao().getAll();
        Channel list=database.ChannelDao().findByName(lista.get(0).getId(),lista.get(0).getRead_key());
        String url="https://api.thingspeak.com/channels/"+list.getScritt_id()+"/feeds.json?api_key="+list.getScritt_read_key()+"&results=1";

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("feeds");
                            JSONObject valori = jsonArray.getJSONObject(0);
                                if (!valori.getString("field7").equals("null")) {
                                    if(Double.parseDouble(valori.getString("field7"))==1){
                                        if(image!=null)image.setImageResource(R.drawable.irrigazioneattiva);
                                    }
                                    else{
                                        if(image!=null)image.setImageResource(R.drawable.irrigazione);
                                    }
                                }
                                else {
                                    if(image!=null)image.setImageResource(R.drawable.irrigazione);
                                }
                            } catch (Exception e) {
                            if(image!=null)image.setImageResource(R.drawable.irrigazione);
                            }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(image!=null) image.setImageResource(R.drawable.irrigazione);
            }
        });
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    private void distanza(String data) {
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

        //converto la data del cloud alla mia zona gmt
        date_value.setTimeZone(TimeZone.getTimeZone("GMT"));

        //durata in secondi
        long durata= (date_now.getTimeInMillis()/1000 - date_value.getTimeInMillis()/1000);
        long giorni1=(durata/86400);
        long temp=giorni1*86400;
        long ore1=(durata-temp)/3600;
        long minuti1=((durata-temp)-3600*ore1)/60;
        temp=(durata-temp)-3600*ore1;
        long secondi1=temp-(minuti1*60);

        text1.setText("Ultimo aggiornamento: " + giorni1 + " giorni " + ore1 + " ore " + minuti1 + " minuti " + secondi1+ " secondi ");
    }

    public static void updateDatabase(AppDatabase db){
        database=db;
    }

    }


