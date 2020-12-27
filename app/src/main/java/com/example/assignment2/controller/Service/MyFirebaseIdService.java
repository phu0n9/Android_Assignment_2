package com.example.assignment2.controller.Service;

import androidx.annotation.NonNull;

import com.example.assignment2.controller.EditData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseIdService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        if(firebaseUser!=null){
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken){
        try{
            FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            Token token1 = new Token(refreshToken);

            Map<String,String> map = new HashMap<>();
            map.put("token",token1.getToken());
            assert firebaseUser != null;
            FirebaseFirestore.getInstance().collection("Tokens").document(firebaseUser.getUid()).set(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
