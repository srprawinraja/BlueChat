package com.example.BOneOnOneChat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class BluetoothSearch extends AppCompatActivity {

    private ActivityResultLauncher<Intent> enableBtLauncher;
    private LottieAnimationView searchButton;
    protected  ProgressBar progressBar;
    private ArrayList<String> allDevicesName;
    private HashSet<String> deviceAdress;
    private ListView listView;
    private int code;
    private BluetoothAdapter bluetoothAdapter;
    private PermissionHandler permissionHandler;
    private ArrayList<BluetoothDevice> allDevices;
    private int width=100;
    private TextView status;


    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_search);
        allDevicesName=new ArrayList<>();
        allDevices=new ArrayList<>();
        deviceAdress=new HashSet<>();

        IntentFilter fill= new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothDeviceListener , fill);
        IntentFilter fill1= new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothDeviceListener , fill1);

        listView=findViewById(R.id.listHome);
        listView.setDivider(null);
        searchButton=findViewById(R.id.img);
        progressBar=findViewById(R.id.progress);
        ImageButton searchExit = findViewById(R.id.back);
        status=findViewById(R.id.textView);

        permissionHandler=new PermissionHandler(this);

        enableBtLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if(code==0)search();
                    } else {
                        finishAffinity();
                    }
                }
        );

        search();

        searchButton.setOnClickListener(v -> search());

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if(permissionHandler.currentApiVersion<permissionHandler.twelve || permissionHandler.isAllPermissionGiven()) {
                if(bluetoothAdapter.isEnabled()) {
                    BluetoothDevice dv = allDevices.get(position);
                    String name = allDevicesName.get(position);
                    String address = dv.getAddress();
                    goToChat(name, address, dv);
                }else turnOnBluetooth(1);
            }
        });

        searchExit.setOnClickListener(v -> gotoMain());

    }

    @SuppressLint("MissingPermission")
    private void gotoMain() {
        if(bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void goToChat(String name, String address, BluetoothDevice dv) {
        if(bluetoothAdapter.isDiscovering())bluetoothAdapter.cancelDiscovery();
        Intent i=new Intent(BluetoothSearch.this,BluetoothChatting.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("device_name",name);
        i.putExtra("device_address",address);
        i.putExtra("option",dv);
        i.putExtra("decider","search");
        startActivity(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    private void search() {
        clear();
        if(permissionHandler.currentApiVersion<permissionHandler.twelve || permissionHandler.isAllPermissionGiven()){
            bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
            if(!bluetoothAdapter.isEnabled()){
                turnOnBluetooth(0);
            }
            else {
                if(permissionHandler.isLocationEnable()) {

                    if(!bluetoothAdapter.isDiscovering()) {
                        status.setText("");
                        Toast.makeText(this, "Warning: Your saved name won't appear to your friends. The Bluetooth device name will be shown instead.", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.VISIBLE);
                        deviceDiscoverble();
                        bluetoothAdapter.startDiscovery();
                    }
                }else permissionHandler.Alert(3);
            }
        }

    }

    private void clear() {
        allDevicesName.clear();
        deviceAdress.clear();
    }


    private void turnOnBluetooth(int code) {
        this.code=code;
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        enableBtLauncher.launch(enableBtIntent);
    }

    //search button width heigth set
    private void setButton_w_h(int operator) {

        ViewGroup.LayoutParams layoutParams = searchButton.getLayoutParams();

        if(operator==0 && width==100) {
            layoutParams.width = searchButton.getWidth() - 300;
            layoutParams.height = searchButton.getHeight() -300;
            width-=10;
            searchButton.pauseAnimation();
        }
        else if(operator==6 && width==90){
            layoutParams.width = searchButton.getWidth() + 300;
            layoutParams.height = searchButton.getHeight() + 300;
            width+=10;
            searchButton.playAnimation();

        }

        searchButton.requestLayout();
    }




    @SuppressLint("MissingPermission")
    private void deviceDiscoverble() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120); // Set the duration in seconds
        startActivity(discoverableIntent);

    }

    BroadcastReceiver bluetoothDeviceListener=new BroadcastReceiver() {
        @SuppressLint({"MissingPermission", "SetTextI18n"})
        @Override
        public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if(device!=null && !deviceAdress.contains(device.getAddress())&& !device.getName().isEmpty()){
                            deviceAdress.add(device.getAddress());
                            allDevices.add(device);
                            allDevicesName.add(device.getName());

                        }
                }
                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    int[] pic = new int[allDevicesName.size()];
                    Arrays.fill(pic, R.drawable.ic_baseline_person_24);
                    if (allDevicesName.isEmpty()) {
                        setButton_w_h(6);
                        status.setText("no friends found");
                    } else {
                        setButton_w_h(0);
                        status.setText("(" + allDevicesName.size() + ")" + " friends  found");
                    }
                    progressBar.setVisibility(View.GONE);
                    geek gk = new geek(BluetoothSearch.this, allDevicesName.toArray(new String[0]), pic);
                    listView.setAdapter(gk);
                }

        }
    };

    @SuppressLint("MissingPermission")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotoMain();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothDeviceListener);
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
                if((permissionHandler.currentApiVersion==permissionHandler.eleven || permissionHandler.currentApiVersion==permissionHandler.ten) && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)==PackageManager.PERMISSION_DENIED){
                    permissionHandler.Alert(2);

                }

            }
            else{
                permissionHandler. showRationaleOrNot(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }


    }


}




