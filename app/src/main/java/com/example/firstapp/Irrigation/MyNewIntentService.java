package com.example.firstapp.Irrigation;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.firstapp.MainActivity;
import com.example.firstapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyNewIntentService extends IntentService {
    private static final int NOTIFICATION_ID = 3;
    public static Double pesoPrec=0.0;
    public static Double pesoAtt=0.0;
    public static Double leaching=0.0;
    public static Double flusso=0.0;
    private Context cont;

    public MyNewIntentService() {
        super("MyNewIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        cont=getApplicationContext();
        donwload();
    }

    private void donwload() {
        String url="https://api.thingspeak.com/channels/816869/feeds.json?api_key=KLEZNXOV7EPHHEUT&results=100";

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

                            Double peso = 0.0;
                            Double irrigazione=0.0;
                            Double lastpeso=0.0;
                            boolean ok=false;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject valori = jsonArray.getJSONObject(i);
                                try {
                                    if (!valori.getString("field7").equals("") && !valori.getString("field7").equals("null")) {
                                        ok=true;
                                        irrigazione=Double.parseDouble(valori.getString("field7"));
                                    }
                                }catch (Exception e){ }

                                if(ok) {
                                    try {
                                        if (!valori.getString("field6").equals("") && !valori.getString("field6").equals("null")) {
                                            Double newpeso = Double.parseDouble(valori.getString("field6"));
                                            if(newpeso> peso){
                                                peso =newpeso;

                                            }
                                            lastpeso= Double.parseDouble(valori.getString("field6"));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }
                            pesoPrec=peso;
                            pesoAtt=lastpeso;
                            Log.d("PESO","peso prima: " + peso +" peso Adesso: " + lastpeso);
                            double min=calcolominuti(pesoPrec,pesoAtt);
                            Log.d("MINUTI NECESSARI:", String.valueOf(min));
                            //invio i minuti necessari al server
                            sendvalue(String.valueOf(min),"AUTOMATICA");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(cont,"ERRORE DATI",Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(cont).add(jsonObjectRequest);
    }

    //invio i dati al server
    private void sendvalue(final String value, final String tipo) {

        String url = "https://api.thingspeak.com/update.json";

        Map<String, String> params = new HashMap();
        params.put("accept", "application/json");
        params.put("api_key", "PAG5TFQPULRTH8RY");
        params.put("field1",value);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                sendnotification("DURATA IRRIGAZIONE: " + value);
               if(getBaseContext()!=null)  Toast.makeText(getBaseContext(),"IRRIGAZIONE"+tipo+" ATTTIVATA!",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sendnotification("ERRORE INVIO DATI");
                if(getBaseContext()!=null)  Toast.makeText(getBaseContext(),"ERRORE IRRIGAZIONE " +tipo+"!",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }


    private void sendnotification(String minutes){
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("IRRIGAZIONE");
        builder.setContentText(minutes);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        Intent notifyIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, notificationCompat);
    }

    private static double calcolominuti(Double pesoPrec,Double pesoAtt) {
        //durata = -Delta P0*(1+leaching_factor)/flusso
        Double deltaP0=pesoPrec-pesoAtt;
        deltaP0=Math.round(deltaP0 * 100.0) / 100.0;

        Double durata=deltaP0*(1+leaching)/flusso;

        //arrotondo con due cifre dopo la virgola
        durata=Math.round(durata * 100.0) / 100.0;

        return durata;
    }

    public static void settingvalue( Double leaching1, Double flusso1){
        leaching=leaching1;
        flusso=flusso1;

    }

}