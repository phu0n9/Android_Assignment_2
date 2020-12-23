package com.example.assignment2.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRegister extends AppCompatActivity {
    protected EditText fullName ;
    protected EditText email ;
    protected EditText mobile ;
    protected EditText address;
    protected EditText password ;
    protected FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email_address);
        password = findViewById(R.id.password);
        mobile = findViewById(R.id.mobile_number);
        address = findViewById(R.id.address);
        fullName =findViewById(R.id.full_name);

        Button signUp = findViewById(R.id.sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    public void register(){
        if(email.getText().toString().matches("") && password.getText().toString().matches("")){
            Toast.makeText(this,"You should enter an username",Toast.LENGTH_SHORT).show();
        }
        else{
            mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.d("hello","create email:success");
                                Toast.makeText(UserRegister.this,"create email success",Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(UserRegister.this,MapsActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Log.d("hello","signInWithEmail:failure",task.getException());
                                Toast.makeText(UserRegister.this,"create email failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }
}