package com.example.firstapp.Irrigation;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import com.example.firstapp.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Double pesoPrec=0.0;
    private Double pesoAtt=0.0;
    private Double leaching=0.35;
    private Double flusso=160.0;
    private int numirra=1;

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

        setInitialValues();

        Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //appena l'irrigazione è attiva
                if (isChecked){
                    setInitialValues();
                    startAllarm();
                    Toast.makeText(getBaseContext(),"IRRIGAZIONE AUTOMATICA ATTTIVATA!",Toast.LENGTH_SHORT).show();
                }
                else{
                    stopAllarm();
                    textTime.setText("NESSUNA IRRIGAZIONE PRESENTE");
                    Channel x=db.ChannelDao().findByName(channel.getId(),channel.getRead_key());
                    db.ChannelDao().delete(x);
                    x.setTimeAlarm("NESSUNA IRRIGAZIONE PRESENTE");
                    x.setAlarmManager( null);
                    db.ChannelDao().insert(x);
                    Toast.makeText(getBaseContext(),"IRRIGAZIONE AUTOMATICA DISATTTIVATA!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        irra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendvalue(durationText.getText().toString(),"MANUALE");
            }
        });
    }

    private void setInitialValues() {
        if(channel.getAlarmManager()!=null){
            alarmManager=channel.getAlarmManager();
            Switch.setChecked(true);
        }

        if(channel.getTimeAlarm()!=null){
            textTime.setText(channel.getTimeAlarm());
        }

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

        MyNewIntentService.settingvalue(leaching,flusso);
    }

    public void startAllarm() {
        Intent notifyIntent = new Intent(this,MyReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(cont, 100, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) cont.getSystemService(Context.ALARM_SERVICE);

        //secupero il channel associato
        Channel x=db.ChannelDao().findByName(channel.getId(),channel.getRead_key());

        //resetto le date precedentemente impostate
        textTime.setText("IRRIGAZIONE AUTOMATICA:\n");

        //misuro la distanza tra le ore
        double distance=(24.0/numirra);

        for(int i=0;i<numirra;i++) {
            int ora=(int) (distance*i);
            int minuto= (int)(((distance*i)-ora)*60);
            /*
             * da aggiungere solo se devo partire dalle 9
             *
             * ora=ora+9;
             * if(ora>24) ora=ora-24;
             *
             */
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            //controllo che l'ora di partenza è superiore alla mia altrimenti aumento il giorno
            if(ora<=calendar.getTime().getHours()){
                Log.d("GIORNOPROVA",String.valueOf(calendar.getTime().getDate()));
                calendar.set(Calendar.DAY_OF_MONTH,calendar.getTime().getDate()+1);
            }
            calendar.set(Calendar.HOUR_OF_DAY, ora);
            calendar.set(Calendar.MINUTE, minuto);
            calendar.set(Calendar.SECOND, 00);

            SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
            Date date = calendar.getTime();
            String time = format1.format(date);

            String text=textTime.getText().toString();
            text=text.concat(time +"\n");
            textTime.setText(text);
            //avvia l'allarme esattamente a quell'ora ogni giorno
            Log.d("DATA SETTATA:",calendar.getTime().toString());

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
        }

        //nel caso voglia attivarla con una data mia settata
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(System.currentTimeMillis());
        //alarmManager.setExact(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(),pendingIntent);

        db.ChannelDao().delete(x);
        x.setAlarmManager(alarmManager);
        x.setTimeAlarm(textTime.getText().toString());
        db.ChannelDao().insert(x);

        Log.d("ALARM","ATTIVATO");
    }

    public void stopAllarm(){
        alarmManager.cancel(pendingIntent);
        Log.d("ALARM","DISATTIVATO");
    }

    //invio i dati al server
    private void sendvalue(String value, final String tipo) {

        String url = "https://api.thingspeak.com/update.json";

        Map<String, String> params = new HashMap();
        params.put("accept", "application/json");
        params.put("api_key", "PAG5TFQPULRTH8RY");
        params.put("field1",value);

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


    //metodi che mi servono per settare le impostazioni

    public void saveirrigationvalues(View v){
        Channel x=db.ChannelDao().findByName(channel.getId(),channel.getRead_key());
        boolean ok=true;

        if(x!=null){
            db.ChannelDao().delete(x);
            try {
                x.setIrrigationDuration(Double.parseDouble(durationText.getText().toString()));
            }catch (Exception e){
                e.printStackTrace();
                ok=false;
            }
            try {
                flusso=Double.parseDouble(flussoText.getText().toString());
                x.setFlussoAcqua(flusso);
            }catch (Exception e){
                e.printStackTrace();
                ok=false;
            }
            try {
                leaching=Double.parseDouble(leachingText.getText().toString());
                x.setLeachingfactor(leaching);
            }catch (Exception e){
                e.printStackTrace();
                ok=false;
            }
            try {
                numirra=Integer.parseInt(irradayText.getText().toString());
                x.setNumirra(numirra);
            }catch (Exception e){
                e.printStackTrace();
                ok=false;
            }
            if(ok) Toast.makeText(getApplicationContext(),"VALORI SALVATI CORRETTAMENTE",Toast.LENGTH_SHORT).show();
            else Toast.makeText(getApplicationContext(),"ERRORE NEL SALVATAGGIO DI ALCUNI VALORI",Toast.LENGTH_SHORT).show();
            db.ChannelDao().insert(x);
            MyNewIntentService.settingvalue(leaching,flusso);
        }
        else Toast.makeText(getApplicationContext(),"IMPOSSIBILE TROVARE IL CHANNEL SPECIFICATO",Toast.LENGTH_SHORT).show();

    }

    public void resetirrigationvalues(View v){
        Channel x=db.ChannelDao().findByName(channel.getId(),channel.getRead_key());

        if(x!=null){
            db.ChannelDao().delete(x);
            x.setNumirra(0);
            x.setLeachingfactor(null);
            x.setFlussoAcqua(null);
            x.setIrrigationDuration(null);
            x.setAlarmManager(null);
            x.setTimeAlarm("NESSUNA IRRIGAZIONE PRESENTE");
            x.setAlarmManager( null);
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


}
