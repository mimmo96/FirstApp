package com.example.GreenApp.Irrigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.GreenApp.AppDatabase;
import com.example.GreenApp.Channel.Channel;
import com.example.GreenApp.Channel.savedValues;
import com.example.firstapp.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class IrrigationActivity extends AppCompatActivity {
    private static AppDatabase db;
    private static EditText durationText;
    private static EditText flussoText;
    private static EditText leachingText;
    private static EditText irradayText;
    private static TextView textDurata;
    private static TextView Durata;
    private static Channel channel;
    private static Switch Switch;
    private static Button irra;
    private static Context cont;
    private static ImageView image;
    private Double leaching=null;
    private Double flusso=null;
    private int numirra=-1;
    private boolean check1=false;
    private boolean check2=false;
    private RequestQueue queue;
    private int field7=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.irrigation_activity);

        if(savedInstanceState==null) {
            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "prodiction")
                    //consente l'aggiunta di richieste nel thred principale
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    //build mi serve per costruire il tutto
                    .build();
        }

        queue=Volley.newRequestQueue(this);

        durationText=findViewById(R.id.editTextDuration);
        flussoText=findViewById(R.id.editTextFlusso);
        leachingText=findViewById(R.id.editTextLeaching);
        irradayText=findViewById(R.id.editTextIrraDay);
        Switch= findViewById(R.id.switch1);
        irra=findViewById(R.id.buttonIrra);
        textDurata=findViewById(R.id.textViewduration);
        Durata=findViewById(R.id.textDurationValues);
        image=findViewById(R.id.imageView4);

        cont=getApplicationContext();

        //recupero i dati dal server
        donwload();

        //azione quando clicco sull'irrigazione automatica
        Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //appena l'irrigazione è attiva
                if (isChecked){
                    if(check2) {
                        //mando i dati al server
                        irrigationOn(flussoText.getText().toString(), leachingText.getText().toString(), irradayText.getText().toString(), "AUTOMATICA");
                    }
                }
                else{
                    //comunico al server di interrompere l'irrigazione
                    if(check1) {
                        irrigationOff();
                    }
                }
            }
        });

        //azione quando clicco sul bottone per attivare l'irrigazione manuale
        irra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendvalue(durationText.getText().toString(),"MANUALE");
            }
        });
    }

    //invio i dati al server (in caso di attivazione manuale)
    private void sendvalue(String value, final String tipo) {

        String url = "https://api.thingspeak.com/update.json";
        List<savedValues> lista=db.SavedDao().getAll();
        Channel list=db.ChannelDao().findByName(lista.get(0).getId(),lista.get(0).getRead_key());
        if(list.getWrite_key()!=null) Log.d("WRITE KEY",list.getWrite_key());
        if(list.getWrite_key()==null || list.getWrite_key().equals("")) {
            Toast.makeText(getBaseContext(),"CHIAVE SCRITTURA ERRATA",Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap();
        params.put("accept", "application/json");
        params.put("api_key", list.getWrite_key());
        params.put("field1",value);
        params.put("field2",flussoText.getText().toString());
        params.put("field3",leachingText.getText().toString());
        params.put("field4", irradayText.getText().toString());
        if(Switch.isChecked()) params.put("field6","1");
        else params.put("field6","0");
        params.put("field7",String.valueOf(field7));

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getBaseContext(),"IRRIGAZIONE "+tipo+" ATTTIVATA!",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),"ERRORE ATTIVAZIONE IRRIGAZIONE " +tipo+" RIPROVA TRA 15 SECONDI O CONTROLLA LA TUA CONNESSIONE!",Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });

       // jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
       //         15000,
       //        4,
       //         DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonRequest);
    }

    //invio i dati al server (in caso di attivazione automatica)
    private void irrigationOn(String flusso,String Leaching,String numirra,  final String tipo) {
        String url = "https://api.thingspeak.com/update.json";
        List<savedValues> lista=db.SavedDao().getAll();
        Channel list=db.ChannelDao().findByName(lista.get(0).getId(),lista.get(0).getRead_key());
        Log.d("WRITE KEY",list.getWrite_key());
        if(list==null || list.getWrite_key()==null || list.getWrite_key().equals("")) {
            Toast.makeText(getBaseContext(),"CHIAVE SCRITTURA ERRATA",Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap();
        params.put("accept", "application/json");
        params.put("api_key", list.getWrite_key());
        params.put("field2",flusso);
        params.put("field3",Leaching);
        params.put("field4",numirra);
        params.put("field6",String.valueOf(1));
        params.put("field7",String.valueOf(field7));

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getBaseContext(),"IRRIGAZIONE "+tipo+" ATTTIVATA!",Toast.LENGTH_SHORT).show();
                check1=true;
                check2=false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),"ERRORE ATTIVAZIONE IRRIGAZIONE " +tipo+" RIPROVA TRA 15 SECONDI O CONTROLLA LA TUA CONNESSIONE!",Toast.LENGTH_LONG).show();
                //in caso di errore devo lasciare inalterata
                Switch.setChecked(false);
                check1=false;
                check2=true;
            }
        });
        queue.add(jsonRequest);
    }

    //invio i dati al server (in caso di attivazione automatica)
    private void irrigationOff() {

        String url = "https://api.thingspeak.com/update.json";
        List<savedValues> lista=db.SavedDao().getAll();
        Channel list=db.ChannelDao().findByName(lista.get(0).getId(),lista.get(0).getRead_key());
        if(list==null || list.getWrite_key()==null || list.getWrite_key().equals("")) {
            Toast.makeText(getBaseContext(),"CHIAVE SCRITTURA ERRATA",Toast.LENGTH_SHORT).show();
            return;
        }
        else Log.d("WRITE KEY",list.getWrite_key());

        Map<String, String> params = new HashMap();
        params.put("accept", "application/json");
        params.put("api_key", list.getWrite_key());
        params.put("field2",flussoText.getText().toString());
        params.put("field3",leachingText.getText().toString());
        params.put("field4", irradayText.getText().toString());
        params.put("field6",String.valueOf(0));
        params.put("field7",String.valueOf(field7));

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getBaseContext(),"IRRIGAZIONE AUTOMATICA DISATTIVATA!",Toast.LENGTH_SHORT).show();
                check1=false;
                check2=true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),"ERRORE DISATTIVAZIONE IRRIGAZIONE AUTOMATICA RIPROVA TRA 15 SECONDI O CONTROLLA LA TUA CONNESSIONE!",Toast.LENGTH_LONG).show();
                Switch.setChecked(true);
                check1=true;
                check2=false;
            }
        });

        queue.add(jsonRequest);
    }

    //metodi che mi servono per settare le impostazioni
    public void saveirrigationvalues(View v){
        Channel x=db.ChannelDao().findByName(channel.getLett_id(),channel.getLett_read_key());

        if(x!=null){
            db.ChannelDao().delete(x);
            try {
                x.setIrrigationDuration(Double.parseDouble(durationText.getText().toString()));
            }catch (Exception e){
            }
            try {
                flusso=Double.parseDouble(flussoText.getText().toString());
                x.setFlussoAcqua(flusso);
            }catch (Exception e){
                flusso=null;
            }
            try {
                leaching=Double.parseDouble(leachingText.getText().toString());
                x.setLeachingfactor(leaching);
            }catch (Exception e){
                leaching=null;
            }
            try {
                numirra=Integer.parseInt(irradayText.getText().toString());
                x.setNumirra(numirra);
            }catch (Exception e){
                numirra=-1;
            }
            db.ChannelDao().insert(x);

            String url = "https://api.thingspeak.com/update.json";
            List<savedValues> lista=db.SavedDao().getAll();
            Channel list=db.ChannelDao().findByName(lista.get(0).getId(),lista.get(0).getRead_key());

            if(list==null || list.getWrite_key()==null || list.getWrite_key().equals("")) {
                Toast.makeText(getBaseContext(),"CHIAVE SCRITTURA ERRATA",Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> params = new HashMap();
            params.put("accept", "application/json");
            params.put("api_key", list.getWrite_key());
            params.put("field2",String.valueOf(flusso));
            params.put("field3",String.valueOf(leaching));
            if(numirra==-1) params.put("field4",null);
            else  params.put("field4",String.valueOf(numirra));
            if(Switch.isChecked()) params.put("field6",String.valueOf(1));
            else params.put("field6",String.valueOf(0));
            params.put("field7",String.valueOf(field7));

            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(getApplicationContext(),"VALORI SALVATI CORRETTAMENTE",Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getBaseContext(),"ERRORE SALVATAGGIO VALORI RIPROVA TRA 15 SECONDI O CONTROLLA LA TUA CONNESSIONE!",Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            });

           queue.add(jsonRequest);
        }
        else Toast.makeText(getApplicationContext(),"IMPOSSIBILE TROVARE IL CHANNEL SPECIFICATO",Toast.LENGTH_SHORT).show();

    }

    public void refreshvalues(View v){
        //recupero i dati dal server
        donwload();
    }

    public static Intent getActivityintent(Context context){
        Intent intent=new Intent(context, IrrigationActivity.class);
        return intent;
    }

    public static void setChannle(Channel chan){
        channel=chan;
    }

    //scarico i dati dal server riguardante la configurazione dell'irrigazione
    private void donwload() {
        List<savedValues> lista=db.SavedDao().getAll();
        Channel list=db.ChannelDao().findByName(lista.get(0).getId(),lista.get(0).getRead_key());
        String url="https://api.thingspeak.com/channels/"+list.getScritt_id()+"/feeds.json?api_key="+list.getScritt_read_key()+"&results=150";

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //recupero l'array feeds
                            JSONArray jsonArray = response.getJSONArray("feeds");
                            ArrayList<String> save = new ArrayList<>();
                            ArrayList<String> saveTime = new ArrayList<>();

                            //creo variabile che conterrà al suo interno l'ultimo valore dell'irrigazione automatica
                            int field6=0;

                            //scandisco tutti i valori e memorizzo gli ultimi valori salvati
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject valori = jsonArray.getJSONObject(i);

                                try {
                                    if (!valori.getString("field2").equals("null")) {
                                        flussoText.setText(valori.getString("field2"));
                                    }
                                } catch (Exception e) {
                                }

                                try {
                                    if (!valori.getString("field3").equals("null")) {
                                        leachingText.setText(valori.getString("field3"));
                                    }
                                } catch (Exception e) {
                                }
                                try {
                                    if (!valori.getString("field4").equals("null")) {
                                        irradayText.setText(valori.getString("field4"));
                                    }
                                } catch (Exception e) {
                                }

                                try {
                                    if (!valori.getString("field6").equals("null")) {
                                        if (Double.parseDouble(valori.getString("field6")) == 1) {
                                            field6=1;
                                        } else {
                                            field6=0;
                                        }

                                    }
                                } catch (Exception e) {
                                }

                                try {
                                    if (!valori.getString("field7").equals("null") && !valori.getString("field7").equals("")) {
                                        save.add(valori.getString("field7"));
                                        saveTime.add(valori.getString("created_at"));
                                    }
                                } catch (Exception e) {
                                }
                            }

                            //controllo se era attiva l'irrigazione automatica, in tal caso l'attivo
                            if (field6 == 1) {
                                Switch.setChecked(true);
                                check1 = true;
                            } else {
                                Switch.setChecked(false);
                                check2 = true;
                            }

                            String primadata="", seconda="";
                            int i = 0;
                            boolean trovato = false;

                            while (i < save.size()) {
                                if (save.get(i).equals("1")) {
                                    if (!trovato) primadata = saveTime.get(i);
                                    //stampo la distanza dall'ultima irrigazione
                                    trovato = true;
                                }
                                if (save.get(i).equals("0")) {
                                    if (trovato) {
                                        seconda = saveTime.get(i);
                                        distanza(seconda);
                                        trovato = false;
                                    }
                                }
                                i++;
                            }

                            if (trovato) {
                                textDurata.setText("0 minuti");

                                Calendar date_now = Calendar.getInstance();
                                date_now.setTimeZone(TimeZone.getTimeZone("GMT"));
                                Calendar date_value = Calendar.getInstance();
                                //parsing della data
                                int giorno = Integer.valueOf(primadata.substring(8, 10));
                                int mese = Integer.valueOf(primadata.substring(5, 7));
                                int anno = Integer.valueOf(primadata.substring(0, 4));
                                int ore = Integer.valueOf(primadata.substring(11, 13));
                                int minuti = Integer.valueOf(primadata.substring(14, 16));
                                int secondi = Integer.valueOf(primadata.substring(17, 19));

                                //setto le impostazioni relative alla data
                                date_value.set(Calendar.YEAR, anno);
                                date_value.set(Calendar.MONTH, mese - 1);
                                date_value.set(Calendar.DAY_OF_MONTH, giorno);
                                date_value.set(Calendar.HOUR_OF_DAY, ore);
                                date_value.set(Calendar.MINUTE, minuti);
                                date_value.set(Calendar.SECOND, secondi);

                                //durata in secondi
                                long durata = (date_now.getTimeInMillis() / 1000 - date_value.getTimeInMillis() / 1000);
                                long giorni1 = (durata / 86400);
                                long temp = giorni1 * 86400;
                                long ore1 = (durata - temp) / 3600;
                                long minuti1 = ((durata - temp) - 3600 * ore1) / 60;
                                temp = (durata - temp) - 3600 * ore1;
                                long secondi1 = temp - (minuti1 * 60);

                                Durata.setText(ore1 + " ore " + minuti1 + " minuti " + secondi1 + " secondi ");
                            } else distanza2(primadata, seconda);

                            JSONObject valori = jsonArray.getJSONObject(jsonArray.length()-1);
                            try {
                                if (!valori.getString("field7").equals("null")) {
                                    if (Double.parseDouble(valori.getString("field7")) == 1) {
                                        image.setImageResource(R.drawable.irrigazioneattiva);
                                        field7 = 1;
                                    } else {
                                        image.setImageResource(R.drawable.irrigazione);
                                        field7 = 0;
                                    }
                                } else image.setImageResource(R.drawable.irrigazione);
                            } catch (Exception e) {
                                image.setImageResource(R.drawable.irrigazione);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(cont,"ERRORE RECUPERO DATI DAL SERVER",Toast.LENGTH_SHORT).show();
            }
        });
       queue.add(jsonObjectRequest);
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

        textDurata.setText( giorni1 + " giorni " + ore1 + " ore " + minuti1 + " minuti " + secondi1+ " secondi ");
    }

    private void distanza2(String data1,String data2) {
        Calendar date_value1 = Calendar.getInstance ();
        Calendar date_value2 = Calendar.getInstance ();

        //parsing della data
        int giorno=Integer.valueOf(data1.substring(8, 10));
        int mese=Integer.valueOf(data1.substring(5, 7));
        int anno=Integer.valueOf(data1.substring(0, 4));
        int ore=Integer.valueOf(data1.substring(11, 13));
        int minuti=Integer.valueOf(data1.substring(14, 16));
        int secondi=Integer.valueOf(data1.substring(17, 19));

        //setto le impostazioni relative alla data
        date_value1.set (Calendar.YEAR,anno);
        date_value1.set (Calendar.MONTH,mese-1);
        date_value1.set (Calendar.DAY_OF_MONTH,giorno);
        date_value1.set (Calendar.HOUR_OF_DAY,ore);
        date_value1.set (Calendar.MINUTE,minuti);
        date_value1.set (Calendar.SECOND, secondi);

        //parsing della data
        int gio=Integer.valueOf(data2.substring(8, 10));
        int me=Integer.valueOf(data2.substring(5, 7));
        int an=Integer.valueOf(data2.substring(0, 4));
        int or=Integer.valueOf(data2.substring(11, 13));
        int min=Integer.valueOf(data2.substring(14, 16));
        int sec=Integer.valueOf(data2.substring(17, 19));

        //setto le impostazioni relative alla data
        date_value2.set (Calendar.YEAR,an);
        date_value2.set (Calendar.MONTH,me-1);
        date_value2.set (Calendar.DAY_OF_MONTH,gio);
        date_value2.set (Calendar.HOUR_OF_DAY,or);
        date_value2.set (Calendar.MINUTE,min);
        date_value2.set (Calendar.SECOND, sec);

        //durata in secondi
        long durata= (date_value2.getTimeInMillis()/1000 - date_value1.getTimeInMillis()/1000);
        long giorni1=(durata/86400);
        long temp=giorni1*86400;
        long ore1=(durata-temp)/3600;
        long minuti1=((durata-temp)-3600*ore1)/60;
        temp=(durata-temp)-3600*ore1;
        long secondi1=temp-(minuti1*60);

        Durata.setText( ore1 + " ore " + minuti1 + " minuti " + secondi1+ " secondi ");
    }

    public static String getCurrentTimezoneOffset() {

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        return String.valueOf((offsetInMillis/(1000*3600))-1);
    }

}
