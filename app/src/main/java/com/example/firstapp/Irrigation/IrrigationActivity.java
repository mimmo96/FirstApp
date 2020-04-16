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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.firstapp.AppDatabase;
import com.example.firstapp.Channel.Channel;
import com.example.firstapp.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class IrrigationActivity extends AppCompatActivity {
    private static AppDatabase db;
    private static EditText duration;
    private static EditText flusso;
    private static EditText leaching;
    private static EditText irraday;
    private static Channel channel;
    private static Switch Switch;
    private static Button irra;
    private static Context cont;

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

        duration=findViewById(R.id.editTextDuration);
        flusso=findViewById(R.id.editTextFlusso);
        leaching=findViewById(R.id.editTextLeaching);
        irraday=findViewById(R.id.editTextIrraDay);
        Switch= findViewById(R.id.switch1);
        irra=findViewById(R.id.buttonIrra);

        cont=getApplicationContext();

        if(channel.getIrrigationDuration()!=null) duration.setText(String.valueOf(channel.getIrrigationDuration()));
        if(channel.getFlussoAcqua()!=null) flusso.setText(String.valueOf(channel.getFlussoAcqua()));
        if(channel.getLeachingfactor()!=null) leaching.setText(String.valueOf(channel.getLeachingfactor()));
        if(channel.getNumirra()!=null) irraday.setText(String.valueOf(channel.getNumirra()));

        Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //appena l'irrigazione è attiva
                if (isChecked){
                    Double pesoPrec=1710.0;
                    Double pesoAtt=1121.5;
                    Double leaching=0.35;
                    Double flusso=160.0;
                    double min=calcolominuti(pesoPrec,pesoAtt,leaching,flusso);
                    sendvalue(String.valueOf(min));
                    Toast.makeText(getBaseContext(),"IRRIGAZIONE AUTOMATICA ATTTIVATA!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getBaseContext(),"IRRIGAZIONE AUTOMATICA DISATTTIVATA!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        irra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getBaseContext(),"IRRIGAZIONE MANUALE ATTTIVATA!",Toast.LENGTH_SHORT).show();
                sendvalue(duration.getText().toString());
            }
        });
    }

    //using okhttp
    private void sendvalue(String value) {

        String url = "https://api.thingspeak.com/update.json";

        Map<String, String> params = new HashMap();
        params.put("accept", "application/json");
        params.put("api_key", "PAG5TFQPULRTH8RY");
        params.put("field1",value);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getBaseContext(),"IRRIGAZIONE MANUALE ATTTIVATA!",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),"ERRORE RICHIESTA IRRIGAZIONE!",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);

    }

    private static double calcolominuti(Double pesoPrec,Double pesoAtt,Double leaching,Double flusso) {
        //durata = -Delta P0*(1+leaching_factor)/flusso
        Double deltaP0=pesoPrec-pesoAtt;
        Double durata=deltaP0*(1+leaching)/flusso;

        return durata;
    }

    public void saveirrigationvalues(View v){
        Channel x=db.ChannelDao().findByName(channel.getId(),channel.getRead_key());
        boolean ok=true;

        if(x!=null){
            db.ChannelDao().delete(x);
            try {
                x.setIrrigationDuration(Double.parseDouble(duration.getText().toString()));
            }catch (Exception e){
                e.printStackTrace();
                ok=false;
            }
            try {
                x.setFlussoAcqua(Double.parseDouble(flusso.getText().toString()));
            }catch (Exception e){
                e.printStackTrace();
                ok=false;
            }
            try {
                x.setLeachingfactor(Double.parseDouble(leaching.getText().toString()));
            }catch (Exception e){
                e.printStackTrace();
                ok=false;
            }
            try {
                x.setNumirra(Double.parseDouble(irraday.getText().toString()));
            }catch (Exception e){
                e.printStackTrace();
                ok=false;
            }
            if(ok) Toast.makeText(getApplicationContext(),"VALORI SALVATI CORRETTAMENTE",Toast.LENGTH_SHORT).show();
            else Toast.makeText(getApplicationContext(),"ERRORE NEL SALVATAGGIO DI ALCUNI VALORI",Toast.LENGTH_SHORT).show();
            db.ChannelDao().insert(x);
        }
        else Toast.makeText(getApplicationContext(),"IMPOSSIBILE TROVARE IL CHANNEL SPECIFICATO",Toast.LENGTH_SHORT).show();

    }

    public void resetirrigationvalues(View v){
        Channel x=db.ChannelDao().findByName(channel.getId(),channel.getRead_key());

        if(x!=null){
            db.ChannelDao().delete(x);
            x.setNumirra(null);
            x.setLeachingfactor(null);
            x.setFlussoAcqua(null);
            x.setIrrigationDuration(null);
            duration.setText("");
            flusso.setText("");
            leaching.setText("");
            irraday.setText("");
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


}
