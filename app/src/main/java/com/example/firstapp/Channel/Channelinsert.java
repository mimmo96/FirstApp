package com.example.firstapp.Channel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firstapp.MainActivity;
import com.example.firstapp.R;

public class Channelinsert  extends AppCompatActivity {

    private static Button annulla;
    private static Button salva;
    private static EditText IDlett;
    private static EditText IDscritt;
    private static EditText read_lett;
    private static EditText read_scritt;
    private static EditText write_scritt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_insert);

        //associo le componenti
        annulla=findViewById(R.id.buttonAnnul);
        salva=findViewById(R.id.buttonSal);
        IDlett=findViewById(R.id.channelID_Lett);
        IDscritt=findViewById(R.id.channelID_Scritt);
        read_lett=findViewById(R.id.channelRead_Lett);
        read_scritt=findViewById(R.id.channelRead_Scritt);
        write_scritt=findViewById(R.id.channelWrite_Scritt);
    }

    //azione eseguita quando premo il pulsante annulla
    public void annulla(View v) {
        finish();
    }

    //azione eseguita quando premo il pulsante salva
    public void salva(View v) {
        Log.d("ChannelInsert","\n\n IDlett:" + IDlett.getText().toString()+ "\tIDscritt:"+ IDscritt.getText().toString() + "\n read_lett:" + read_lett.getText().toString()
                + "\tread_scritt:" +read_scritt.getText().toString()+ "\n\t\t\t write_scritt:" +write_scritt.getText().toString() );

        if(IDlett.getText().toString().equals("") || read_lett.getText().toString().equals("")) Toast.makeText(getApplicationContext(),"INSERISCI ID E CHIAVE DI LETTURA!",Toast.LENGTH_SHORT).show();
        else{
           // if(IDlett.getText().toString().equals(""))
           //     if(IDlett.getText().toString().equals(""))

        }

    }

    public static Intent getActivityintent(Context context){
        Intent intent=new Intent(context,Channelinsert.class);
        return intent;
    }
}


  /*
                final EditText taskEditText =new EditText(BasicContext);
                //specifico che devo prendere solo numeri interi
                taskEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                taskEditText.setRawInputType(Configuration.KEYBOARD_12KEY);

                final EditText taskEditText2 =new EditText(BasicContext);
                final EditText taskEditText3 =new EditText(BasicContext);
                AlertDialog.Builder dialog=new AlertDialog.Builder(BasicContext)
                        .setTitle("NUOVO CANALE")
                        .setMessage("INSERISCI ID")
                        .setView(taskEditText)
                        .setPositiveButton("AVANTI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlertDialog.Builder dialog2=new AlertDialog.Builder(BasicContext)
                                        .setTitle("NUOVO CANALE")
                                        .setMessage("INSERISCI CHIAVE LETTURA")
                                        .setView(taskEditText2)
                                        .setPositiveButton("AVANTI", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                 AlertDialog.Builder dialog3=new AlertDialog.Builder(BasicContext)
                                                .setTitle("NUOVO CANALE")
                                                .setMessage("INSERISCI CHIAVE SCRITTURA")
                                                .setView(taskEditText3)
                                                .setPositiveButton("CONFERMA", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                    //controllo se i dati inseriti corrispondono ad un channel esistente
                                                                if (db.ChannelDao().findByName(taskEditText.getText().toString(), taskEditText2.getText().toString()) != null)
                                                                    Toast.makeText(BasicContext, "channel già esistente!", Toast.LENGTH_SHORT).show();
                                                                else {
                                                                    // controllo se i parametri inseriti sono corretti
                                                                    DEFAULT_ID = taskEditText.getText().toString();
                                                                    DEFAULT_READ_KEY = taskEditText2.getText().toString();
                                                                    DEFAULT_WRITE_KEY = taskEditText3.getText().toString();
                                                                    if (testData(DEFAULT_ID, DEFAULT_READ_KEY,DEFAULT_WRITE_KEY)) {
                                                                        //comunico il database aggiornato al thread
                                                                        MyTimerTask.updateDatabase(db);
                                                                        Toast.makeText(BasicContext, "operazione eseguita correttamente!", Toast.LENGTH_SHORT).show();
                                                                        //segnalo al thread principale i nuovi id,key
                                                                        if (pos == -1) pos = 0;
                                                                        MainActivity.setDefaultSetting(DEFAULT_ID, DEFAULT_READ_KEY, DEFAULT_WRITE_KEY, pos);
                                                                    } else
                                                                        Toast.makeText(BasicContext, "operazione ERRATA!", Toast.LENGTH_SHORT).show();
                                                                    //segnalo eventuali modifiche
                                                                    adapter.notifyDataSetChanged();
                                                                }
                                                            }
                                                        })
                                                         .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                             @Override
                                                             public void onClick(DialogInterface dialog, int which) {
                                                                 Toast.makeText(BasicContext,"operazione annullata!",Toast.LENGTH_SHORT).show();
                                                             }
                                                         });
                                                 AlertDialog allert3=dialog3.create();
                                                 allert3.show();
                                            }
                                        })
                                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(BasicContext,"operazione annullata!",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                AlertDialog allert2=dialog2.create();
                                allert2.show();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(BasicContext,"operazione annullata!",Toast.LENGTH_SHORT).show();
                            }
                        });

                AlertDialog allert=dialog.create();
                allert.show();

                */