package com.example.BOneOnOneChat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(() -> {

            SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
            String myString = sharedPreferences.getString("name", null);

            Intent i;
            if(myString==null){
                i = new Intent(SplashScreen.this, Name.class);
            }
            else{
                i = new Intent(SplashScreen.this, MainActivity.class);
            }
            startActivity(i);
            finish();

        },1000);
    }
}