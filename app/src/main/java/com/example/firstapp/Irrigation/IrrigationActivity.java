package com.example.firstapp.Irrigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.example.firstapp.AppDatabase;
import com.example.firstapp.Channel.Channel;
import com.example.firstapp.R;

public class IrrigationActivity extends AppCompatActivity {
    private static AppDatabase db;
    private static EditText duration;
    private static EditText flusso;
    private static EditText leaching;
    private static EditText irraday;
    private static Channel channel;

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

        if(channel.getIrrigationDuration()!=null) duration.setText(String.valueOf(channel.getIrrigationDuration()));
        if(channel.getFlussoAcqua()!=null) flusso.setText(String.valueOf(channel.getFlussoAcqua()));
        if(channel.getLeachingfactor()!=null) leaching.setText(String.valueOf(channel.getLeachingfactor()));
        if(channel.getNumirra()!=null) irraday.setText(String.valueOf(channel.getNumirra()));
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
