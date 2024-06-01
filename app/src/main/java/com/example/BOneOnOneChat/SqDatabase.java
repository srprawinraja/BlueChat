package com.example.BOneOnOneChat;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;


public class SqDatabase extends SQLiteOpenHelper {
    private static final String COLUMN_ID = "ID";
    private static final String TABLE_NAME = "USER_TABLE" ;
    private static final String USER_NAME = "USER_NAME";
    private static final String USER_ADDRESS = "USER_ADDRESS";
    private static final String PROFILE_BACKGROUND_COLOR = "PROFILE_BACKGROUND_COLOR";
    private final ArrayList<String[]> userDetail=new ArrayList<>();
    public SqDatabase(@Nullable Context context) {
        super(context,"dataBase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_NAME + " TEXT, " +
                USER_ADDRESS + " TEXT, " +
                PROFILE_BACKGROUND_COLOR + " TEXT)";

        db.execSQL(createTableQuery);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @SuppressLint("Range")
    public void add(String name, String address, String color){
        SQLiteDatabase dbR = this.getReadableDatabase();
        SQLiteDatabase db=this.getWritableDatabase();
        boolean chek=false;

        Cursor cursor
                = dbR.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while (cursor.moveToNext()) {

            if(address.equals(cursor.getString(cursor.getColumnIndex(USER_ADDRESS)))){
                chek=true;
                if(color.equals("no")) color=cursor.getString(cursor.getColumnIndex(PROFILE_BACKGROUND_COLOR));
                if(!name.equals(cursor.getString(cursor.getColumnIndex(USER_NAME))) || !color.equals(cursor.getString(cursor.getColumnIndex(PROFILE_BACKGROUND_COLOR)))){
                    String whereClause = "id=?";
                    String[] whereArgs = {String.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_ID)))};
                    ContentValues cv=new ContentValues();
                    cv.put(USER_NAME,name);
                    cv.put(USER_ADDRESS,address);
                    cv.put(PROFILE_BACKGROUND_COLOR,color);
                    db.update(TABLE_NAME,cv,whereClause,whereArgs);
                }

            }
        }
        if(!chek) {
            if(color.equals("no")) color= "xyz";
            ContentValues cv = new ContentValues();

            cv.put(USER_NAME, name);
            cv.put(USER_ADDRESS, address);
            cv.put(PROFILE_BACKGROUND_COLOR, color);

            try {
                db.insert(TABLE_NAME, null, cv);

            } catch (SQLiteException e) {
                // Handle the exception, and log or display the error message for debugging
                Log.e("SQLite Error", Objects.requireNonNull(e.getMessage()));
            }

        }
        cursor.close();
        db.close();
    }

    public ArrayList<String[]> retrieve(){
        SQLiteDatabase db = this.getReadableDatabase();
        userDetail.clear();
        Cursor cursor
                = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while (cursor.moveToNext()) {

            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(USER_NAME));
            @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(USER_ADDRESS));
            @SuppressLint("Range") String backgroundColor = cursor.getString(cursor.getColumnIndex(PROFILE_BACKGROUND_COLOR));
            userDetail.add(new String[]{name,address,backgroundColor});
        }
        cursor.close();
        db.close();
        return userDetail;

    }


}