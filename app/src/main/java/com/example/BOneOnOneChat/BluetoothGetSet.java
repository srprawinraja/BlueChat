package com.example.BOneOnOneChat;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class BluetoothGetSet implements Parcelable {

    private String from;
    private String message;
    private String time;
    public BluetoothGetSet(){

    }


    protected BluetoothGetSet(Parcel in) {
        from = in.readString();
        message = in.readString();
        time=in.readString();
    }

    public static final Creator<BluetoothGetSet> CREATOR = new Creator<BluetoothGetSet>() {
        @Override
        public BluetoothGetSet createFromParcel(Parcel in) {
            return new BluetoothGetSet(in);
        }

        @Override
        public BluetoothGetSet[] newArray(int size) {
            return new BluetoothGetSet[size];
        }
    };

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time=time;
    }
    public String getTime(){return time;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(from);
        dest.writeString(message);
        dest.writeString(time);
    }
}
