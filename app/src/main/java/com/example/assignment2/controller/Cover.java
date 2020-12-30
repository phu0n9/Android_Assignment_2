package com.example.assignment2.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.example.assignment2.R;

public class Cover extends AppCompatActivity {

    protected AnimationDrawable drawable;
    protected ImageView imageView;
    private final static int SPLASH_TIME_OUT = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);
        imageView = findViewById(R.id.animation);
        imageView.setBackgroundResource(R.drawable.animation_drawable);
        drawable = (AnimationDrawable) imageView.getBackground();
        drawable.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Cover.this,WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }

}