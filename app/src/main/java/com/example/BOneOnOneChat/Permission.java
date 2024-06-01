package com.example.BOneOnOneChat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

public class Permission {

    private final Context context;
    private final int currentApiVersion;
    private final int twelve;
    private final int eleven;

    Permission(Context context){
        twelve=31;
        eleven=30;
        currentApiVersion= Build.VERSION.SDK_INT;
        this.context=context;
    }

    protected Boolean  isLocationEnable(){
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Alert("Location services are required for this application to function properly. Would you like to enable them now?",1);
            return false;
        }
        return true;
    }

    protected Boolean isAllPermissionGiven(){
        boolean check=true;

        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)!= PackageManager.PERMISSION_GRANTED && currentApiVersion>= twelve){
             Alert("Please enable location access permission in settings to proceed",0);
            check=false;
        }
        else{
            if(ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                Alert("Please enable location access permission in settings to proceed",0);
                check=false;

            }

            if(currentApiVersion==eleven && ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_BACKGROUND_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                Alert("You must manually select the option 'Allow all the time' for location in order for this app to work!",0);
                check=false;

            }

        }
        return check;
    }

    protected void Alert(String msg2, int code){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Allow My Application to use your location")
                .setMessage(msg2)
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent;
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);

                    if(code==1){
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                    else {
                        intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }

                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }




}
