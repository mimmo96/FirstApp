package com.example.firstapp.Graphic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.firstapp.Channel.ChannelActivity;
import com.example.firstapp.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    LineGraphSeries<DataPoint> series;
    private static String channelID="816869";
    private static String READ_KEY="KLEZNXOV7EPHHEUT";
    private static String url="https://api.thingspeak.com/channels/"+channelID+ "/feeds.json?api_key=" + READ_KEY;
    private RecyclerView recyclerView;
    private static ArrayList<String> graphView;
    private List<Double> temp=new ArrayList<>();
    private List<Double> umid=new ArrayList<>();
    private List<Double> ph=new ArrayList<>();
    private List<Double> cond=new ArrayList<>();
    private List<Double> irra=new ArrayList<>();
    private List<Double> po=new ArrayList<>();
    private Context context=this;

    final List<String> created=new ArrayList<>();
    private List<ModelData> Insertdata=new ArrayList<>();

    public static void setGrapView(ArrayList<String> gra,String id,String key){
        graphView=gra;
        url="https://api.thingspeak.com/channels/"+id+ "/feeds.json?api_key=" + key+"&results=8000";
        channelID=id;
        READ_KEY=key;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphic_activity_main);

        recyclerView = findViewById(R.id.recyclerview);

        getJsonResponse(url);


    }

    private void getJsonResponse(String url) {

        final JsonObjectRequest jsonObjectRequest;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MainActivity", "download eseguito correttamente");
                        Toast x= Toast.makeText(getApplicationContext(),"download eseguito correttamente",Toast.LENGTH_SHORT);
                        x.show();
                        try {
                            //recupero l'array feeds
                            JSONArray jsonArray = response.getJSONArray("feeds");

                            //scorro tutto l'array e stampo a schermo il valore di field1
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //recupero il primo oggetto dell'array
                                final JSONObject value = jsonArray.getJSONObject(i);

                                //salvo i valori contenuti nei field1 di tipo double
                                if(!value.getString("field1").equals("") && !value.getString("field1").equals("null"))
                                    temp.add(Double.parseDouble(value.getString("field1")));

                                if(!value.getString("field2").equals("") && !value.getString("field2").equals("null"))
                                     umid.add(Double.parseDouble(value.getString("field2")));

                                if(!value.getString("field3").equals("") && !value.getString("field3").equals("null"))
                                    ph.add(Double.parseDouble(value.getString("field3")));

                                if(!value.getString("field4").equals("") && !value.getString("field4").equals("null"))
                                    cond.add(Double.parseDouble(value.getString("field4")));

                                if(!value.getString("field5").equals("") && !value.getString("field5").equals("null"))
                                    irra.add(Double.parseDouble(value.getString("field5")));

                                if(!value.getString("field6").equals("") && !value.getString("field6").equals("null"))
                                    po.add(Double.parseDouble(value.getString("field6")));

                                created.add(value.getString("created_at"));

                            }

                            for (int i = 0; i < graphView.size(); i++) {
                                if(graphView.get(i).equals("temperature"))  makegraph("temperature",temp);
                                if(graphView.get(i).equals("ph"))  makegraph("ph",ph);
                                if(graphView.get(i).equals("umidità"))  makegraph("umidità",umid);
                                if(graphView.get(i).equals("conducibilità elettrica"))  makegraph("conducibilità elettrica",cond);
                                if(graphView.get(i).equals("irradianza"))  makegraph("irradianza",irra);
                                if(graphView.get(i).equals("peso"))  makegraph("peso",po);
                            }

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            recyclerView.setLayoutManager(linearLayoutManager);

                            recyclerView.setAdapter(new RecyclerViewAdapter(Insertdata, context));
                            recyclerView.setHasFixedSize(true); //le cardView sono tutte delle stesse dimensioni

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("MainActivity", "Errore nel download");
                Toast x= Toast.makeText(getApplicationContext(),"Errore download",Toast.LENGTH_SHORT);
                x.show();
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
    }

    private void makegraph(String name, List<Double> list) {

        DataPoint[] data = new DataPoint[list.size()];

        Calendar date_value = Calendar.getInstance();

        for (int i = 0; i < list.size(); i++) {
            String data_creazione = created.get(i);

            int giorno = Integer.valueOf(data_creazione.substring(8, 10));
            int mese = Integer.valueOf(data_creazione.substring(5, 7));
            int anno = Integer.valueOf(data_creazione.substring(0, 4));
            int ore = Integer.valueOf(data_creazione.substring(11, 13));
            int minuti = Integer.valueOf(data_creazione.substring(14, 16));
            int secondi = Integer.valueOf(data_creazione.substring(17, 19));


            date_value.set(Calendar.YEAR, anno);
            date_value.set(Calendar.MONTH, mese - 1);
            date_value.set(Calendar.DAY_OF_MONTH, giorno);
            date_value.set(Calendar.HOUR_OF_DAY, ore);
            date_value.set(Calendar.MINUTE, minuti);
            date_value.set(Calendar.SECOND, secondi);
            Date dat = date_value.getTime();
            data[i] = new DataPoint(dat, list.get(i));

        }
        series = new LineGraphSeries<>(data);

        Insertdata.add(new ModelData(name, series));

    }

        public static Intent getActivityintent(Context context){
        Intent intent=new Intent(context, MainActivity.class);
        return intent;
    }
}
