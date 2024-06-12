package com.example.BOneOnOneChat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class editName extends AppCompatActivity {
    private EditText edit;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        edit=findViewById(R.id.editTextPhone);
        edit.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        Button ok = findViewById(R.id.button2);
        ImageButton back = findViewById(R.id.back_setting2);
        edit.requestFocus();

        new Handler().postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
        }, 200);

        back.setOnClickListener(v -> {
            Intent i=new Intent(getApplicationContext(),Profile.class);
            startActivity(i);
        });

        ok.setOnClickListener(v -> {
            String name=edit.getText().toString();
            if(checkName(name)){
                SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", name);
                editor.apply();
                edit.setText(name);
                Toast.makeText(editName.this, "Changed your Name Successfully", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, Profile.class);
                startActivity(i);

            }
        });
    }


    private Boolean checkName(String enterText) {
        if(!enterText.isEmpty() && enterText.length()<=10) {
            int count=0;
            for(char c:enterText.toCharArray()) {
                if(Character.isWhitespace(c)) count++;
            }
            if(count!=enterText.length()) {
                return true;
            }
            else Toast.makeText(this, "enter valid name", Toast.LENGTH_SHORT).show();
        }
        else {

            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, 10));

            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 1000);
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
            Toast.makeText(editName.this, "please enter your name " + enterText, Toast.LENGTH_SHORT).show();

            Toast.makeText(editName.this, " enter your name between 1 to 10 characters", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}