package com.example.assignment2.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    protected TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        name = findViewById(R.id.testing);

        Map<String,Object> cleaning_site = new HashMap<>();
        cleaning_site.put("first","hello");
        cleaning_site.put("last","lovelace");
        cleaning_site.put("born",1815);

        db.collection("site")
                .add(cleaning_site)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String msg = "DocumentSnapshot added with ID: "+documentReference.getId();
                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity","DocumentSnapshot added with ID:"+documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = "Error adding document "+e.toString();
                        Toast.makeText(MainActivity.this,error,Toast.LENGTH_SHORT).show();
                    }
                });

        db.collection("site")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                if(document.getData().get("first").equals("hello"))
                                    Log.d("hello", document.getId() + " => " +document.getData().get("first"));
                            }
                        } else {
                            Log.d("hello", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}