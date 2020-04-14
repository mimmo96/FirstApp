package com.example.firstapp.Irrigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.firstapp.R;

public class IrrigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.irrigation_activity);
    }

    public static Intent getActivityintent(Context context){
        Intent intent=new Intent(context, IrrigationActivity.class);
        return intent;
    }
}
