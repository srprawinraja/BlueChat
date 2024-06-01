package com.example.BOneOnOneChat;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class geek extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] maintitle;
    private final int[] imgid;

    public geek(Activity context, String[] maintitle, int[] imgid) {
        super(context, R.layout.activity_geek, maintitle);
        this.context=context;
        this.maintitle=maintitle;
        this.imgid=imgid;
    }
    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View rowView=inflater.inflate(R.layout.activity_geek, null,true);

        TextView titleText = rowView.findViewById(R.id.search_title);
        ImageView imageView = rowView.findViewById(R.id.icon);


        titleText.setText(maintitle[position]);
        imageView.setImageResource(imgid[position]);

        return rowView;

    }
}