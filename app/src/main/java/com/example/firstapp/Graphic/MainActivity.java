package com.example.firstapp.Graphic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.firstapp.Channel.Channel;
import com.example.firstapp.R;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
 * Progetto: svilluppo App Android per Tirocinio interno
 *
 * Dipartimento di Informatica Università di Pisa
 *
 * Autore:Domenico Profumo matricola 533695
 * Si dichiara che il programma è in ogni sua parte, opera originale dell'autore
 *
 */

public class MainActivity extends AppCompatActivity {

    LineGraphSeries<DataPoint> series;
    private static String channelID="816869";
    private static String READ_KEY="KLEZNXOV7EPHHEUT";
    private static String url="https://api.thingspeak.com/channels/"+channelID+ "/feeds.json?api_key=" + READ_KEY;
    private RecyclerView recyclerView;
    private static ArrayList<String> nameFields;
    private static ArrayList<Integer> position;
    private static List<Channel> channelPos;
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
    private static int i=0;

    private List<ModelData> Insertdata=new ArrayList<>();

    //gra contiene la lista dei nomi dei field, channel il canale ad esso associato con tutte le info e pos indica la posizione del filed nel canale
    public static void setGrapView(ArrayList<String> nameList, List<Channel> channel, ArrayList<Integer> pos){
        nameFields=nameList;
        position=pos;
        channelPos=channel;
        position=pos;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphic_activity_main);
        recyclerView = findViewById(R.id.recyclerview);


        for(int i=0;i<channelPos.size();i++){
            url="https://api.thingspeak.com/channels/"+channelPos.get(i).getId()+"/feeds.json?api_key="+channelPos.get(i).getRead_key()+"&results=8000";
            getJsonResponse(url,i);
        }
    }

    //azione che deve avvenire quando premo sul pulsante vai
    public void visualizzaGrafici(View v){

        EditText dataStart=findViewById(R.id.editTextDatestart);
        EditText dataEnd=findViewById(R.id.editTextDateEnd);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); // Make sure user insert date into edittext in this format.

        Date dateObject;
        Date dateObject1;
        int giornoStart=0;
        int meseStart=0;
        int annoStart=0;
        int giornoEnd=0;
        int meseEnd=0;
        int annoEnd=0;
        int stop=0;
        try{
            //parsing data inizio
            String dob_var=(dataStart.getText().toString());
            dateObject = formatter.parse(dob_var);

            giornoStart=dateObject.getDate();
            if(giornoStart<0 || giornoStart >31) throw new ParseException("giorno non corretto",1);
            meseStart=dateObject.getMonth()+1;
            if(meseStart<0 ||  meseStart>12) throw new ParseException("mese non corretto",1);
            annoStart=dateObject.getYear()+1900;
            if(annoStart<2000) throw new ParseException("data non corretta",1);

            //parsing data fine
            String dob_var1=(dataEnd.getText().toString());
            dateObject1 = formatter.parse(dob_var1);

            giornoEnd=dateObject1.getDate();
            if(giornoEnd<0 || giornoEnd >31) throw new ParseException("giorno non corretto",2);
            meseEnd=dateObject1.getMonth()+1;
            if(meseEnd<0 ||  meseEnd>12) throw new ParseException("mese non corretto",2);
          //  if(giornoEnd==32){
          //      giornoEnd=1;
          //      meseEnd++;
          //  }
            annoEnd=dateObject1.getYear()+1900;
            if(annoEnd<2000) throw new ParseException("data non corretta",2);
          //  if(meseEnd==13){
          //      meseEnd=1;
          //      annoEnd++;
          //  }
        }

        catch (java.text.ParseException e)
        {
            stop=1;
            Toast.makeText(getApplicationContext(), "data inserita non corretta",Toast.LENGTH_SHORT).show();
            Log.i("Graphc/MainActivity", e.toString());
        }
        Log.i("Graphc/MainActivity","ho inserito:\n data inizio: " + giornoStart+"-"+meseStart+"-"+annoStart+"\n"+"data fine: "+ giornoEnd+"-"+meseEnd+"-"+annoEnd);

        if(stop==0) {
            Insertdata.clear();
            for (int i = 0; i < channelPos.size(); i++) {
                url = "https://api.thingspeak.com/channels/" + channelPos.get(i).getId() + "/feeds.json?api_key=" + channelPos.get(i).getRead_key() +
                        "&start=" + annoStart + "-" + meseStart + "-" + giornoStart + "%2000:00:00&end=" + annoEnd + "-" + meseEnd + "-" + giornoEnd + "%2000:00:00" + "&results=8000";
                getJsonResponse(url, i);
            }
        }
    }

    private void getJsonResponse(String url, final int index) {

        final JsonObjectRequest jsonObjectRequest;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Graphic/MainActivity", "download eseguito correttamente");
                        try {
                            //recupero l'array feeds
                            JSONArray jsonArray = response.getJSONArray("feeds");
                            String posfil="fields".concat(""+position.get(index));
                            //scorro tutto l'array e stampo a schermo il valore di field1
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //recupero il primo oggetto dell'array
                                final JSONObject value = jsonArray.getJSONObject(i);

                                //salvo i valori contenuti nei field1 di tipo double
                                try {
                                    if (posfil.equals("fields1") && !value.getString("field1").equals("") && !value.getString("field1").equals("null")) {
                                        fields1.add(Double.parseDouble(value.getString("field1")));
                                        date_fields1.add(value.getString("created_at"));
                                    }
                                }catch (Exception e){ }
                                try {
                                    if (posfil.equals("fields2") && !value.getString("field2").equals("") && !value.getString("field2").equals("null")) {
                                        fields2.add(Double.parseDouble(value.getString("field2")));
                                        date_fields2.add(value.getString("created_at"));
                                    }
                                }catch (Exception e){ }
                                try {
                                    if (posfil.equals("fields3") && !value.getString("field3").equals("") && !value.getString("field3").equals("null")) {
                                        fields3.add(Double.parseDouble(value.getString("field3")));
                                        date_fields3.add(value.getString("created_at"));
                                    }
                                }catch (Exception e){ }
                                try {
                                    if (posfil.equals("fields4") && !value.getString("field4").equals("") && !value.getString("field4").equals("null")) {
                                        fields4.add(Double.parseDouble(value.getString("field4")));
                                        date_fields4.add(value.getString("created_at"));
                                    }
                                }catch (Exception e){ }
                                try{
                                    if(posfil.equals("fields5") && !value.getString("field5").equals("") && !value.getString("field5").equals("null")){
                                        fields5.add(Double.parseDouble(value.getString("field5")));
                                        date_fields5.add(value.getString("created_at"));
                                    }
                                }catch (Exception e){ }
                                try {
                                    if (posfil.equals("fields6") && !value.getString("field6").equals("") && !value.getString("field6").equals("null")) {
                                        fields6.add(Double.parseDouble(value.getString("field6")));
                                        date_fields6.add(value.getString("created_at"));
                                    }
                                }catch (Exception e){ }
                                try{
                                    if(posfil.equals("fields7") && !value.getString("field7").equals("") && !value.getString("field7").equals("null")){
                                        fields7.add(Double.parseDouble(value.getString("field7")));
                                        date_fields7.add(value.getString("created_at"));
                                    }
                                }catch (Exception e){ }

                                try{
                                      if(posfil.equals("fields8") && !value.getString("field8").equals("") && !value.getString("field8").equals("null")){
                                         fields8.add(Double.parseDouble(value.getString("field8")));
                                         date_fields8.add(value.getString("created_at"));
                                }
                                  }catch (Exception e){ }
                            }

                            if(posfil.equals("fields1"))makegraph(nameFields.get(index),fields1,date_fields1);
                            if(posfil.equals("fields2"))makegraph(nameFields.get(index),fields2,date_fields2);
                            if(posfil.equals("fields3"))makegraph(nameFields.get(index),fields3,date_fields3);
                            if(posfil.equals("fields4"))makegraph(nameFields.get(index),fields4,date_fields4);
                            if(posfil.equals("fields5"))makegraph(nameFields.get(index),fields5,date_fields5);
                            if(posfil.equals("fields6"))makegraph(nameFields.get(index),fields6,date_fields6);
                            if(posfil.equals("fields7"))makegraph(nameFields.get(index),fields7,date_fields7);
                            if(posfil.equals("fields8"))makegraph(nameFields.get(index),fields8,date_fields8);

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            recyclerView.setLayoutManager(linearLayoutManager);

                            recyclerView.setAdapter(new RecyclerViewAdapter(Insertdata, context));
                            recyclerView.setHasFixedSize(true); //le cardView sono tutte delle stesse dimensioni
                            //libero tutta la memoria
                            fields1.clear();
                            date_fields1.clear();
                            fields2.clear();
                            date_fields2.clear();
                            fields3.clear();
                            date_fields3.clear();
                            fields4.clear();
                            date_fields4.clear();
                            fields5.clear();
                            date_fields5.clear();
                            fields7.clear();
                            date_fields6.clear();
                            fields6.clear();
                            date_fields7.clear();
                            fields8.clear();
                            date_fields8.clear();

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
        Double somma=0.0;
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
            somma=somma+list.get(i);
        }
        series = new LineGraphSeries<>(data);
        series.setColor(Color.RED);
        Double media=Math.round((somma/created.size()) * 100.0) / 100.0;
        Insertdata.add(new ModelData(name, series,media));
        date_value.clear();
    }

        public static Intent getActivityintent(Context context){
            Intent intent=new Intent(context, MainActivity.class);
            return intent;
        }

    public static void stampa() {
        System.out.println("Stampo nameFields :");
        for (int i = 0; i < nameFields.size(); i++) {
            System.out.println(i + ": " + nameFields.get(i));
        }
        System.out.println("FINE");
        System.out.println("Stampo position :");
        for (int i = 0; i < position.size(); i++) {
            System.out.println(i + ": " + position.get(i));
        }
        System.out.println("FINE");
        System.out.println("Stampo channelPos :");
        for (int i = 0; i < channelPos.size(); i++) {
            System.out.println(i + ": " + channelPos.get(i).getId());
        }
        System.out.println("FINE");
    }
}
