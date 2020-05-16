package com.example.firstapp.Irrigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.firstapp.AppDatabase;
import com.example.firstapp.Channel.Channel;
import com.example.firstapp.Channel.savedValues;
import com.example.firstapp.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IrrigationActivity extends AppCompatActivity {
    private static AppDatabase db;
    private static EditText durationText;
    private static EditText flussoText;
    private static EditText leachingText;
    private static EditText irradayText;
    private static TextView textTime;
    private static Channel channel;
    private static Switch Switch;
    private static Button irra;
    private static Context cont;
    private Double leaching=0.35;
    private Double flusso=160.0;
    private int numirra=1;
    private int check=0;

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

        durationText=findViewById(R.id.editTextDuration);
        flussoText=findViewById(R.id.editTextFlusso);
        leachingText=findViewById(R.id.editTextLeaching);
        irradayText=findViewById(R.id.editTextIrraDay);
        Switch= findViewById(R.id.switch1);
        irra=findViewById(R.id.buttonIrra);
        textTime=findViewById(R.id.textViewTime);

        cont=getApplicationContext();

        //recupero i valori dal database
        setInitialValues();
        //recupero i dati dal server
        donwload();

        //azione quando clicco sull'irrigazione automatica
        Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //appena l'irrigazione è attiva
                if (isChecked){
                    if(check!=0) {
                        //mando i dati al server
                        irrigationOn(flussoText.getText().toString(), leachingText.getText().toString(), irradayText.getText().toString(), "AUTOMATICA");
                        textTime.setText("IRRIGAZIONE ATTIVATA");
                    }
                }
                else{
                    //comunico al server di interrompere l'irrigazione
                    irrigationOff();
                    textTime.setText("NESSUNA IRRIGAZIONE PRESENTE");
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

    //recupero dal database i dati precedentemente configurati
    private void setInitialValues() {
        //minuti che mi serviranno per l'irrigazione automatica
        if(channel.getIrrigationDuration()!=null) durationText.setText(String.valueOf(channel.getIrrigationDuration()));

        if(channel.getFlussoAcqua()!=null){
            flusso=channel.getFlussoAcqua();
            flussoText.setText(String.valueOf(flusso));
        }
        if(channel.getLeachingfactor()!=null){
            leaching=channel.getLeachingfactor();
            leachingText.setText(String.valueOf(leaching));
        }

        //numero di irrigazioni al giorno
        if(channel.getNumirra()!=0){
            numirra=channel.getNumirra();
            irradayText.setText(String.valueOf(numirra));
        }
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

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getBaseContext(),"IRRIGAZIONE"+tipo+" ATTTIVATA!",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),"ERRORE IRRIGAZIONE " +tipo+"!",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
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

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getBaseContext(),"IRRIGAZIONE"+tipo+" ATTTIVATA!",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),"ERRORE ATTIVAZIONE IRRIGAZIONE " +tipo+"!",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    //invio i dati al server (in caso di attivazione automatica)
    private void irrigationOff() {

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
        params.put("field2",flussoText.getText().toString());
        params.put("field3",leachingText.getText().toString());
        params.put("field4", irradayText.getText().toString());
        params.put("field6",String.valueOf(0));

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getBaseContext(),"IRRIGAZIONE AUTOMATICA DIATTTIVATA!",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),"ERRORE DISATTIVAZIONE IRRIGAZIONE!",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    //metodi che mi servono per settare le impostazioni
    public void saveirrigationvalues(View v){
        Channel x=db.ChannelDao().findByName(channel.getLett_id(),channel.getLett_read_key());

        if(x!=null){
            db.ChannelDao().delete(x);
            try {
                x.setIrrigationDuration(Double.parseDouble(durationText.getText().toString()));
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                flusso=Double.parseDouble(flussoText.getText().toString());
                x.setFlussoAcqua(flusso);
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                leaching=Double.parseDouble(leachingText.getText().toString());
                x.setLeachingfactor(leaching);
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                numirra=Integer.parseInt(irradayText.getText().toString());
                x.setNumirra(numirra);
            }catch (Exception e){
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(),"VALORI SALVATI CORRETTAMENTE",Toast.LENGTH_SHORT).show();
            db.ChannelDao().insert(x);
        }
        else Toast.makeText(getApplicationContext(),"IMPOSSIBILE TROVARE IL CHANNEL SPECIFICATO",Toast.LENGTH_SHORT).show();

    }

    public void resetirrigationvalues(View v){
        Channel x=db.ChannelDao().findByName(channel.getLett_id(),channel.getLett_read_key());

        if(x!=null){
            db.ChannelDao().delete(x);
            x.setNumirra(0);
            x.setLeachingfactor(null);
            x.setFlussoAcqua(null);
            x.setIrrigationDuration(null);
            Switch.setChecked(false);
            durationText.setText("");
            flussoText.setText("");
            leachingText.setText("");
            irradayText.setText("");
            db.ChannelDao().insert(x);
        }
        else Toast.makeText(getApplicationContext(),"IMPOSSIBILE TROVARE IL CHANNEL SPECIFICATO",Toast.LENGTH_SHORT).show();
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
        String url="https://api.thingspeak.com/channels/"+list.getScritt_id()+"/feeds.json?api_key="+list.getScritt_read_key()+"&results=1";

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //recupero l'array feeds
                            JSONArray jsonArray = response.getJSONArray("feeds");

                                JSONObject valori = jsonArray.getJSONObject(0);
                            try {
                                if (!valori.getString("field2").equals("null")) {
                                    flussoText.setText(valori.getString("field2"));
                                }
                            }catch (Exception e){ }

                            try {
                                if (!valori.getString("field3").equals("null")) {
                                    leachingText.setText(valori.getString("field3"));
                                }
                            } catch (Exception e) { }
                            try {
                                if (!valori.getString("field4").equals("null")) {
                                    irradayText.setText(valori.getString("field4"));
                                }
                            } catch (Exception e) { }

                            try {
                                if (!valori.getString("field6").equals("null")) {
                                    if(Double.parseDouble(valori.getString("field6"))==1) Switch.setChecked(true);
                                    else Switch.setChecked(false);
                                    check=1;
                                }
                            } catch (Exception e) { }

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
        Volley.newRequestQueue(cont).add(jsonObjectRequest);
    }

}
