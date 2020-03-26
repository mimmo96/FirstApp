package com.example.firstapp.Alert;


import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

    private  Double tempMin;
    private  Double tempMax;
    private  Double umidMin;
    private  Double umidMax;
    private  Double condMin;
    private  Double condMax;
    private  Double phMin;
    private  Double phMax;
    private  Double irraMin;
    private  Double irraMax;
    private  Double pesMin;
    private  Double pesMax;
    private TextView temp;
    private TextView umid;
    private TextView ph;
    private TextView cond;
    private TextView irra;
    private TextView peso;
    private static Context cont=AlertActivity.getContext();

    String url;


    public MyTimerTask(String url,Double tempMin,Double tempMax, Double umidMin, Double umidMax, Double condMin, Double condMax,
                       Double phMin,Double phMax,Double irraMin,Double irraMax,Double pesMin ,Double pesMax,TextView temp,
                       TextView umid, TextView ph, TextView cond, TextView irra, TextView peso) {
        this.tempMin=tempMin;
        this.tempMax=tempMax;
        this.umidMin=umidMin;
        this.umidMax=umidMax;
        this.condMin=condMin;
        this.condMax=condMax;
        this.phMin=phMin;
        this.phMax=phMax;
        this.irraMin=irraMin;
        this.irraMax=irraMax;
        this.pesMin=pesMin;
        this.pesMax=pesMax;
        this.url=url;
        this.temp=temp;
        this.umid=umid;
        this.irra=irra;
        this.peso=peso;
        this.ph=ph;
        this.cond=cond;
    }

    @Override
    public void run() {
        getJsonResponse(url);
    }

    //metodo per reperire le risposte json
     private void getJsonResponse (String url){
        //se non ho nessun url inserita setto i valori a 0

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
                                Toast.makeText(AlertActivity.getContext(),"download eseguito!",Toast.LENGTH_SHORT).show();
                                //salvo tutti i field nell'array
                                try {
                                    for(int i=0;i<dim;i++) {
                                        fields.add(String.valueOf(response.getJSONObject("channel").get("field" + (i+1))));
                                    }
                                }catch(Exception e) {

                                }


                                //scorro tutto l'array e stampo a schermo il valore di field1
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    //recupero il primo oggetto dell'array
                                    final JSONObject value = jsonArray.getJSONObject(i);

                                    String temperature = value.getString("field1");
                                    String umidity = value.getString("field2");
                                    String ph1 = value.getString("field3");
                                    String conducibilita = value.getString("field4");
                                    String irradianza = value.getString("field5");
                                    String peso1 = value.getString("field6");
                                    Double t=0.0;
                                    Double u=0.0;
                                    Double p=0.0;
                                    Double c=0.0;
                                    Double ir=0.0;
                                    Double pe=0.0;
                                    if(fields.get(0).equals("Temperature")){
                                        t=Math.round(Double.parseDouble(String.format(temperature)) * 100.0) / 100.0;
                                        temp.setText(String.valueOf(t));
                                    }
                                    else temp.setText("- -");
                                    if(fields.get(1).equals("Humidity")){
                                        u=Math.round(Double.parseDouble(String.format(umidity)) * 100.0) / 100.0;
                                        umid.setText(String.valueOf(u));
                                    }
                                    else umid.setText("- -");
                                    if(fields.get(2).equals("pH_value")){
                                        p=Math.round(Double.parseDouble(String.format(umidity)) * 100.0) / 100.0;
                                        ph.setText(String.valueOf(p));
                                    }
                                    else ph.setText("- -");
                                    if(fields.get(3).equals("electric_conductivity")){
                                        c=Math.round(Double.parseDouble(String.format(conducibilita)) * 100.0) / 100.0;
                                        cond.setText(String.valueOf(c));
                                    }
                                    else cond.setText("- -");
                                    if(fields.get(4).equals("Irradiance")){
                                        ir=Math.round(Double.parseDouble(String.format(irradianza)) * 100.0) / 100.0;
                                        irra.setText(String.valueOf(ir));
                                    }
                                    else irra.setText("- -");
                                    if(fields.get(5).equals("P0")){
                                        pe=Math.round(Double.parseDouble(String.format(irradianza)) * 100.0) / 100.0;
                                        peso.setText(String.valueOf(pe).concat(" g"));
                                    }
                                    else peso.setText("- -");

                                    if(t<tempMin && tempMin!=0.0) AlertActivity.printnotify("temperatura bassa!",1);
                                    if(t>tempMax && tempMax!=0.0) AlertActivity.printnotify("temperatura alta!",2);
                                    if(u<umidMin && umidMin!=0.0) AlertActivity.printnotify("umidità bassa!",3);
                                    if(u>umidMax && umidMax!=0.0) AlertActivity.printnotify("umidità alta!",4);
                                    if(c<condMin && condMin!=0.0) AlertActivity.printnotify("conducibilità bassa!",5);
                                    if(c>condMax && condMax!=0.0) AlertActivity.printnotify("conducibilità alta!",6);
                                    if(p<phMin && phMin!=0.0) AlertActivity.printnotify("ph basso!",7);
                                    if(p>phMax && phMax!=0.0) AlertActivity.printnotify("ph alto!",8);
                                    if(ir<irraMin && irraMin!=0.0) AlertActivity.printnotify("irradianza bassa!",9);
                                    if(ir>irraMax && irraMax!=0.0) AlertActivity.printnotify("irradianza alta!",10);
                                    if(pe<pesMin && pesMin!=0.0) AlertActivity.printnotify("peso basso!",11);
                                    if(pe>pesMax && pesMax!=0.0) AlertActivity.printnotify("peso alto!",12);

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(cont,"errore download!",Toast.LENGTH_SHORT).show();
                }
            });
            Volley.newRequestQueue(cont).add(jsonObjectRequest);

    }


    }


