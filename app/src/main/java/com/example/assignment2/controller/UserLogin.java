package com.example.assignment2.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.assignment2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserLogin extends AppCompatActivity {
    private final String TAG = UserLogin.class.getName();

    protected FirebaseAuth mAuth;

    protected EditText email;
    protected EditText password;
    protected String token = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email_address);
        password = findViewById(R.id.password);

        Button signUp = findViewById(R.id.sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLogin.this, UserRegister.class);
                startActivity(intent);
            }
        });

        Button logIn = findViewById(R.id.log_in);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
                addUserToken();
            }
        });
    }


    public void login(){
        if(email.getText().toString().matches("") || password.getText().toString().matches("")){
            Toast.makeText(this,"You havent sign in with email",Toast.LENGTH_SHORT).show();
        }
        else{
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.d("hello","signInWithEmail:success");
                                Toast.makeText(UserLogin.this,"Authentication success",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(UserLogin.this,MapsActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Log.d("hello","signInWithEmail:failure",task.getException());
                                Toast.makeText(UserLogin.this,"Authentication failed",Toast.LENGTH_LONG).show();
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserLogin.this);
                                alertDialog.setTitle("Email or password invalid").setMessage(Objects.requireNonNull(task.getException()).getMessage())
                                        .setNegativeButton("Got it", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
                            }
                        }
                    });
        }
    }

    private void addUserToken(){
        try{
            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            assert mUser != null;
            mUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                token = task.getResult().getToken();
                                Map<String,String> userToken = new HashMap<>();
                                userToken.put("token",token);
                                FirebaseFirestore.getInstance().collection("Tokens").document(mUser.getUid()).set(userToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("hello", "DocumentSnapshot successfully written!");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("hello", "Error writing document", e);
                                    }
                                });
                            } else {
                                Log.d("hello",task.getException().toString());
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}