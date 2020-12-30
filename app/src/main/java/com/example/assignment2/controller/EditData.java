package com.example.assignment2.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment2.R;
import com.example.assignment2.controller.Service.APIService;
import com.example.assignment2.controller.Service.Client;
import com.example.assignment2.controller.Service.Data;
import com.example.assignment2.controller.Service.MyResponse;
import com.example.assignment2.controller.Service.NotificationSender;
import com.example.assignment2.controller.Service.Token;
import com.example.assignment2.model.CleaningSite;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditData extends AppCompatActivity {
    protected EditText siteName;
    protected EditText siteAddress;
    protected TextView participants;
    protected TextView latitude;
    protected TextView longitude;
    protected TextView ownerEmail;
    protected Button confirmBtn;
    protected CleaningSite site;
    protected String siteId;

    protected FirebaseFirestore db;
    protected int checkChanges = 0;
    protected  List<String> userId = new ArrayList<>();
    protected HashMap<String,String> participantsList = new HashMap<>();
    protected List<String> beforeUpdate = new ArrayList<>();
    protected String message = null;
    protected String title = "Site Owner update";
    protected  APIService apiService;
    public static final String CHANNEL_ID = "default_channel_id";
    protected Toolbar toolbar;
    protected Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);
        onEditing();
        setToolbar();
        setToolbarBackBtn();
    }


    private void setToolbar(){
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.getMenu().clear();
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        }
    }

    private void setToolbarBackBtn(){
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditData.this,MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onEditing(){
        beforeUpdate.clear();
        userId.clear();
        siteName = findViewById(R.id.edit_site_name);
        siteAddress = findViewById(R.id.edit_address_name);
        participants = findViewById(R.id.edit_participants);
        latitude = findViewById(R.id.edit_latitude);
        longitude = findViewById(R.id.edit_longitude);
        ownerEmail = findViewById(R.id.edit_site_owner);
        confirmBtn = findViewById(R.id.edit_confirm_btn);
        site = new CleaningSite();

        Intent intent = getIntent();
        siteId = intent.getStringExtra("id");
        db = FirebaseFirestore.getInstance();

        db.collection("site").document(siteId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    siteName.setText(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("name")).toString());
                    siteAddress.setText(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("address")).toString());
                    latitude.setText(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("latitude")).toString());
                    longitude.setText(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("longitude")).toString());
                    if(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("owner")) instanceof  HashMap){
                        HashMap<String,String> hashMap = (HashMap<String, String>) Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("owner"));
                        String str = hashMap.keySet().toString();
                        ownerEmail.setText(str);
                    }
                    beforeUpdate.add(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("name")).toString());
                    beforeUpdate.add(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("address")).toString());

                    if(document.getData().get("participants") instanceof HashMap){
                        participantsList = (HashMap<String, String>) document.getData().get("participants");
                        assert participantsList != null;
                        String str = (participantsList.keySet().toString()).replaceAll("\\[", "").replaceAll("\\]","");
                        Log.d("hello", str);
                        participants.setText(str);
                        userId.addAll(participantsList.values());
                        Log.d("hello",userId.toString());
                    }
                    else{
                        participants.setText(Objects.requireNonNull(document.getData().get("participants")).toString());
                    }

                    Log.d("hello", "Cached document data: " + document.getData());
                }
                else{
                    Log.d("hello", "Cached get failed: ", task.getException());
                }
            }
        });
        onEditBtnClick();
    }

    private void onEditBtnClick(){
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("site").document(siteId).update("name",siteName.getText().toString(),"address",siteAddress.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("hello","Updated successfully1");
                                Intent intent = new Intent(EditData.this,MapsActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("hello","Error updating document",e);
                            }
                        });
                listenForUpdate();
                addUserToke();
            }
        });
    }

    private void listenForUpdate(){
        final DocumentReference documentReference = db.collection("site").document(siteId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.d("hello",error.toString());
                    return;
                }

                try{
                    if(value != null && value.exists()){
                        Log.d("hello","Current data: "+value.getData());
                        Toast.makeText(EditData.this,"Listen Data",Toast.LENGTH_SHORT).show();
                        if(!Objects.equals(Objects.requireNonNull(value.getData()).get("name"), beforeUpdate.get(0))){
                            beforeUpdate.set(0,(String) Objects.requireNonNull(value.getData()).get("name"));
                            checkChanges = 1;
                            Log.d("hello","Notice this line");
                        }
                        else if(!Objects.equals(value.getData().get("address"), beforeUpdate.get(1))){
                            Log.d("hello","Notice this line1");
                            beforeUpdate.set(1,(String) Objects.requireNonNull(value.getData()).get("address"));
                            checkChanges = 2;
                        }
                        else if((!Objects.equals(value.getData().get("name"), beforeUpdate.get(0))) && (!Objects.equals(value.getData().get("address"), beforeUpdate.get(1)))){
                            Log.d("hello","Notice this line2");
                            beforeUpdate.set(0,(String) Objects.requireNonNull(value.getData()).get("name"));
                            beforeUpdate.set(1,(String) Objects.requireNonNull(value.getData()).get("address"));
                            checkChanges = 3;
                        }
                    }
                    else{
                        Log.d("hello","Current data: null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    private String getCheckChanges(){
        if(checkChanges == 1){
            message = "One of your joined site has updated the name";
        }
        else if(checkChanges == 2){
            message = "One of your joined site has updated the address";
        }
        else {
            message = "One of your joined site has updated the name and the address";
        }
        return message;
    }



    public void addUserToke(){
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        for (String user: userId){
            db.collection("Tokens").document(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("hello", "DocumentSnapshot data: " + document.getData());
                            String userToken = Objects.requireNonNull(document.getData()).get("token").toString();
                            sendNotifications(userToken,title,getCheckChanges());
                        } else {
                            Log.d("hello", "No such document");
                        }
                    } else {
                        Log.d("hello", "get failed with ", task.getException());
                    }
                }
            });
        }
        UpdateToken();
    }

    private void UpdateToken(){
        try{
            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Token token = new Token(refreshedToken);
            Map<String,String> map = new HashMap<>();
            map.put("token",token.getToken());
            assert mUser != null;
            Log.d("hello","get userID "+mUser.getUid());
            db.collection("Tokens").document(mUser.getUid()).set(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendNotifications(String userToken, String title, String message) {
        Data data = new Data(title, message);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, title, importance);
            channel.setDescription(message);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationSender sender = new NotificationSender(data, userToken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Log.d("hello","we have failed");
                    }
                    else{
                        Log.d("hello","we have sent successfully");
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                Toast.makeText(EditData.this, "Failed ", Toast.LENGTH_LONG).show();
            }
        });
    }

}