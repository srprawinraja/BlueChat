package com.example.BOneOnOneChat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BlueAdapter extends RecyclerView.Adapter<BlueAdapter.MessageViewHolder>{
    private final List<BluetoothGetSet> userMessagesList;

    public BlueAdapter (List<BluetoothGetSet> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }



    public static class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText,senderTime,receiverTime;



        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.bluetooth_sender_messsage_text);
            receiverMessageText = itemView.findViewById(R.id.bluetooth_receiver_message_text);
            senderTime = itemView.findViewById(R.id.senderTime);
            receiverTime = itemView.findViewById(R.id.receiverTime);
        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetoothchatxml,parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        BluetoothGetSet bluetoothGetSet = userMessagesList.get(position);
        String from=bluetoothGetSet.getFrom();

        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverTime.setVisibility(View.GONE);
        if(from.equals("sender")){
            holder.senderMessageText.setVisibility(View.VISIBLE);
            holder.senderTime.setVisibility(View.VISIBLE);
            holder.senderMessageText.setText(bluetoothGetSet.getMessage());
            holder.senderTime.setText(bluetoothGetSet.getTime());
        }
        else{
            holder.senderMessageText.setVisibility(View.GONE);
            holder.senderTime.setVisibility(View.GONE);
            holder.receiverTime.setVisibility(View.VISIBLE);
            holder.receiverMessageText.setVisibility(View.VISIBLE);
            holder.receiverMessageText.setText(bluetoothGetSet.getMessage());
            holder.receiverTime.setText(bluetoothGetSet.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


}
