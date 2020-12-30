package com.example.assignment2.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.assignment2.R;

public class WelcomeActivity extends AppCompatActivity {

    protected ImageView imageView;
    protected AnimationDrawable drawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button welcomeBtn = findViewById(R.id.welcome_btn);
        welcomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, UserLogin.class);
                startActivity(intent);
            }
        });

        imageView = findViewById(R.id.image_cover);
        imageView.setBackgroundResource(R.drawable.photo_drawable);
        drawable = (AnimationDrawable) imageView.getBackground();
        drawable.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawable.stop();
                imageView.clearAnimation();
            }
        },drawable.getDuration(5));
    }
}