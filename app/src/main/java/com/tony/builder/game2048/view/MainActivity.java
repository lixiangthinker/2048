package com.tony.builder.game2048.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tony.builder.game2048.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); //not working for AppCompatActivity
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
    }
}
