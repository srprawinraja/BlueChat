package com.example.BOneOnOneChat;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class BluetoothSearch extends AppCompatActivity {

    private LottieAnimationView searchButton;
    protected  ProgressBar progressBar;
    private ArrayList<String> allDevicesName;
    private HashSet<String> deviceAdress;
    private ListView listView;
    private BluetoothAdapter bluetoothAdapter;
    private Permission permission;
    private ArrayList<BluetoothDevice> allDevices;
    private geek gk;
    private int width=100;
    private TextView status;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_search);
        permission=new Permission(this);
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

        if(permission.isAllPermissionGiven()){
            if(permission.isLocationEnable()) {
            try{
                if(!bluetoothAdapter.isEnabled())bluetoothAdapter.enable();}
            catch (NullPointerException e){
                setBluetooth(1);
            }
                progressBar.setVisibility(View.VISIBLE);
                deviceDiscoverble();
            }

        }
        searchButton.setOnClickListener(v -> {
            if(permission.isAllPermissionGiven()){
                if (permission.isLocationEnable()) {
                try{
                    if(!bluetoothAdapter.isEnabled())bluetoothAdapter.enable();}
                catch (NullPointerException e){
                    setBluetooth(1);
                }
                    clear();
                    if(gk!=null)gk.notifyDataSetChanged();
                    progressBar.setVisibility(View.VISIBLE);
                    bluetoothAdapter.startDiscovery();
                    deviceDiscoverble();
                    listView.setDividerHeight(20);
                    status.setText(" ");

                }


            }


        });

        listView.setOnItemClickListener((parent, view, position, id) -> {

            if(permission.isAllPermissionGiven()){
                try{
                    if(!bluetoothAdapter.isEnabled())bluetoothAdapter.enable();}
                catch (NullPointerException e){
                    setBluetooth(0);
                }
            BluetoothDevice dv=allDevices.get(position);
            bluetoothAdapter.cancelDiscovery();
            Intent i=new Intent(BluetoothSearch.this,BluetoothChatting.class);
            String name;
            String address;
            name = allDevicesName.get(position);
            address=dv.getAddress();
            try{

                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.putExtra("device_name",name);
                i.putExtra("device_address",address);
                i.putExtra("option",dv);
                i.putExtra("decider","search");
                startActivity(i);
            }catch (NullPointerException e) {
                Toast.makeText(this, "device not found", Toast.LENGTH_SHORT).show();
            }
            }
        });

        searchExit.setOnClickListener(v -> {
            if(bluetoothAdapter.isDiscovering())bluetoothAdapter.cancelDiscovery();
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);

        });

    }

    private void clear() {
        allDevicesName.clear();
        deviceAdress.clear();
    }

    @SuppressLint("MissingPermission")
    private void setBluetooth(int code) {
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = manager.getAdapter();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled())bluetoothAdapter.enable();
        if(code==1) bluetoothAdapter.startDiscovery();
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
        if(permission.isAllPermissionGiven()) {
            try{
                if(!bluetoothAdapter.isEnabled())bluetoothAdapter.enable();}
            catch (NullPointerException e){
                setBluetooth(0);
            }
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120); // Set the duration in seconds
            startActivity(discoverableIntent);

        }
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
                        Toast.makeText(BluetoothSearch.this, "no device were found", Toast.LENGTH_SHORT).show();
                    } else {
                        setButton_w_h(0);
                        status.setText("(" + allDevicesName.size() + ")" + " friends  found");
                    }
                    progressBar.setVisibility(View.GONE);
                    gk = new geek(BluetoothSearch.this, allDevicesName.toArray(new String[0]), pic);
                    listView.setAdapter(gk);
                }

        }
    };

    @SuppressLint("MissingPermission")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(permission.isAllPermissionGiven()) {
            try{
                if(!bluetoothAdapter.isEnabled())bluetoothAdapter.enable();}
            catch (NullPointerException e){
                setBluetooth(0);
            }
            if (bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }
}




