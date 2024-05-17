package com.trindade.stringscreator.utils;

import androidx.appcompat.app.AppCompatActivity; 
import androidx.activity.EdgeToEdge;

import android.os.Bundle;
import android.view.View;


public class ThemedActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        phoneBars();
    }
    
    public void phoneBars(){
        /*EdgeToEdge.enable(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);*/
    }    
}
