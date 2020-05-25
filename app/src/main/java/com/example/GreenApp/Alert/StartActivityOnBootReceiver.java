package com.example.GreenApp.Alert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

//classe che mi permette all'avvio del dispositivo di avviare il servizio in background
public class StartActivityOnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            ExampleService.stoptimer();
            Intent serviceIntent = new Intent(context, ExampleService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
            Log.d("STARTACTIVITYONBOOT","BOOT COMPLETATO");
        }
    }
}
