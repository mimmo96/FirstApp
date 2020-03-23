package com.example.firstapp.Alert;


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
    String url;


    public MyTimerTask(String url,Double tempMin,Double tempMax, Double umidMin, Double umidMax, Double condMin, Double condMax,
                       Double phMin,Double phMax,Double irraMin,Double irraMax,Double pesMin ,Double pesMax) {
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

                                    Double temperature = Double.parseDouble(value.getString("field1"));
                                    Double umidity = Double.parseDouble(value.getString("field2"));
                                    Double ph = Double.parseDouble(value.getString("field3"));
                                    Double conducibilita = Double.parseDouble(value.getString("field4"));
                                    Double irradianza = Double.parseDouble(value.getString("field5"));
                                    Double peso = Double.parseDouble(value.getString("field6"));

                                    if(temperature<tempMin) AlertActivity.printnotify("temperatura bassa!",1);
                                    if(temperature>tempMax) AlertActivity.printnotify("temperatura alta!",2);
                                    if(umidity<umidMin) AlertActivity.printnotify("umidità bassa!",3);
                                    if(umidity>umidMax) AlertActivity.printnotify("umidità alta!",4);
                                    if(conducibilita<condMin) AlertActivity.printnotify("conducibilità bassa!",5);
                                    if(conducibilita>condMax) AlertActivity.printnotify("conducibilità alta!",6);
                                    if(ph<phMin) AlertActivity.printnotify("ph basso!",7);
                                    if(ph>phMax) AlertActivity.printnotify("ph alto!",8);
                                    if(irradianza<irraMin) AlertActivity.printnotify("irradianza bassa!",9);
                                    if(irradianza>irraMax) AlertActivity.printnotify("irradianza alta!",10);
                                    if(peso<pesMin) AlertActivity.printnotify("peso basso!",11);
                                    if(peso>pesMax) AlertActivity.printnotify("peso alto!",12);

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AlertActivity.getContext(),"errore download!",Toast.LENGTH_SHORT).show();
                }
            });
            Volley.newRequestQueue(AlertActivity.getContext()).add(jsonObjectRequest);

    }


    }


