package com.example.BOneOnOneChat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.splashscreen.SplashScreen;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
    private Intent i;
    private BluetoothAdapter bluetoothAdapter;
    private ActivityResultLauncher<Intent> enableBtLauncher;
    private Geek1 geek1;
    private int code;
    private String userName;
    private SqDatabase sqDatabase;
    private ArrayList <String[]> userDetail=new ArrayList<>();
    private PermissionHandler permissionHandler;
    private int position;
    @Override
    protected void onResume() {
        super.onResume();

        try{
            userDetail=sqDatabase.retrieve();
            geek1.notifyDataSetChanged();
        }
        catch (NullPointerException ignored){}
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }


    @SuppressLint({"MissingPermission", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        userName= sharedPreferences.getString("name", "");
        if(userName.isEmpty()){
            Intent intent=new Intent(this, Name.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_main);

        LottieAnimationView arrw = findViewById(R.id.img);
        ImageView img = findViewById(R.id.emoji);
        ImageButton  search=findViewById(R.id.search_icon);
        ImageButton setting = findViewById(R.id.imageButton);

        ListView listNew = findViewById(R.id.listnew);
        listNew.setDivider(null);
        listNew.setDividerHeight(60);

        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        userName= sharedPreferences.getString("name", "");



        permissionHandler=new PermissionHandler(this);
        sqDatabase = new SqDatabase(MainActivity.this);

        userDetail=sqDatabase.retrieve();
        if(!userDetail.isEmpty()) {
            arrw.setVisibility(View.INVISIBLE);
            img.setVisibility(View.INVISIBLE);
        }
        String[] A=new String[userDetail.size()];
        geek1=new Geek1(this,A,userDetail);
        listNew.setAdapter(geek1);

        enableBtLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if(code==1 ) goToChat(userName, position);
                        else goToSearch();
                    } else {
                        finishAffinity();
                    }
                }
        );


        setting.setOnClickListener(v -> goToSetting());

        search.setOnClickListener(v -> {
            Log.d("this is clicked","click");
            if(permissionHandler.isAllPermissionGiven()) {
                 bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(bluetoothAdapter.isEnabled()){
                   goToSearch();
                }else turnOnBluetooth(0);
            }
        });

        listNew.setOnItemClickListener((parent, view, position, id) -> {
            this.position=position;
           if(permissionHandler.currentApiVersion<permissionHandler.twelve ||permissionHandler.isAllPermissionGiven()){
               bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
               if(!bluetoothAdapter.isEnabled()) turnOnBluetooth(1);
               else  goToChat(userName, position);
           }

        });


    }

    private void goToSetting() {
        i = new Intent(getApplicationContext(), Setting.class);
        startActivity(i);
    }
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void goToSearch() {
        if(permissionHandler.isLocationEnable()) {
            i = new Intent(getApplicationContext(), BluetoothSearch.class);
            startActivity(i);
        }else permissionHandler.Alert(3);
    }

    private void goToChat(String userName, int position) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(userDetail.get(position)[1]);
        Intent i = new Intent(this, BluetoothChatting.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("device_name", (userName==null)? userDetail.get(position)[0] : userName);
        i.putExtra("device_address", userDetail.get(position)[1]);
        i.putExtra("option", device);
        i.putExtra("decider", "main");
        startActivity(i);
    }

    private void turnOnBluetooth(int code) {
        this.code=code;
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        enableBtLauncher.launch(enableBtIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(permissionHandler.NEAR_BY_SHARE_REQUEST_CODE==requestCode){
            if(grantResults[0]== PackageManager.PERMISSION_DENIED){
                permissionHandler.showRationaleOrNot(android.Manifest.permission.BLUETOOTH_CONNECT);
            }

        }
        else if(requestCode==permissionHandler.ACCESS_FINE_LOCATION_CODE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if((permissionHandler.currentApiVersion==permissionHandler.eleven || permissionHandler.currentApiVersion==permissionHandler.ten) &&ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)==PackageManager.PERMISSION_DENIED){
                    permissionHandler.Alert(2);
                }

            }
            else{
                permissionHandler. showRationaleOrNot(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }


    }



}



