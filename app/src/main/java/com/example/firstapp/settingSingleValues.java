package com.example.firstapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firstapp.Alert.AlertActivity;
import com.example.firstapp.Alert.ExampleService;
import com.example.firstapp.Channel.Channel;

import java.util.List;

public class settingSingleValues extends AppCompatActivity {

    private static String titolo;
    private static String corrente;
    private static String id;
    private static String key;
    private static AppDatabase db;
    private static TextView min;
    private static TextView max;
    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_single_values);

        TextView valoreCorrente=findViewById(R.id.SingleCurr);
        TextView title=findViewById(R.id.textViewTitle);
        min=findViewById(R.id.SingleMin);
        max=findViewById(R.id.SingleMax);
        title.setText(titolo);
        valoreCorrente.setText(corrente);
        context=getApplicationContext();

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

    public static void ButtonReset(View view){
        Channel x=db.ChannelDao().findByName(id,key);
        db.ChannelDao().delete(x);

        if(titolo.equals("Temperature")){
           x.setTempMin(null);
           x.setTempMax(null);
           min.setText("- -");
           max.setText("- -");
        }
        if(titolo.equals("Ph")){
            x.setPhMin(null);
            x.setPhMax(null);
            min.setText("- -");
            max.setText("- -");
        }
        if(titolo.equals("Irradianza")){
            x.setIrraMin(null);
            x.setIrraMax(null);
            min.setText("- -");
            max.setText("- -");
        }
        if(titolo.equals("Conducibilità elettrica")){
           x.setCondMin(null);
           x.setCondMax(null);
            min.setText("- -");
            max.setText("- -");
        }
        if(titolo.equals("Peso")){
            x.setPesMin(null);
            x.setPesMax(null);
            min.setText("- -");
            max.setText("- -");
        }
        if(titolo.equals("Umidità")){
            x.setUmidMin(null);
            x.setUmidMax(null);
            min.setText("- -");
            max.setText("- -");
        }
        db.ChannelDao().insert(x);

    }

    public static void ButtonConferma(View view){
        Channel x=db.ChannelDao().findByName(id,key);
        db.ChannelDao().delete(x);

        if(titolo.equals("Temperature")){
            try {
                Double Minimo = Double.valueOf(min.getText().toString());
                x.setTempMin(Minimo);
            } catch (NumberFormatException e) {

            }
            try {
                Double Massimo = Double.valueOf(max.getText().toString());
                x.setTempMax(Massimo);
            } catch (NumberFormatException e) {

            }
        }
        if(titolo.equals("Ph")){
            try {
                Double Minimo = Double.valueOf(min.getText().toString());
                x.setPhMin(Minimo);
            } catch (NumberFormatException e) {

            }
            try {
                Double Massimo = Double.valueOf(max.getText().toString());
                x.setPhMax(Massimo);
            } catch (NumberFormatException e) {

            }
        }
        if(titolo.equals("Irradianza")){
            try {
                Double Minimo = Double.valueOf(min.getText().toString());
                x.setIrraMin(Minimo);
            } catch (NumberFormatException e) {

            }
            try {
                Double Massimo = Double.valueOf(max.getText().toString());
                x.setIrraMax(Massimo);
            } catch (NumberFormatException e) {

            }
        }
        if(titolo.equals("Conducibilità elettrica")){
            try {
                Double Minimo = Double.valueOf(min.getText().toString());
                x.setCondMin(Minimo);
            } catch (NumberFormatException e) {

            }
            try {
                Double Massimo = Double.valueOf(max.getText().toString());
                x.setCondMax(Massimo);
            } catch (NumberFormatException e) {

            }
        }
        if(titolo.equals("Peso")){
            try {
                Double Minimo = Double.valueOf(min.getText().toString());
                x.setPesMin(Minimo);
            } catch (NumberFormatException e) {

            }
            try {
                Double Massimo = Double.valueOf(max.getText().toString());
                x.setPesMax(Massimo);
            } catch (NumberFormatException e) {

            }
        }
        if(titolo.equals("Umidità")){
            try {
                Double Minimo = Double.valueOf(min.getText().toString());
                x.setUmidMin(Minimo);
            } catch (NumberFormatException e) {

            }
            try {
                Double Massimo = Double.valueOf(max.getText().toString());
                x.setUmidMax(Massimo);
            } catch (NumberFormatException e) {
            }
        }

        db.ChannelDao().insert(x);

    }
}
