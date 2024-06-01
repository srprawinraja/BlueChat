package com.example.BOneOnOneChat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;


public class BluetoothChatting extends AppCompatActivity {
    private  NotificationManager notificationManager;
    private TextView status;
    private  BluetoothAdapter bluetoothAdapter;
    private EditText edit;
    private AlertDialog.Builder builder;
    public static Boolean mute=false;
    private String device_name,device_address;
    private String color;
    private String decider;
    private RecyclerView.Adapter adapter;
    private  SqDatabase sqDatabase;
    private String SecondMsg="",ThirdMsg="",FourthMsg="",FivthMsg="";
    private final int currentApiVersion = Build.VERSION.SDK_INT;
    private BluetoothDevice bluetoothDevice;
    private final String secretMessage="lkjafr34[2;52]24'2d";
    private ClientClass clientClass;
    private  ServerClass serverClass,serverClass1;
    private Permission permission;
    private SharedPreferences sharedPreferences;
    private SpannableString[] spanArray;
    private Boolean ScreenOff=true,check;
    SendReceive sendReceive;
    private int FiveNotify=1,count=0;
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;
    static final int STATE_WAITING=6;
    private Boolean p=false;
    private  String[] storeMsg;
    private LinearLayout item;
    private List<BluetoothGetSet> messagesList = new ArrayList<>();
    private BlueAdapter blueAdapter;
    private RecyclerView userMessagesList;
    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");


    private final Handler handler=new Handler(new Handler.Callback() {
        @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what)
            {
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
                case STATE_WAITING:
                    status.setText("Waiting to be Connected");
                    break;

                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    if(tempMsg.length()>19 && tempMsg.startsWith(secretMessage) ) {
                        Message message = Message.obtain();
                        message.what = STATE_CONNECTED;
                        handler.sendMessage(message);
                        if(!check) {
                            check=true;
                            sendReceive.write((secretMessage + color).getBytes());
                            sqDatabase.add(device_name, device_address, tempMsg.substring(20));
                        }
                    }
                    else {
                        int thirteen=33;
                        Date currentTime = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", new Locale("en", "IN"));
                        String formattedTime = sdf.format(currentTime);
                        BluetoothGetSet bluetoothObj = new BluetoothGetSet();
                        bluetoothObj.setFrom("psf");
                        bluetoothObj.setMessage(tempMsg);
                        bluetoothObj.setTime(formattedTime);
                        messagesList.add(bluetoothObj);
                        if (ScreenOff) {
                            if(currentApiVersion < thirteen || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
                            showNotification(tempMsg);
                        } }
                        else{
                            blueAdapter.notifyDataSetChanged();
                            userMessagesList.smoothScrollToPosition(Objects.requireNonNull(userMessagesList.getAdapter()).getItemCount());
                        }

                               }
                    break;
            }
            return false;
        }
    });

    private void showNotification(String Messages) {
        //String hiText = "<font color='#FF0000'>hi</font>";
        // Html.fromHtml(hiText)

        count+=1;
        if(FiveNotify>=1){
            SecondMsg = storeMsg[0];
            storeMsg[0]=" "+device_name+" "+Messages;
            spanArray[0]=span(storeMsg[0]);
        }
        if(FiveNotify>=2){
            ThirdMsg=storeMsg[1];
            storeMsg[1]=SecondMsg;
            spanArray[1]=span(storeMsg[1]);
        }
        if(FiveNotify>=3){
            FourthMsg=storeMsg[2];
            storeMsg[2]=ThirdMsg;
            spanArray[2]=span(storeMsg[2]);

        }
        if(FiveNotify>=4){
            FivthMsg=storeMsg[3];
            storeMsg[3]=FourthMsg;
            spanArray[3]=span(storeMsg[3]);
        }
        if(FiveNotify>=5){
            storeMsg[4]=FivthMsg;
            spanArray[4]=span(storeMsg[4]);
        }
        if(FiveNotify<5) FiveNotify+=1;

        if(sharedPreferences.getBoolean("notificationSound",false)&&!mute) {
            // Vibrate for 500 milliseconds

            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(200,-1));

        }


        Intent intent = new Intent(getApplicationContext(),BluetoothChatting.class);
        //intent.putExtra("broad",true);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder;
        if(sharedPreferences.getBoolean("notificationHide",false)&&!mute) {

            Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notifiy);
            Intent buttonIntent = new Intent(this, NotificationReceiver.class);
             @SuppressLint("UnspecifiedImmutableFlag") PendingIntent buttonPendingIntent = PendingIntent.getBroadcast(BluetoothChatting.this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                    R.color.red, "Mute", buttonPendingIntent)
                    .build();


            builder = new NotificationCompat.Builder(this, "Notification")

                    .setContentTitle("\n")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setSmallIcon(R.drawable.b44)
                    .addAction(action)
                    .setSound(soundUri)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);





            builder.setContentText(span(" " + device_name + " " + Messages));

            builder.setStyle(new NotificationCompat.InboxStyle()

                    .addLine(spanArray[4])
                    .addLine(spanArray[3])
                    .addLine(spanArray[2])
                    .addLine(spanArray[1])
                    .addLine(spanArray[0])
            );
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(BluetoothChatting.this);

            notificationManager.notify(1, builder.build());

        }
        else if(!mute){
            builder = new NotificationCompat.Builder(this, "Notification")

                    .setContentTitle("bluetoothChat")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setSmallIcon(R.drawable.circle)
                    .setContentText(count+" new notification")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(BluetoothChatting.this);

            notificationManager.notify(1, builder.build());

        }





    }

    private SpannableString span(String msg) {
        SpannableString spannableString = new SpannableString(msg);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLACK);
        spannableString.setSpan(colorSpan,1,device_name.length()+2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }




    @SuppressLint({"MissingPermission", "HardwareIds", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenOff=true;
        check=false;
        builder = new AlertDialog.Builder(this);
        spanArray=new SpannableString[5];
        storeMsg = new String[5];
        Intent i = getIntent();
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        color= String.valueOf(sharedPreferences.getInt("profileColor", 0));
        setContentView(R.layout.activity_bluetooth_chatting);
        permission=new Permission(this);
        userMessagesList= findViewById(R.id.bluetooth_cycle);
        TextView text = findViewById(R.id.bluetooth_textView7);
        edit=findViewById(R.id.bluetooth_input_message);
        sqDatabase = new SqDatabase(BluetoothChatting.this);
        ImageButton backSetting = findViewById(R.id.back_setting3);
        Button connect = findViewById(R.id.connect);
        Button disconnect = findViewById(R.id.disconnect);
        edit=findViewById(R.id.bluetooth_input_message);
        status=findViewById(R.id.bluetooth_textView2);
        ImageButton option = findViewById(R.id.option);
        RelativeLayout topLay = findViewById(R.id.bluetooth_rel);
        ImageView send = findViewById(R.id.bluetooth_send);
        NotificationChannel channel1=new NotificationChannel("Notification","Notification", NotificationManager.IMPORTANCE_HIGH);
        setNotify(channel1);
        item=findViewById(R.id.item);

        device_name = i.getStringExtra("device_name");
        device_address=i.getStringExtra("device_address");
        decider = i.getStringExtra("decider");
        bluetoothDevice = Objects.requireNonNull(getIntent().getExtras()).getParcelable("option");

        Gson gson = new Gson();
        Type type=new TypeToken<ArrayList<BluetoothGetSet>>(){}.getType();
        String serialize=sharedPreferences.getString(device_address,null);
        if(serialize!=null){
            messagesList= gson.fromJson(serialize,type);
        }

        blueAdapter = new BlueAdapter(messagesList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(blueAdapter);
         adapter = userMessagesList.getAdapter();
        if (adapter != null && adapter.getItemCount() > 0) userMessagesList.scrollToPosition(adapter.getItemCount() - 1);
        if(permission.isAllPermissionGiven()){
            setBluetooth();
            serverClass=new ServerClass();
            clientClass = new ClientClass(bluetoothDevice);
            clientClass.start();
        }else status.setText("");
        // up and down changed
        if(device_name.length()>10) {
            text.setText(device_name.substring(0, 9));
        }
        else{ text.setText(device_name);
        }
        option.setOnClickListener(v -> {

            if(p) {item.setVisibility(View.GONE); p=false;}
            else {item.setVisibility(View.VISIBLE); p=true;}

        });
        backSetting.setOnClickListener(v -> {
            if(status.getText().toString().equals("Connected"))alert();
            else{
                try {
                    serverClass.serverSocket.close();
                    serverClass1.serverSocket.close();
                } catch (IOException |NullPointerException e) {
                    e.printStackTrace();
                }
                serverClass.run=false;
               try{ serverClass1.run=false;}catch(NullPointerException ignored){}
                super.onBackPressed();
            }
        });
        topLay.setOnClickListener(v -> item.setVisibility(View.GONE));
        send.setOnClickListener(v -> {
            if(permission.isAllPermissionGiven()) {
                try{
                    bluetoothAdapter.enable();
                }
                catch(NullPointerException e){
                    setBluetooth();
                }
                String alpha = edit.getText().toString();
                if (!alpha.isEmpty() && status.getText().toString().equals("Connected")) {
                    Date currentTime = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", new Locale("en", "IN"));
                    String formattedTime = sdf.format(currentTime);
                    String string = String.valueOf(edit.getText());
                    try {
                        sendReceive.write(string.getBytes());
                        BluetoothGetSet bluetoothObj = new BluetoothGetSet();
                        bluetoothObj.setFrom("sender");
                        bluetoothObj.setMessage(alpha);
                        bluetoothObj.setTime(formattedTime);
                        messagesList.add(bluetoothObj);
                        blueAdapter.notifyDataSetChanged();
                        userMessagesList.smoothScrollToPosition(Objects.requireNonNull(userMessagesList.getAdapter()).getItemCount());
                        edit.setText("");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        connect.setOnClickListener(v -> {

            if(permission.isAllPermissionGiven()) {

                if (status.getText().toString().isEmpty() || status.getText().toString().equals("Waiting to be Connected"))
                        try{
                            bluetoothAdapter.enable();
                        }
                        catch(NullPointerException e){
                            setBluetooth();
                        }
                        try{serverClass.run=false;}catch (NullPointerException e){
                            serverClass=new ServerClass();
                        }
                        try{serverClass1.run=false;}catch (NullPointerException ignored){}
                        try {
                            serverClass.serverSocket.close();
                            serverClass1.serverSocket.close();
                        } catch (IOException | NullPointerException e) {
                            e.printStackTrace();
                        }
                        clientClass = new ClientClass(bluetoothDevice);
                        clientClass.start();

            }
        });
        disconnect.setOnClickListener(v -> {
            if(status.getText().toString().equals("Connected")){
                sendReceive.run=false;
                sendReceive.decider=true;
                try {
                    sendReceive.outputStream.close();
                    sendReceive.inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        });
    }

    @SuppressLint("MissingPermission")
    private void setBluetooth() {
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = manager.getAdapter();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled())   bluetoothAdapter.enable();
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        if (adapter != null && adapter.getItemCount() > 0) userMessagesList.scrollToPosition(adapter.getItemCount() - 1);
        if (adapter != null && adapter.getItemCount() > 0) userMessagesList.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(messagesList);
        editor.putString(device_address,jsonString);
        editor.apply();
    }

    private void alert(){
        builder.setTitle("Exit")
                .setMessage("Leaving this page will disconnect the device. Confirm exit? ")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sendReceive.run=false;
                    sendReceive.decider=false;

                    try {
                        serverClass.serverSocket.close();
                        sendReceive.outputStream.close();
                        sendReceive.inputStream.close();
                        serverClass1.serverSocket.close();
                        clientClass.socket.close();
                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                    }
                    Intent i1;
                    if(decider.equals("main")) i1 =new Intent(BluetoothChatting.this,MainActivity.class);
                    else i1 =new Intent(BluetoothChatting.this,BluetoothSearch.class);
                    startActivity(i1);

                })
                .setNegativeButton("No", (dialog, which) -> {

                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void setNotify(NotificationChannel ch) {
        notificationManager= getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(ch);
    }

    private class ServerClass extends Thread{
        private  BluetoothServerSocket serverSocket;
        private Boolean run;
        @SuppressLint("MissingPermission")
        public ServerClass() {

            run=true;
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }



        public void run()
        {
            BluetoothSocket socket=null;
            while (run)
            {

                try {
                    Message message=Message.obtain();
                    message.what=STATE_WAITING;
                    handler.sendMessage(message);
                    socket=serverSocket.accept();
                    if(!socket.getRemoteDevice().getAddress().equals(device_address)) socket=null;

                } catch (IOException |NullPointerException e) {
                    e.printStackTrace();

                }
                if(socket!=null)
                {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    run=false;
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);
                    sendReceive=new SendReceive(socket);
                    sendReceive.write((secretMessage+color).getBytes());
                    sendReceive.start();
                    break;
                }

            }
        }


    }

    private class ClientClass extends Thread {
        private BluetoothSocket socket;


        @SuppressLint("MissingPermission")
        public ClientClass (BluetoothDevice device1)
        {

            Message message = Message.obtain();
            message.what = STATE_CONNECTING;
            handler.sendMessage(message);
            try {
                socket= device1.createRfcommSocketToServiceRecord(MY_UUID);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @SuppressLint("MissingPermission")
        public void run()
        {
            try {
                if(!socket.getRemoteDevice().getAddress().equals(device_address)) socket=null;
                if (socket != null) {
                    socket.connect();
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                }

            } catch (IOException | NullPointerException e) {
                e.printStackTrace();

                try{serverClass.start();}catch (IllegalThreadStateException ee){serverClass1=new ServerClass(); serverClass1.start();}

            }
        }
    }

    private class SendReceive extends Thread {
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private Boolean run;
        private Boolean decider;
        public SendReceive (BluetoothSocket socket)
        {
            decider=true;
            run=true;

            InputStream tempIn = null;
            OutputStream tempOut=null;
            try {
                tempIn= socket.getInputStream();
                tempOut= socket.getOutputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
            outputStream=tempOut;
        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;


            while (run)
            {
                try {

                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    run=false;
                    try {
                        outputStream.close();
                        inputStream.close();
                    } catch (IOException ee) {
                        ee.printStackTrace();
                    }
                    if(decider) {
                           try{serverClass.start();}catch (IllegalThreadStateException ee){serverClass1=new ServerClass();
                           serverClass1.start();}
                    }

                    break;
                }


            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(status.getText().toString().equals("Connected")){
            alert();
        }
        else{
            try {
                serverClass.serverSocket.close();
                serverClass1.serverSocket.close();
                clientClass.socket.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
            serverClass.run=false;
            try{serverClass1.run=false;} catch (NullPointerException ignored){}
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScreenOff=true;


    }



    @Override
    protected void onResume() {
        super.onResume();

        ScreenOff=false;
        storeMsg=new String[5];
        FiveNotify=1;
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }


}


