package com.example.firstapp.Irrigation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    private static Intent intent1;
    private static Context context;

    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context1, Intent intent) {
        context=context1;
        intent1 = new Intent(context, MyNewIntentService.class);
        context.startService(intent1);
        Log.d("SERVICES","STARTED");
    }

}