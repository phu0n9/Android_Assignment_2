package com.example.assignment2.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment2.R;
import com.example.assignment2.model.CleaningSite;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddSite extends AppCompatActivity {
    protected TextView latitude;
    protected TextView longitude;
    protected EditText siteName;
    protected EditText address;
    protected TextView siteOwner;
    private CleaningSite cleaningSite;
    protected FirebaseUser userRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_site);
        latitude = findViewById(R.id.resLat);
        longitude = findViewById(R.id.resLong);
        siteName = findViewById(R.id.siteName);
        address = findViewById(R.id.address);
        siteOwner = findViewById(R.id.siteOwner);
        userRecord = FirebaseAuth.getInstance().getCurrentUser();

        cleaningSite = new CleaningSite();
        Intent intent = getIntent();
        cleaningSite.setLat(intent.getDoubleExtra("latitude",0));
        cleaningSite.setLon(intent.getDoubleExtra("longitude",0));
        cleaningSite.setOwner(userRecord.getEmail());

        latitude.setText(Double.toString(cleaningSite.getLat()));
        longitude.setText(Double.toString(cleaningSite.getLon()));
        siteOwner.setText(userRecord.getEmail());
    }

    public void addLatLong(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> latlong = new HashMap<>();
        Map<String,String> ownerEmail = new HashMap<>();
        ownerEmail.put(userRecord.getEmail(),userRecord.getUid());
        latlong.put("latitude",Double.toString(cleaningSite.getLat()));
        latlong.put("longitude",Double.toString(cleaningSite.getLon()));
        latlong.put("owner",ownerEmail);
        latlong.put("participants","no one");

        if(address.getText().toString().matches("") && siteName.getText().toString().matches("")){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddSite.this);
            alertDialog.setTitle("Alert").setMessage("Please enter address and site name")
                    .setNegativeButton("Got it", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
        }
        else {
            latlong.put("name",siteName.getText().toString());
            latlong.put("address",address.getText().toString());

            db.collection("site")
                    .add(latlong)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String msg = "DocumentSnapshot added with ID: " + documentReference.getId();
                            Toast.makeText(AddSite.this, msg, Toast.LENGTH_SHORT).show();
                            Log.d("MainActivity", "DocumentSnapshot added with ID:" + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String error = "Error adding document " + e.toString();
                            Toast.makeText(AddSite.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void onConfirmAddSite(View view) {
        addLatLong();
        cleaningSite = new CleaningSite(siteName.getText().toString(),address.getText().toString());
        onPostSite();
    }

    protected void onPostSite() {
        Toast.makeText(AddSite.this,"we have posted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AddSite.this, MapsActivity.class);
        setResult(101, intent);
        finish();
    }
}