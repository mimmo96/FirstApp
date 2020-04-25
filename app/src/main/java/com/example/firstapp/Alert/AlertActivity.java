package com.example.firstapp.Alert;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
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
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/*
 * Progetto: svilluppo App Android per Tirocinio interno
 *
 * Dipartimento di Informatica Università di Pisa
 *
 * Autore:Domenico Profumo matricola 533695
 * Si dichiara che il programma è in ogni sua parte, opera originale dell'autore
 *
 */


public class AlertActivity extends AppCompatActivity {
    private static NotificationManagerCompat notificationManager;
    private static Context cont;
    private EditText tempMin;
    private EditText tempMax;
    private EditText umidMin;
    private EditText umidMax;
    private EditText condMin;
    private EditText condMax;
    private EditText phMin;
    private EditText phMax;
    private EditText irraMin;
    private EditText irraMax;
    private EditText pesMin;
    private EditText pesMax;
    private EditText tempomax;
    private static TextView temp;
    private static TextView umid;
    private static TextView ph;
    private static TextView cond;
    private static TextView irra;
    private static TextView peso;
    private static EditText minutes;
    private static Intent serviceIntent;
    private static Channel channel;             //channel usato
    private static AppDatabase database;
    private static Switch aSwitch;
    private static String LastvaluesSTime;
    private static int minuti=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_settings);

        cont=getApplication();
        notificationManager=NotificationManagerCompat.from(this);
        tempMin=findViewById(R.id.Tempmin);
        tempMax=findViewById(R.id.tempmax);
        umidMin=findViewById(R.id.umidmin);
        umidMax=findViewById(R.id.umidmax);
        condMin=findViewById(R.id.condmin);
        condMax=findViewById(R.id.condmax);
        phMin=findViewById(R.id.phmin);
        phMax=findViewById(R.id.phmax);
        irraMin=findViewById(R.id.irramin);
        irraMax=findViewById(R.id.irramax);
        pesMin=findViewById(R.id.pesomin);
        pesMax=findViewById(R.id.pesomax);
        temp=findViewById(R.id.textViewtemp);
        umid=findViewById(R.id.textViewUmid);
        ph=findViewById(R.id.textViewPh);
        cond=findViewById(R.id.textViewcond);
        irra=findViewById(R.id.textViewirra);
        peso=findViewById(R.id.textViewPes);
        tempomax=findViewById(R.id.Edittempomax);
        minutes=findViewById(R.id.editTextMinuti);
        aSwitch=findViewById(R.id.switch2);


        database = Room.databaseBuilder(this, AppDatabase.class, "prodiction")
                //consente l'aggiunta di richieste nel thred principale
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                //build mi serve per costruire il tutto
                .build();

        serviceIntent = new Intent(this, ExampleService.class);

        //ripristino i valori relativi al channel precedentemente salvati
        if (channel.getTempMin()!= null ) tempMin.setText(String.format(channel.getTempMin().toString()));
        if (channel.getTempMax()!=null) tempMax.setText(String.format(channel.getTempMax().toString()));
        if (channel.getUmidMin()!=null) umidMin.setText(String.format(channel.getUmidMin().toString()));
        if (channel.getUmidMax()!=null) umidMax.setText(String.format(channel.getUmidMax().toString()));
        if (channel.getCondMin()!=null) condMin.setText(String.format(channel.getCondMin().toString()));
        if (channel.getCondMax()!=null) condMax.setText(String.format(channel.getCondMax().toString()));
        if (channel.getPhMin()!=null) phMin.setText(String.format(channel.getPhMin().toString()));
        if (channel.getPhMax()!=null) phMax.setText(String.format(channel.getPhMax().toString()));
        if (channel.getIrraMin()!=null) irraMin.setText(String.format(channel.getIrraMin().toString()));
        if (channel.getIrraMax()!=null) irraMax.setText(String.format(channel.getIrraMax().toString()));
        if (channel.getPesMin()!=null) pesMin.setText(String.format(channel.getPesMin().toString()));
        if (channel.getPesMax()!=null) pesMax.setText(String.format(channel.getPesMax().toString()));
        if (channel.getLastimevalues()!=0) minutes.setText(String.valueOf(channel.getLastimevalues()));
        if (channel.getTempomax()!=0) tempomax.setText(String.valueOf(channel.getTempomax()));

        //se le notifiche erano attive avvio il servizio notifiche
        if (channel.getNotification()){
            aSwitch.setChecked(true);
            startService();
        }
        else{
            aSwitch.setChecked(false);
        }

        //scarico la media dei valori e la rapresento a schermo
        downloadMedia();

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //appena l'irrigazione è attiva
                if (isChecked){
                    Log.d("AlertActivity","attivo notifiche");
                    startService();
                    //abilito le notifiche
                    Channel x=database.ChannelDao().findByName(channel.getId(),channel.getRead_key());
                    database.ChannelDao().delete(x);
                    x.setNotification(true);
                    database.ChannelDao().insert(x);
                    channel=x;
                }
                else{
                    Log.d("AlertActivity","fermo notifiche");

                    //disabilito le notifiche
                    Channel x=database.ChannelDao().findByName(channel.getId(),channel.getRead_key());
                    database.ChannelDao().delete(x);
                    x.setNotification(false);
                    database.ChannelDao().insert(x);
                    stopService();
                }
            }
        });

    }

    private void downloadMedia() {
        String url=null;
        if(minuti==0){
            url= "https://api.thingspeak.com/channels/" + channel.getId() + "/feeds.json?api_key="
                    + channel.getRead_key() + "&results=1";
        }
        else{
            int dist=0;
            if(channel.getLastimevalues()==0) dist=minuti;
            else dist=channel.getLastimevalues()+minuti;
            Log.d("Distanza:",String.valueOf(minuti+channel.getLastimevalues()));
            url= "https://api.thingspeak.com/channels/" + channel.getId() + "/feeds.json?api_key="
                    + channel.getRead_key() + "&minutes=" + dist + "&offset=2";
        }
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("MINUTI: ",String.valueOf(minuti));
                                //recupero l'array feeds
                                JSONArray jsonArray = response.getJSONArray("feeds");

                                //recupero i fields associati al channel
                                ArrayList<String> fields = new ArrayList<String>();
                                int dim = response.getJSONObject("channel").length();
                                Log.d("Thread background", "donwload eseguito");
                                //salvo tutti i field nell'array
                                try {
                                    for (int i = 0; i < dim; i++) {
                                        fields.add(String.valueOf(response.getJSONObject("channel").get("field" + (i + 1))));
                                    }
                                } catch (Exception e) {

                                }

                                Double somma = 0.0;
                                Double t = 0.0;
                                Double u = 0.0;
                                Double p = 0.0;
                                Double c = 0.0;
                                Double ir = 0.0;
                                Double pe = 0.0;
                                String cretime=null;

                                //scorro tutto l'array
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    somma++;

                                    //recupero il primo oggetto dell'array
                                    final JSONObject value = jsonArray.getJSONObject(i);
                                    try {
                                        String temperature = value.getString("field1");
                                        if (fields.get(0).equals("Temperature")) {
                                            t = t + Double.parseDouble(String.format(temperature));
                                        }
                                    }catch (Exception e){  }
                                    try{
                                        String umidity = value.getString("field2");
                                        if (fields.get(1).equals("Humidity")) {
                                            u = u + Double.parseDouble(String.format(umidity));
                                        }
                                    }catch (Exception e){  }
                                    try {
                                        String ph1 = value.getString("field3");
                                        if (fields.get(2).equals("pH_value")) {
                                            p = p + Double.parseDouble(String.format(ph1));
                                        }
                                    }catch (Exception e){}
                                    try {
                                        String conducibilita = value.getString("field4");
                                        if (fields.get(3).equals("electric_conductivity")) {
                                            c = c + Double.parseDouble(String.format(conducibilita));
                                        }
                                    }catch (Exception e){ }
                                    try {
                                        String irradianza = value.getString("field5");
                                        if (fields.get(4).equals("Irradiance")) {
                                            ir = ir + Double.parseDouble(String.format(irradianza));
                                        }
                                    }catch (Exception e){ }
                                    try {
                                        String peso1 = value.getString("field6");
                                        if (fields.get(5).equals("P0")) {
                                            pe = pe + Double.parseDouble(String.format(peso1));
                                        }
                                    }catch (Exception e){ }
                                    try {
                                        cretime = value.getString("created_at");
                                        minuti=(int)distanza(cretime)/60;
                                    }catch (Exception e){ }
                                }

                                //in cretime avrò il valore dell'ultimo dato
                                minuti=(int) distanza(cretime);

                                //calcolo la media di tutti i valori e la confronto con i miei valori,se la supera invio la notifica
                                t = Math.round(t / somma * 100.0) / 100.0;
                                u = Math.round(u / somma * 100.0) / 100.0;
                                p = Math.round(p / somma * 100.0) / 100.0;
                                c = Math.round(c / somma * 100.0) / 100.0;
                                ir = Math.round(ir / somma * 100.0) / 100.0;
                                pe = Math.round(pe / somma * 100.0) / 100.0;
                                Log.d("SOMMA VALORI: ", somma.toString());
                                Log.d("MEDIA VALORI: ", "t:" + t + " u:" + u + " p:" + p + " c:" + c + " ir:" + ir + " pe:" + pe);
                                if (channel.getNotification()) Log.d("NOTIFICHE", "ATTIVE");
                                else Log.d("NOTIFICHE", "NON ATTIVE");
                                //invio le notifiche se i valori non rispettano le soglie imposte
                                    try {
                                        if (temp != null){
                                            //se non ho scaricato valori
                                            if(somma==0)  temp.setText("- -");
                                            else temp.setText(String.valueOf(t));
                                        }
                                    } catch (Exception e) {
                                       temp.setText("- -");
                                    }
                                    try {
                                        if (umid != null){
                                            //se non ho scaricato valori
                                            if(somma==0)  umid.setText("- -");
                                            else umid.setText(String.valueOf(u));
                                        }
                                    } catch (Exception e) {
                                        umid.setText("- -");
                                    }
                                    try {
                                        if (ph != null){
                                            //se non ho scaricato valori
                                            if(somma==0)  ph.setText("- -");
                                            else  ph.setText(String.valueOf(p));
                                        }
                                    } catch (Exception e) {
                                       ph.setText("- -");
                                    }
                                    try {
                                        if (cond != null){
                                            //se non ho scaricato valori
                                            if(somma==0)  cond.setText("- -");
                                            else cond.setText(String.valueOf(c));
                                        }
                                    } catch (Exception e) {
                                       cond.setText("- -");
                                    }
                                    try {
                                        if (irra != null){
                                            //se non ho scaricato valori
                                            if(somma==0)  irra.setText("- -");
                                            else irra.setText(String.valueOf(ir));
                                        }
                                    } catch (Exception e) {
                                      irra.setText("- -");
                                    }
                                    try {
                                        if (peso != null){
                                            //se non ho scaricato valori
                                            if(somma==0)  peso.setText("- -");
                                            else peso.setText(String.valueOf(pe).concat(" g"));
                                        }
                                    } catch (Exception e) {
                                       peso.setText("- -");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Log.d("AlertActivity", "donwload eseguito correttamente");
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("AlertActivity", "errore donwload");
                }
            });
            Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }

    public static Intent getActivityintent(Context context){
        Intent intent=new Intent(context, AlertActivity.class);
        return intent;
    }

    //se premo il pulsante save
    public void saveButton(View v) {
        Channel x = database.ChannelDao().findByName(channel.getId(), channel.getRead_key());

        if (x != null) {
            database.ChannelDao().delete(x);
            try {
                x.setTempMin(Double.valueOf(tempMin.getText().toString()));
            } catch (NumberFormatException e) {
            }
            try {
                x.setTempMax(Double.valueOf(tempMax.getText().toString()));
            } catch (NumberFormatException e) {
            }
            try {
                x.setUmidMin(Double.valueOf(umidMin.getText().toString()));
            } catch (NumberFormatException e) {
            }
            try {
                x.setUmidMax(Double.valueOf(umidMax.getText().toString()));
            } catch (NumberFormatException e) {
            }
            try {
                x.setCondMin(Double.valueOf(condMin.getText().toString()));
            } catch (NumberFormatException e) {
            }
            try {
                x.setCondMax(Double.valueOf(condMax.getText().toString()));
            } catch (NumberFormatException e) {
            }
            try {
                x.setPhMin(Double.valueOf(phMin.getText().toString()));
            } catch (NumberFormatException e) {
            }
            try {
                x.setPhMax(Double.valueOf(phMax.getText().toString()));
            } catch (NumberFormatException e) {
            }
            try {
                x.setIrraMin(Double.valueOf(irraMin.getText().toString()));
            } catch (NumberFormatException e) {
            }
            try {
                x.setIrraMax(Double.valueOf(irraMax.getText().toString()));
            } catch (NumberFormatException e) {
            }
            try {
                x.setPesMin(Double.valueOf(pesMin.getText().toString()));
            } catch (NumberFormatException e) {
            }
            try {
                x.setPesMax(Double.valueOf(pesMax.getText().toString()));
            } catch (NumberFormatException e) {
            }
            try {
                x.setLastimevalues(Integer.valueOf(minutes.getText().toString()));
            } catch (NumberFormatException e) {
            }
            try {
                x.setTempomax(Integer.valueOf(tempomax.getText().toString()));
            } catch (NumberFormatException e) {
            }
            database.ChannelDao().insert(x);
            channel=x;
            Toast.makeText(cont,"VALORI SALVATI CORRETTAMENTE!",Toast.LENGTH_SHORT).show();
        }

        //riscarico i dati e faccio un reset di tutto
        downloadMedia();
    }

    //se premo il pulsante reset
    public void resetButton (View v) {
            Channel x=database.ChannelDao().findByName(channel.getId(),channel.getRead_key());
            database.ChannelDao().delete(x);
            x.setTempMin(null);
            x.setTempMax(null);
            x.setUmidMin(null);
            x.setUmidMax(null);
            x.setCondMin(null);
            x.setCondMax(null);
            x.setPhMin(null);
            x.setPhMax(null);
            x.setIrraMin(null);
            x.setIrraMax(null);
            x.setPesMin(null);
            x.setPesMax(null);
            x.setLastimevalues(0);
            x.setTempomax(0);
            database.ChannelDao().insert(x);

            //resetto i valori anche nei text
            tempMin.setText(" ");
            tempMax.setText(" ");
            umidMin.setText(" ");
            umidMax.setText(" ");
            condMin.setText(" ");
            condMax.setText(" ");
            phMin.setText(" ");
            phMax.setText(" ");
            irraMin.setText(" ");
            irraMax.setText(" ");
            pesMin.setText(" ");
            pesMax.setText(" ");
            minutes.setText(" ");
            tempomax.setText(" ");

            if (channel.getLastimevalues()!=0) minutes.setText(String.valueOf(channel.getLastimevalues()));
            Toast.makeText(cont,"VALORI RESETTATI CORRETTAMENTE",Toast.LENGTH_SHORT).show();
    }

    //funzione eseguita quando attivo le notifiche
    public void startService() {
            //se era stata già avviata fermo la precedente
            if (channel.getTimer()!=null ) {
                Log.d("TERMINO","sto terminando");
                stopService();
            }

            //converto i minuti in Stringa;
            String min=minutes.getText().toString();

            //setto i parametri da me impostati al service
            ExampleService.setvalue(temp, umid, ph, cond, irra, peso, channel, database,min);
            ContextCompat.startForegroundService(this, serviceIntent);
    }

    public static void stopService() {
        ExampleService.stoptimer();
    }

    public static void setChannel(Channel chan){
        channel=chan;
    }

    public static Context getContext(){
        return cont;
    }

    private static long distanza(String data) {
        if(data==null) return 0;
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

        //durata in secondi dall'ultimo aggiornamento
        long durata= (date_now.getTimeInMillis()/1000 - date_value.getTimeInMillis()/1000);

        //restituisco la durata in minuti approssimata ad un minuto in piu per sicurezza
        return  (durata/60)+1;
    }

    public static void setminutes(String minutes){
        minuti=(int)distanza(minutes);
    }

}
