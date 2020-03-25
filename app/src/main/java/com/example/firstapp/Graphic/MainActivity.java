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
    private static ArrayList<String> fieldslist;
    private static ArrayList<Integer> position;
    private List<Double> fields1=new ArrayList<>();
    private List<Double> fields2=new ArrayList<>();
    private List<Double> fields3=new ArrayList<>();
    private List<Double> fields4=new ArrayList<>();
    private List<Double> fields5=new ArrayList<>();
    private List<Double> fields6=new ArrayList<>();
    private List<Double> fields7=new ArrayList<>();
    private List<Double> fields8=new ArrayList<>();
    private List<String> date_fields1=new ArrayList<>();
    private List<String> date_fields2=new ArrayList<>();
    private List<String> date_fields3=new ArrayList<>();
    private List<String> date_fields4=new ArrayList<>();
    private List<String> date_fields5=new ArrayList<>();
    private List<String> date_fields6=new ArrayList<>();
    private List<String> date_fields7=new ArrayList<>();
    private List<String> date_fields8=new ArrayList<>();
    private Context context=this;

    private List<ModelData> Insertdata=new ArrayList<>();

    public static void setGrapView(ArrayList<String> gra,ArrayList<Integer> pos,String id,String key){
        //lista contenente i nomi di tutti i fields da stampare
        fieldslist=gra;
        position=pos;
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
                                try {
                                    if (!value.getString("field1").equals("") && !value.getString("field1").equals("null")) {
                                        fields1.add(Double.parseDouble(value.getString("field1")));
                                        date_fields1.add(value.getString("created_at"));
                                    }
                                }catch (Exception e){ }
                                try {
                                    if (!value.getString("field2").equals("") && !value.getString("field2").equals("null")) {
                                        fields2.add(Double.parseDouble(value.getString("field2")));
                                        date_fields2.add(value.getString("created_at"));
                                    }
                                }catch (Exception e){ }
                                try {
                                    if (!value.getString("field3").equals("") && !value.getString("field3").equals("null")) {
                                        fields3.add(Double.parseDouble(value.getString("field3")));
                                        date_fields3.add(value.getString("created_at"));
                                    }
                                }catch (Exception e){ }
                                try {
                                    if (!value.getString("field4").equals("") && !value.getString("field4").equals("null")) {
                                        fields4.add(Double.parseDouble(value.getString("field4")));
                                        date_fields4.add(value.getString("created_at"));
                                    }
                                }catch (Exception e){ }
                                try{
                                    if(!value.getString("field5").equals("") && !value.getString("field5").equals("null")){
                                        fields5.add(Double.parseDouble(value.getString("field5")));
                                        date_fields5.add(value.getString("created_at"));
                                    }
                                }catch (Exception e){ }
                                try {
                                    if (!value.getString("field6").equals("") && !value.getString("field6").equals("null")) {
                                        fields6.add(Double.parseDouble(value.getString("field6")));
                                        date_fields6.add(value.getString("created_at"));
                                    }
                                }catch (Exception e){ }
                                try{
                                    if(!value.getString("field7").equals("") && !value.getString("field7").equals("null")){
                                        fields7.add(Double.parseDouble(value.getString("field7")));
                                        date_fields7.add(value.getString("created_at"));
                                    }
                                }catch (Exception e){ }

                                try{
                                      if(!value.getString("field8").equals("") && !value.getString("field8").equals("null")){
                                         fields8.add(Double.parseDouble(value.getString("field8")));
                                          date_fields8.add(value.getString("created_at"));
                                }
                                  }catch (Exception e){ }

                            }

                            //stampa();

                            for (int i = 0; i < position.size(); i++) {
                                int pos=position.get(i);
                                String posfil="fields".concat(""+pos);
                                if(posfil.equals("fields0"))makegraph(fieldslist.get(i),fields1,date_fields1);
                                if(posfil.equals("fields1"))makegraph(fieldslist.get(i),fields2,date_fields2);
                                if(posfil.equals("fields2"))makegraph(fieldslist.get(i),fields3,date_fields3);
                                if(posfil.equals("fields3"))makegraph(fieldslist.get(i),fields4,date_fields4);
                                if(posfil.equals("fields4"))makegraph(fieldslist.get(i),fields5,date_fields5);
                                if(posfil.equals("fields5"))makegraph(fieldslist.get(i),fields6,date_fields6);
                                if(posfil.equals("fields6"))makegraph(fieldslist.get(i),fields7,date_fields7);
                                if(posfil.equals("fields7"))makegraph(fieldslist.get(i),fields8,date_fields8);
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

    private void makegraph(String name, List<Double> list,List<String> created) {

        DataPoint[] data = new DataPoint[created.size()];

        Calendar date_value = Calendar.getInstance();

        for (int i = 0; i < created.size(); i++) {
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

    public static void stampa() {
        for (int i = 0; i < fieldslist.size(); i++) {
            System.out.println(i + ": " + fieldslist.get(i));
        }

        for (int i = 0; i < position.size(); i++) {
            System.out.println(i + ": " + position.get(i));
        }
    }
}
