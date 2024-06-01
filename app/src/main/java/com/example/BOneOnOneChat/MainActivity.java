package com.example.BOneOnOneChat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
    private Intent i;
    private Geek1 geek1;
    private SqDatabase sqDatabase;
    private ArrayList <String[]> userDetail=new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private Permission permission;
    private BluetoothAdapter mBluetoothAdapter;
    private String oldName;
    private String name;
    @Override
    protected void onResume() {
        super.onResume();

        try{
            userDetail=sqDatabase.retrieve();
            geek1.notifyDataSetChanged();
        }
        catch (NullPointerException ignored){}
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState==null) SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        if(sharedPreferences.getString("name", null)==null){
            Intent i=new Intent(this,Name.class);
            startActivity(i);
            finish();
        }
        setContentView(R.layout.activity_main);
        LottieAnimationView arrw = findViewById(R.id.img);
        ImageView img = findViewById(R.id.emoji);
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        oldName=sharedPreferences.getString("oldName", "");
        name=sharedPreferences.getString("name", "");
        ListView listNew = findViewById(R.id.listnew);
        listNew.setDivider(null);
        listNew.setDividerHeight(60);
        ImageButton bluetoothPermission = findViewById(R.id.blue);
        ImageButton setting = findViewById(R.id.imageButton);
        sqDatabase = new SqDatabase(MainActivity.this);
        userDetail=sqDatabase.retrieve();
        permission=new Permission(this);
        if(!userDetail.isEmpty()) {
            arrw.setVisibility(View.INVISIBLE);
            img.setVisibility(View.INVISIBLE);
        }
            String[] A=new String[userDetail.size()];
            geek1=new Geek1(this,A,userDetail);
            listNew.setAdapter(geek1);

        if(permission.isAllPermissionGiven()) {
            try{
                if(!mBluetoothAdapter.isEnabled())mBluetoothAdapter.enable();
                if(!mBluetoothAdapter.getName().equals(name))mBluetoothAdapter.setName(name);
            }
            catch (NullPointerException e){
                setBluetooth(2);
            }

        }

            setting.setOnClickListener(v -> {
            i = new Intent(getApplicationContext(), Setting.class);
            startActivity(i);
        });

        bluetoothPermission.setOnClickListener(v -> {
            if(permission.isAllPermissionGiven()) {
                try{
                    if(!mBluetoothAdapter.isEnabled())mBluetoothAdapter.enable();

                }
                catch (NullPointerException e){
                    setBluetooth(0);
                }
                i = new Intent(getApplicationContext(), BluetoothSearch.class);
                startActivity(i);
            }
        });
        listNew.setOnItemClickListener((parent, view, position, id) -> {
            if(permission.isAllPermissionGiven()) {
                try{
                    if(!mBluetoothAdapter.isEnabled())mBluetoothAdapter.enable();}


                catch (NullPointerException e){
                    setBluetooth(0);
                }
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(userDetail.get(position)[1]);
                Intent i = new Intent(this, BluetoothChatting.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.putExtra("device_name", (device.getName()==null)? userDetail.get(position)[0] : device.getName());
                i.putExtra("device_address", userDetail.get(position)[1]);
                i.putExtra("option", device);
                i.putExtra("decider", "main");
                startActivity(i);}

        });


    }

    @SuppressLint("MissingPermission")
    private void setNewName(String name) {
        mBluetoothAdapter.setName(name);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(permission.isAllPermissionGiven()) {
            try{
                if(!mBluetoothAdapter.isEnabled())mBluetoothAdapter.enable();
                if(!mBluetoothAdapter.getName().equals(oldName))mBluetoothAdapter.setName(oldName);
            }
            catch (NullPointerException e){
                setBluetooth(1);
            }

        }

    }

    @SuppressLint("MissingPermission")
    private void setOldName(String oldName) {
        mBluetoothAdapter.setName(oldName);
    }

    @SuppressLint("MissingPermission")
    private void setBluetooth(int code) {
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBluetoothAdapter.isEnabled()) mBluetoothAdapter.enable();
        if(code==1 && !mBluetoothAdapter.getName().equals(oldName)) setOldName(sharedPreferences.getString("oldName", ""));
        else if(code==2 && !mBluetoothAdapter.getName().equals(name)) setNewName(sharedPreferences.getString("name", ""));
    }
}



