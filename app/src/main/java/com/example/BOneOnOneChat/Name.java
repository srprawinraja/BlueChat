package com.example.BOneOnOneChat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class Name extends AppCompatActivity {
    private static final String CHANNEL_ID ="Notification" ;
    private AppCompatEditText editText;
    private String enterTxt;
    private ActivityResultLauncher<Intent> enableBtLauncher;
    private PermissionHandler permissionHandler;
    private SharedPreferences sharedPreferences;


    private Boolean checkName(String enterText) {
        if(enterText.isEmpty()) Toast.makeText(this, "please enter your name", Toast.LENGTH_SHORT).show();
        else if(enterText.length()>9) Toast.makeText(this, "enter your name between 1 to 9 character", Toast.LENGTH_SHORT).show();
        else if(enterText.charAt(enterText.length()-1)==' ' || enterText.charAt(0)==' ') Toast.makeText(this, "name cannot end or start with space", Toast.LENGTH_SHORT).show();
        else return true;
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, 10));
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 1000);
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
        return false;
    }

    @SuppressLint("MissingPermission")
    private void redirect(String enterText) {
        BluetoothAdapter  bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter.isEnabled()){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name", enterText);
            editor.putBoolean("notificationHide", true);
            editor.putBoolean("notificationSound", true);
            editor.putBoolean("sound", true);
            int color = ContextCompat.getColor(Name.this, R.color.profilebackground);
            editor.putInt("profileColor", color);
            editor.apply();
            Toast.makeText(Name.this, "Welcome " + enterText + "!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Name.this, MainActivity.class);
            i.putExtra("username", enterText);
            startActivity(i);
        }
        else turnOnBluetooth();
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editText=findViewById(R.id.editTextPhone);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        permissionHandler=new PermissionHandler(this);
        enableBtLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        redirect(enterTxt);
                    } else {

                        finishAffinity();
                    }
                }
        );

        CharSequence name = "Notification";
        String description = "Message Notification";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Button next = findViewById(R.id.button2);
        next.setOnClickListener(view -> {
            enterTxt= Objects.requireNonNull(editText.getText()).toString();
            if(checkName(enterTxt)){
                if(permissionHandler.currentApiVersion>= permissionHandler.twelve){
                    if(permissionHandler.isAllPermissionGiven()){
                        redirect(enterTxt);
                    }
                }
                else{
                    if(checkName(enterTxt)){
                        redirect(enterTxt);
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void turnOnBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

// Launch the intent using the launcher
        enableBtLauncher.launch(enableBtIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("inside request code", enterTxt+"inside");

        if(permissionHandler.NEAR_BY_SHARE_REQUEST_CODE==requestCode){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                redirect(enterTxt);
            }
            else{
                permissionHandler.showRationaleOrNot(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }
        else if(requestCode==permissionHandler.ACCESS_FINE_LOCATION_CODE){
            if(grantResults[0]==PackageManager.PERMISSION_DENIED){
                permissionHandler. showRationaleOrNot(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            else{
                redirect(enterTxt);

            }
        }


    }

}