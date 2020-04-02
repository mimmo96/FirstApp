package com.example.firstapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firstapp.Channel.Channel;

public class settingSingleValues extends AppCompatActivity {

    private static String titolo;
    private static String corrente;
    private static String id;
    private static String key;
    private static AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_single_values);

        TextView valoreCorrente=findViewById(R.id.SingleCurr);
        TextView title=findViewById(R.id.textViewTitle);
        TextView min=findViewById(R.id.SingleMin);
        TextView max=findViewById(R.id.SingleMax);
        title.setText(titolo);
        valoreCorrente.setText(corrente);

        Channel x=db.ChannelDao().findByName(id,key);
        if(titolo.equals("Temperature")){
            if(x.getTempMin()!=null) min.setText(x.getTempMin().toString());
            if(x.getTempMax()!=null) max.setText(x.getTempMax().toString());
        }
        if(titolo.equals("Ph")){
            if(x.getPhMin()!=null) min.setText(x.getPhMin().toString());
            if(x.getPhMax()!=null) max.setText(x.getPhMax().toString());
        }
        if(titolo.equals("Irradianza")){
            if(x.getIrraMin()!=null) min.setText(x.getIrraMin().toString());
            if(x.getIrraMax()!=null) max.setText(x.getIrraMax().toString());
        }
        if(titolo.equals("Conducibilità elettrica")){
            if(x.getCondMin()!=null) min.setText(x.getCondMin().toString());
            if(x.getCondMax()!=null) max.setText(x.getCondMax().toString());
        }
        if(titolo.equals("Peso")){
            if(x.getPesMin()!=null) min.setText(x.getPesMin().toString());
            if(x.getPesMax()!=null) max.setText(x.getPesMax().toString());
        }
        if(titolo.equals("Umidità")){
            if(x.getUmidMin()!=null) min.setText(x.getUmidMin().toString());
            if(x.getUmidMax()!=null) max.setText(x.getUmidMax().toString());
        }

    }

    public static Intent getActivityintent(Context context){
        Intent intent=new Intent(context, settingSingleValues.class);
        return intent;
    }

    public static void settingsValues(String title,String currValues,AppDatabase database,String id1,String key1){
        titolo=title;
        corrente=currValues;
        db=database;
        id=id1;
        key=key1;

    }
}
