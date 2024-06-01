
package com.example.BOneOnOneChat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import yuku.ambilwarna.AmbilWarnaDialog;

public class Profile extends AppCompatActivity {
    public  ImageView profileColor,profileImage,profileIcon;
    private  AmbilWarnaDialog ambilWarnaDialog;
    private int defaultColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);




        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);

        String myString = sharedPreferences.getString("name", "");

        Button buttonEdit = findViewById(R.id.button_edit);
        buttonEdit.setText(myString);
        Button color = findViewById(R.id.color);
        defaultColor= ContextCompat.getColor(getApplicationContext(),R.color.profilebackground);
        profileColor=findViewById(R.id.profileColor);
        profileImage=findViewById(R.id.profileImage);
        profileIcon=findViewById(R.id.imageView);
        ImageButton backEdit = findViewById(R.id.back_setting2);

        try {
            int colorProfile = sharedPreferences.getInt("profileColor", 0);


            Drawable drawable = ContextCompat.getDrawable(Profile.this, R.drawable.ic_baseline_account_circle_24);
            if (drawable != null) {
                drawable = drawable.mutate();
                drawable.setTint(colorProfile);
            }


            profileIcon.setImageDrawable(drawable);
            profileImage.setImageDrawable(drawable);
            profileColor.setBackgroundColor(colorProfile);
            //back

        }
        catch (Resources.NotFoundException e) {
            // Handle the exception if the color resource is not found

        }
        backEdit.setOnClickListener(v -> {
            Intent i=new Intent(getApplicationContext(),Setting.class);
            startActivity(i);
        });
        color.setOnClickListener(v -> {
            ambilWarnaDialog = new AmbilWarnaDialog(Profile.this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {

                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color1) {
                    try {

                        Drawable drawable = ContextCompat.getDrawable(Profile.this, R.drawable.ic_baseline_account_circle_24);
                        if (drawable != null) {
                            drawable = drawable.mutate();
                            drawable.setTint(color1);
                        }

                        profileColor.setBackgroundColor(color1);

                        profileIcon.setImageDrawable(drawable);

                        profileImage.setImageDrawable(drawable);

// If you are using an ImageView to display the drawable
                        SharedPreferences sharedPreferences1 = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                        editor1.putInt("profileColor", color1);
                        editor1.apply();
                    }
                    catch (Resources.NotFoundException e) {
                        // Handle the exception if the color resource is not found

                    }
                }
            });
            ambilWarnaDialog.show();


        });


        buttonEdit.setOnClickListener(v -> {
            Intent i= new Intent(getApplicationContext(),editName.class);
            startActivity(i);

        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(this,Setting.class);
        startActivity(i);
    }
}

