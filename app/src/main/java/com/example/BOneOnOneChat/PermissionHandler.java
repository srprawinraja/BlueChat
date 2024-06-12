package com.example.BOneOnOneChat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class PermissionHandler {
    private final Context context;
    protected final int  currentApiVersion;
    protected final int twelve;
    protected final int eleven;
    protected final int ten;
    private final String[] messages;
    protected final int NEAR_BY_SHARE_REQUEST_CODE;
    protected final int BLUETOOTH_ENABLE_REQUEST_CODE;
    protected final int ACCESS_FINE_LOCATION_CODE;
    PermissionHandler(Context context){
        this.context=context;
        currentApiVersion= Build.VERSION.SDK_INT;
        twelve=Build.VERSION_CODES.S;
        eleven=Build.VERSION_CODES.R;
        ten=Build.VERSION_CODES.Q;
        ACCESS_FINE_LOCATION_CODE=11;
        NEAR_BY_SHARE_REQUEST_CODE=12;
        BLUETOOTH_ENABLE_REQUEST_CODE=100;
        messages=new String[]{"To connect with nearby devices, our app needs access to your device's location. Please grant the permission.", "To connect with nearby devices, our app needs access to your device's location. You can grant this permission manually in the app settings.", "You must manually select the option 'Allow all the time' for location in order for this app to work!","Please turn on location services to enable Bluetooth scanning."};

    }
    @RequiresApi(api = Build.VERSION_CODES.S)
    protected Boolean isAllPermissionGiven(){
        boolean given= currentApiVersion < twelve || context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_DENIED;
        if(currentApiVersion<twelve && context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED) given=false;
        else if((currentApiVersion==eleven || currentApiVersion==ten) && context.checkSelfPermission(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)== PackageManager.PERMISSION_DENIED) given=false;
        if(!given) askPermission();
        return given;
    }
    private void openApplicationSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }
    protected Boolean  isLocationEnable(){
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    @RequiresApi(api = Build.VERSION_CODES.S)
    protected void askPermission(){
        if(currentApiVersion>=twelve && context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT)== PackageManager.PERMISSION_DENIED){
            nearBysharePermission();
        }
        if(currentApiVersion<twelve && context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED){
            accessFineLocation();
        }
        else if(currentApiVersion==eleven || currentApiVersion==ten && context.checkSelfPermission(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)== PackageManager.PERMISSION_DENIED) Alert(2);

    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    protected void showRationaleOrNot(String manifestPermission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, manifestPermission)){
            Alert(0);
        }
        else{
            Alert(1);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void nearBysharePermission() {
        ActivityCompat.requestPermissions((Activity) context,new String[]{android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.BLUETOOTH_ADVERTISE, android.Manifest.permission.BLUETOOTH_SCAN},NEAR_BY_SHARE_REQUEST_CODE);
    }
    private void accessFineLocation() {
        ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_FINE_LOCATION_CODE);
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    protected void Alert(int index){


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enable Location Access")
                .setMessage(messages[index])
                .setPositiveButton("OK", (dialog, which) -> {
                    Log.d("prawin","alert");
                    if(index==1)openApplicationSetting();
                    else if(index==2)openApplicationSetting();
                    else if(index==3){
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                    else askPermission();
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
