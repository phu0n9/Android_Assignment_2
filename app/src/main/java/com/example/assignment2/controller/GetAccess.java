package com.example.assignment2.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GetAccess extends AppCompatActivity {
    protected Button getAccessBtn;
    protected TextView access_site_name;
    protected TextView access_address;
    protected TextView access_participants;
    protected TextView access_owner;
    protected TextView access_latitude;
    protected TextView access_longitude;
    protected TableRow access_participants_row;
    protected TableRow access_owner_row;

    protected FirebaseFirestore db;
    protected String siteId;
    protected CheckBox checkBox;
    protected FirebaseUser mUser;
    protected Map<String,String> superUser = new HashMap<>();
    protected HashMap<String,String> ownerEmail = new HashMap<>();
    protected HashMap<String,String> userEmail = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_access);
        onAccess();
    }


    private void onAccess(){
        getAccessBtn = findViewById(R.id.access_btn);
        access_site_name = findViewById(R.id.access_site_name);
        access_address = findViewById(R.id.access_address);
        access_participants = findViewById(R.id.access_participants);
        access_owner = findViewById(R.id.access_owner);
        access_latitude = findViewById(R.id.access_latitude);
        access_longitude = findViewById(R.id.access_longitude);
        checkBox = findViewById(R.id.checkbox_access);
        access_participants_row = findViewById(R.id.row_participants_access);
        access_owner_row = findViewById(R.id.owner_access_row);

        Intent intent = getIntent();

        siteId = intent.getStringExtra("markerId");
        db = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("site").document(siteId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    access_site_name.setText(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("name")).toString());
                    access_address.setText(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("address")).toString());
                    access_latitude.setText(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("latitude")).toString());
                    access_longitude.setText(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("longitude")).toString());
                    access_participants_row.setVisibility(View.GONE);
                    access_owner_row.setVisibility(View.GONE);
                    if(document.getData().get("owner") instanceof HashMap){
                        ownerEmail = (HashMap<String, String>) document.getData().get("owner");
                        String string = String.valueOf(ownerEmail.keySet()).replaceAll("\\[", "").replaceAll("\\]","");
                        access_owner.setText(string);
                    }

                    if (document.getData().get("participants").getClass().getSimpleName().equals("String")) {
                        access_participants.setText("no one");
                    } else {
                        userEmail = (HashMap<String, String>) document.getData().get("participants");
                        List<String> str = new ArrayList<>(userEmail.keySet());
                        String string = String.valueOf(str).replaceAll("\\[", "").replaceAll("\\]","");
                        access_participants.setText(string);
                    }
                    isSuperUser();
                }
                else{
                    Log.d("hello", "Cached get failed: ", task.getException());
                }
            }
        });
    }

    private void isSuperUser(){
        db.collection("superUser").whereEqualTo("userId",mUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        if(document.getData().get("userId").equals(mUser.getUid())){
                            access_participants_row.setVisibility(View.VISIBLE);
                            access_owner_row.setVisibility(View.VISIBLE);
                            checkBox.setVisibility(View.GONE);
                            getAccessBtn.setVisibility(View.GONE);
                            Log.d("hello","you should see this as a super user "+document.getData());
                        }
                    }
                }
                else {
                    Log.d("hello","Error getting documents: "+task.getException());
                }
            }
        });

        db.collection("superUser").whereNotEqualTo("userId",mUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        if(!document.getData().get("userId").equals(mUser.getUid())){
                            Log.d("hello","you should see this as a regular user "+document.getData());
                            notSuperUser();
                        }
                    }
                }
                else {
                    Log.d("hello","Error getting documents: "+task.getException());
                }
            }
        });

    }

    private void notSuperUser(){
        getAccessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(GetAccess.this);
                    alertDialog.setTitle("Confirm message").setMessage("Are you sure you want to get access of this?")
                            .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    superUser.put("userId",mUser.getUid());
                                    db.collection("superUser").add(superUser).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Intent intent = new Intent(GetAccess.this, MapsActivity.class);
                                            Toast.makeText(GetAccess.this, "You are now a super user.",Toast.LENGTH_SHORT).show();
                                            startActivity(intent);
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    String error = "Error adding document " + e.toString();
                                                    Log.d("hello",error);
                                                }
                                            });
                                }
                            }).create().show();
                }
                else{
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(GetAccess.this);
                    alertDialog.setTitle("Checkbox not check").setMessage("Please check the rules and term check box.")
                            .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
                }
            }
        });
    }


}