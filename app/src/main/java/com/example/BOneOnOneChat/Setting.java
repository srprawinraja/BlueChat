package com.example.BOneOnOneChat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

public class Setting extends AppCompatActivity {
    private Intent i;
    private ImageButton switch1,switch2,switch3;
    private Boolean BoolSwitch1,BoolSwitch2,BoolSwitch3;
    private SharedPreferences sharedPreferences;
    private  SharedPreferences.Editor editor;
    @SuppressLint({"MissingPermission", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ImageButton back = findViewById(R.id.back_setting);
        Button logout = findViewById(R.id.button1);
        Button profilEdit = findViewById(R.id.button_edit);
        Button notify1 = findViewById(R.id.button_notify);
        Button notify2 = findViewById(R.id.button_notify_silent);
        Button sound = findViewById(R.id.button_mute);
        switch1=findViewById(R.id.switch1);
        switch2=findViewById(R.id.switch2);
        switch3=findViewById(R.id.switch3);

        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        back.setOnClickListener(v -> {
            i=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        });

        notify1.setOnClickListener(v -> {

            if(BoolSwitch1){
                BoolSwitch1=false;
                editor.putBoolean("notificationHide",false);
                editor.commit();
                switch1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_not_interested_24));
            }
            else{
                BoolSwitch1=true;
                switch1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_check_circle_24));
                editor.putBoolean("notificationHide",true);
                editor.commit();
            }

        });
        notify2.setOnClickListener(v -> {
            if(BoolSwitch2)  {
                BoolSwitch2=false;
                switch2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_not_interested_24));
                editor.putBoolean("notificationSound",false);
                editor.commit();
            }
            else {
                BoolSwitch2=true;
                switch2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_check_circle_24));
                editor.putBoolean("notificationSound",true);
                editor.commit();
            }
        });

        sound.setOnClickListener(v -> {
            if(BoolSwitch3)  {
                BoolSwitch3=false;
                switch3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_not_interested_24));
                editor.putBoolean("sound",false);
                editor.commit();
            }
            else {
                BoolSwitch3=true;
                switch3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_check_circle_24));
                editor.putBoolean("sound",true);
                editor.commit();
            }
        });



        profilEdit.setOnClickListener(v -> {
            i=new Intent(getApplicationContext(),Profile.class);
            startActivity(i);
        });
       logout.setOnClickListener(v -> {


           SharedPreferences preferences =getSharedPreferences("MyPreferences",MODE_PRIVATE);
           SharedPreferences.Editor editor = preferences.edit();
           editor.clear();
           editor.apply();
           SqDatabase s=new SqDatabase(this);
           s.deleteDatabase(this);
           i=new Intent(getApplicationContext(),Name.class);
           startActivity(i);

       });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        i=new Intent(this,MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();

        BoolSwitch1=sharedPreferences.getBoolean("notificationHide",false);
        BoolSwitch2=sharedPreferences.getBoolean("notificationSound",false);
        BoolSwitch3=sharedPreferences.getBoolean("sound",false);
        if(BoolSwitch1){

            //switch1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_check_circle_24));
            switch1.setImageDrawable( AppCompatResources.getDrawable(this,(R.drawable.ic_baseline_check_circle_24)));
        }
        else{
            switch1.setImageDrawable(AppCompatResources.getDrawable(this,(R.drawable.ic_baseline_not_interested_24)));

           // switch1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_not_interested_24));
        }
        if(BoolSwitch2){
            switch2.setImageDrawable(AppCompatResources.getDrawable(this,(R.drawable.ic_baseline_check_circle_24)));

        }
        else{
            switch2.setImageDrawable(AppCompatResources.getDrawable(this,(R.drawable.ic_baseline_not_interested_24)));

            //switch2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_not_interested_24));
        }
        if(BoolSwitch3){
            switch3.setImageDrawable(AppCompatResources.getDrawable(this,(R.drawable.ic_baseline_check_circle_24)));

        }
        else{
            switch3.setImageDrawable(AppCompatResources.getDrawable(this,(R.drawable.ic_baseline_not_interested_24)));

            //switch2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_not_interested_24));
        }

    }
}