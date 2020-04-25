package com.example.firstapp;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.firstapp.Alert.AlertActivity;
import com.example.firstapp.Channel.Channel;
import com.example.firstapp.Channel.SavedDao;
import com.example.firstapp.Channel.savedValues;

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
    String url;
    Context context;
    private static String channelID=null;
    private static String READ_KEY=null;
    private static AppDatabase database;


    public MyTimerTask(String id, String key,String url, TextView textTemp1,TextView textUmidity1, TextView textPh1, TextView textConducibilita1,
                       TextView textIrradianza1,TextView textPO1,TextView stato,TextView testo1, Context cont,AppDatabase database) {
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
    }

    @Override
    public void run() {
       getJsonResponse(url);
    }

    //metodo per reperire le risposte json
     private void getJsonResponse (final String url){
        //se non ho nessun url inserita setto i valori a 0

            final List<String> createdtime = new ArrayList<>();

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //recupero l'array feeds
                                JSONArray jsonArray = response.getJSONArray("feeds");

                                //recupero i fields associati al channel
                                ArrayList<String> fields=new ArrayList<String>();
                                int dim=response.getJSONObject("channel").length();

                                //salvo tutti i nomi dei field nell'array
                                try {
                                    for(int i=0;i<dim;i++) {
                                        fields.add(String.valueOf(response.getJSONObject("channel").get("field" + (i+1))));
                                    }
                                }catch(Exception e) {

                                }

                                //recupero il canale e lo cancello, dopo aver settato i valori lo reinserisco
                                Channel v=database.ChannelDao().findByName(channelID,READ_KEY);
                                if(v!=null) database.ChannelDao().delete(v);

                                if (0<fields.size() && fields.get(0) != null) v.setFiled1(fields.get(0));
                                else v.setFiled1(null);
                                if (1<fields.size() && fields.get(1) != null) v.setFiled2(fields.get(1));
                                else v.setFiled2(null);
                                if (2<fields.size() && fields.get(2) != null) v.setFiled3(fields.get(2));
                                else v.setFiled3(null);
                                if (3<fields.size() && fields.get(3) != null) v.setFiled4(fields.get(3));
                                else v.setFiled4(null);
                                if (4<fields.size() && fields.get(4) != null) v.setFiled5(fields.get(4));
                                else v.setFiled5(null);
                                if (5<fields.size() && fields.get(5) != null) v.setFiled6(fields.get(5));
                                else v.setFiled6(null);
                                if (6<fields.size() && fields.get(6) != null) v.setFiled7(fields.get(6));
                                else v.setFiled7(null);
                                if (7<fields.size() && fields.get(7) != null) v.setFiled8(fields.get(7));
                                else v.setFiled8(null);
                                database.ChannelDao().insert(v);

                                int size=jsonArray.length()-1;
                                //scorro tutto l'array di valori che ho ricevuto
                                    //recupero il primo oggetto dell'array
                                    final JSONObject value = jsonArray.getJSONObject(size);

                                    try {
                                        String temperature = value.getString("field1");
                                        //se ho impostato un valore, inserisci quello,altrimenti se già c'è uno standard prendilo in automatico almenti non scrivo nullo
                                        if(v.getImagetemp()!=null){
                                            String field=value.getString(v.getImagetemp());
                                            textTemp.setText(String.valueOf(Math.round(Double.parseDouble(String.format(field)) * 100.0) / 100.0));
                                        }
                                        else if (fields.get(0).equals("Temperature")) textTemp.setText(String.valueOf(Math.round(Double.parseDouble(String.format(temperature)) * 100.0) / 100.0).concat(" °C"));
                                    }catch (Exception e){
                                        textTemp.setText("- -");
                                    }
                                    try{
                                        String umidity = value.getString("field2");
                                        if(v.getImageumid()!=null){
                                            String field=value.getString(v.getImageumid());
                                            textUmidity.setText(String.valueOf(Math.round(Double.parseDouble(String.format(field)) * 100.0) / 100.0));
                                        }
                                        else if(fields.get(1).equals("Humidity")) textUmidity.setText(String.valueOf(Math.round(Double.parseDouble(String.format(umidity)) * 100.0) / 100.0));
                                    }catch (Exception e){
                                        textUmidity.setText("- -");
                                    }
                                    try {
                                        String ph = value.getString("field3");
                                        if(v.getImageph()!=null){
                                            String field=value.getString(v.getImageph());
                                            textPh.setText(String.valueOf(Math.round(Double.parseDouble(String.format(field)) * 100.0) / 100.0));
                                        }
                                       else if (fields.get(2).equals("pH_value")) textPh.setText(String.valueOf(Math.round(Double.parseDouble(String.format(ph)) * 100.0) / 100.0));
                                    }catch (Exception e){
                                        textPh.setText("- -");
                                    }
                                    try {
                                        String conducibilita = value.getString("field4");
                                        if(v.getImagecond()!=null){
                                            String field=value.getString(v.getImagecond());
                                            textConducibilita.setText(String.valueOf(Math.round(Double.parseDouble(String.format(field)) * 100.0) / 100.0));
                                        }
                                        else if (fields.get(3).equals("electric_conductivity")) textConducibilita.setText(String.valueOf(Math.round(Double.parseDouble(String.format(conducibilita)) * 100.0) / 100.0).concat(" dS/m"));
                                    }catch (Exception e){
                                        textConducibilita.setText("- -");
                                    }
                                    try {
                                        String irradianza = value.getString("field5");
                                        if(v.getImageirra()!=null){
                                            String field=value.getString(v.getImageirra());
                                            textIrradianza.setText(String.valueOf(Math.round(Double.parseDouble(String.format(field)) * 100.0) / 100.0));
                                        }
                                        else if (fields.get(4).equals("Irradiance")) textIrradianza.setText(String.valueOf(Math.round(Double.parseDouble(String.format(irradianza)) * 100.0) / 100.0).concat(" w/m²"));
                                    }catch (Exception e){
                                        textIrradianza.setText("- -");
                                    }

                                    Boolean ok=false;
                                    Double irrigazione =0.0;
                                    Double drainaggio = 0.0;

                                    //scandisco tutti i 100 valori per trovare i valodi di irrigazione e il drenaggio
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject valori = jsonArray.getJSONObject(i);
                                    try {
                                        if (!valori.getString("field7").equals("") && !valori.getString("field7").equals("null")) {
                                            ok=true;
                                            irrigazione=Double.parseDouble(valori.getString("field7"));
                                        }
                                    }catch (Exception e){ }

                                    try {
                                        if (!valori.getString("field8").equals("") && !valori.getString("field8").equals("null")) {
                                            ok=true;
                                            drainaggio=Double.parseDouble(valori.getString("field8"));
                                        }
                                    }catch (Exception e){ }

                                }

                                try {
                                    if (v.getImagepeso() != null) {
                                        String field = value.getString(v.getImagepeso());
                                        textPeso.setText(String.valueOf(Math.round(Double.parseDouble(String.format(field)) * 100.0) / 100.0));
                                    } else if (ok)
                                        textPeso.setText(String.valueOf(Math.round((irrigazione - drainaggio) * 100.0) / 100.0).concat(" g/m²"));
                                    else textPeso.setText("- -");
                                } catch (Exception e){ textPeso.setText("- -"); }

                                String cretime = value.getString("created_at");
                                createdtime.add(cretime);
                                distanza(cretime);
                                String evap=textPeso.getText().toString();
                                //nota:creatime è in gmt00
                                AlertActivity.setminutes(cretime,evap);
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

    public static void stampa() {

        List<Channel> arrayList = database.ChannelDao().getAll();
        System.out.println("stampo il database cannel");
        for(int i=0;i<arrayList.size();i++) System.out.println(arrayList.get(i).getId() +" --" + arrayList.get(i).getFiled1() +" --" + arrayList.get(i).getFiled2()
                +" --" + arrayList.get(i).getFiled3() +" --" + arrayList.get(i).getFiled4() +" --" + arrayList.get(i).getFiled5()
                +" --" + arrayList.get(i).getFiled6()+" --" + arrayList.get(i).getFiled7()
                +" --" + arrayList.get(i).getFiled8() +"--" + arrayList.get(i).getRead_key() );

        System.out.println("FINE");

        List <savedValues> arrayList1 = database.SavedDao().getAll();
        System.out.println("stampo il database saved");
        for(int i=0;i<arrayList1.size();i++) System.out.println(arrayList1.get(i).getId() +" --" + arrayList1.get(i).getPosition() +" --" + arrayList1.get(i).getRead_key() );

        System.out.println("FINE");



    }

    }


