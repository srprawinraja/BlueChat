package com.example.BOneOnOneChat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.InputType;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class Name extends AppCompatActivity {
    private AppCompatEditText editText;
    private final int currentApiVersion = Build.VERSION.SDK_INT;
    private final int eleven=30;
    protected final String TABLE_NAME="USER";
    protected final String USER_NAME="USER_NAME";
    protected final String USER_ADDRESS="USER_ADDRESS";
    protected   final String PROFILE_BACKGROUND_COLOR="PROFILE_BACKGROUND_COLOR";
    protected final String KEY="key_ID";
    protected SQLiteDatabase db;
    private final int NOTIFICATION_PERMISSION = 200;
    protected String createTableQuery;
    private SharedPreferences sharedPreferences;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @SuppressLint("InlinedApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int BACKGROUND_LOCATION = 111;
        if(requestCode!=NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_CONNECT) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("permission", true);
                    editor.apply();
                    if (requestCode == BACKGROUND_LOCATION)
                        alert("Allow My Application to use your location", "You must manually select the option 'Allow all the time' for location in order for this app to work!", requestCode);
                    else
                        alert("Allow My Application to use your location", "Please grant permission to enable Bluetooth functionality.", requestCode);
                }
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && currentApiVersion == eleven)
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION);
                else {
                    String enterText = Objects.requireNonNull(editText.getText()).toString();

                    if (checkName(enterText)) {
                        redirect(enterText);
                    }
                }
            }
        }
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
            Toast.makeText(Name.this, "please enter your name " + enterText, Toast.LENGTH_SHORT).show();

            Toast.makeText(Name.this, " enter your name between 1 to 10 characters", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    private void redirect(String enterText) {
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = manager.getAdapter();
        bluetoothAdapter.enable();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("oldName", bluetoothAdapter.getName());
        editor.putString("name", enterText);
        editor.putBoolean("notificationHide", true);
        editor.putBoolean("notificationSound", true);
        int color = ContextCompat.getColor(Name.this, R.color.profilebackground);
        editor.putInt("profileColor", color);
        editor.apply();
        db = openOrCreateDatabase("my_database", Context.MODE_PRIVATE, null);
        // Get the current time
        createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_NAME + " TEXT, " + USER_ADDRESS + "TEXT" + PROFILE_BACKGROUND_COLOR + "TEXT)";
        db.execSQL(createTableQuery);
        Toast.makeText(Name.this, "Welcome " + enterText + "!", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(Name.this, MainActivity.class);
        i.putExtra("username", enterText);
         startActivity(i);
    }

    private void alert(String ms1,String ms2, int requestCode) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(ms1)
                .setMessage(ms2)
                .setPositiveButton("OK", (dialog, which) -> {
                    if(requestCode==NOTIFICATION_PERMISSION){
                        if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)){
                            openApplicationSetting();
                        }
                    }
                    else {
                        if (sharedPreferences.getBoolean("permission", false)) {
                            openApplicationSetting();
                        } else askPermission();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openApplicationSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @SuppressLint("InlinedApi")
    private void askPermission() {
        int twelve = 31;
        int thirteen=33;
        if(currentApiVersion>= twelve && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            int BLUETOOTH_PERMISSION = 101;

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                        BLUETOOTH_PERMISSION);
                if(currentApiVersion>=thirteen)requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);

        }
        else{
            int LOCATION_PERMISSION = 100;
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
            }
            else if(currentApiVersion==eleven  && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
            }
            else{
                if(checkName(Objects.requireNonNull(editText.getText()).toString())){
                    redirect(Objects.requireNonNull(editText.getText()).toString());
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editText=findViewById(R.id.editTextPhone);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        if(checkName(Objects.requireNonNull(editText.getText()).toString())){
                            redirect(Objects.requireNonNull(editText.getText()).toString());
                        }
                    } else {
                        alert("Notification Permissions Required","To receive message alerts, BlueChat requires notification permissions. Please grant the necessary permissions to enable this feature.",NOTIFICATION_PERMISSION);
                    }
                });
        Button next = findViewById(R.id.button2);
        next.setOnClickListener(v -> askPermission());
    }
}