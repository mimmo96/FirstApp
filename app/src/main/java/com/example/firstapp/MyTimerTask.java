package com.example.firstapp;


import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

public class MyTimerTask extends TimerTask {

    TextView textTemp;
    TextView textUmidity;
    TextView textPh;
    TextView textConducibilita;
    TextView textIrradianza;
    TextView textPeso;
    TextView text1;
    TextView stato;
    String url;
    Context context;
    private static String channelID=null;
    private static String READ_KEY=null;


    public MyTimerTask(String url, TextView textTemp1,TextView textUmidity1, TextView textPh1, TextView textConducibilita1,
                       TextView textIrradianza1,TextView textPO1,TextView stato,TextView testo1, Context cont) {
        textTemp=textTemp1;
        textUmidity=textUmidity1;
        textPh=textPh1;
        textConducibilita=textConducibilita1;
        textIrradianza=textIrradianza1;
        textPeso=textPO1;
        this.stato=stato;
        text1=testo1;
        this.url=url;
        context=cont;
    }

    @Override
    public void run() {
       getJsonResponse(url);
    }

    //metodo per reperire le risposte json
     private void getJsonResponse (String url){
        //se non ho nessun url inserita setto i valori a 0

            final List<String> createdtime = new ArrayList<>();


            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //recupero l'array feeds
                                JSONArray jsonArray = response.getJSONArray("feeds");

                                //scorro tutto l'array e stampo a schermo il valore di field1
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    //recupero il primo oggetto dell'array
                                    final JSONObject value = jsonArray.getJSONObject(i);


                                    String temperature = value.getString("field1");
                                    String umidity = value.getString("field2");
                                    String ph = value.getString("field3");
                                    String conducibilita = value.getString("field4");
                                    String irradianza = value.getString("field5");
                                    String peso = value.getString("field6");

                                    textTemp.setText(String.valueOf(Math.round(Double.parseDouble(String.format(temperature)) * 100.0) / 100.0));
                                    textUmidity.setText(String.valueOf(Math.round(Double.parseDouble(String.format(umidity)) * 100.0) / 100.0));
                                    textPh.setText(String.valueOf(Math.round(Double.parseDouble(String.format(ph)) * 100.0) / 100.0));
                                    textConducibilita.setText(String.valueOf(Math.round(Double.parseDouble(String.format(conducibilita)) * 100.0) / 100.0));
                                    textIrradianza.setText(String.valueOf(Math.round(Double.parseDouble(String.format(irradianza)) * 100.0) / 100.0));
                                    textPeso.setText(String.valueOf(Math.round(Double.parseDouble(String.format(peso)) * 100.0) / 100.0).concat(" g"));


                                    String cretime = value.getString("created_at");
                                    createdtime.add(cretime);
                                    distanza(cretime);
                                }
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

    private void distanza(String data) {

        Calendar date_now= Calendar.getInstance ();
        Calendar date_value = Calendar.getInstance ();

        //parsing della data

        int giorno=Integer.valueOf(data.substring(8, 10));
        int mese=Integer.valueOf(data.substring(5, 7));
        int anno=Integer.valueOf(data.substring(0, 4));
        int ore=Integer.valueOf(data.substring(11, 13));
        int minuti=Integer.valueOf(data.substring(14, 16));
        int secondi=Integer.valueOf(data.substring(17, 19));


        date_value.set(Calendar.YEAR,anno);
        date_value.set(Calendar.MONTH,mese-1);
        date_value.set(Calendar.DAY_OF_MONTH,giorno);
        date_value.set (Calendar.HOUR_OF_DAY,ore);
        date_value.set (Calendar.MINUTE,minuti);
        date_value.set (Calendar.SECOND, secondi);


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

    }


