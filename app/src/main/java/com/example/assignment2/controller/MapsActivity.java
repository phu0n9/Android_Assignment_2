package com.example.assignment2.controller;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment2.R;
import com.example.assignment2.model.CleaningSite;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.nio.file.Paths.get;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int MY_LOCATION_REQUEST = 99;
    protected FusedLocationProviderClient locationClient;
    protected LocationRequest locationRequest;
    protected TableLayout table;
    protected TextView siteName;
    protected TextView address;
    protected TextView participants;
    protected Map<Marker,QueryDocumentSnapshot> map = new HashMap<>();
    protected Button joinBtn;
    protected Button getParticipantListBtn;
    protected TableRow tableRow;
    protected FirebaseUser userRecord;
    protected FirebaseFirestore db;
    protected int checkParticipant;
    protected HashMap<String,String> hashMap = new HashMap<>();
    protected Map<String,String> userEmail = new HashMap<>();
    protected Map<String,String> ownerEmail = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        table = findViewById(R.id.table);
        siteName = findViewById(R.id.site_name);
        address = findViewById(R.id.site_address);
        participants = findViewById(R.id.participants);
        tableRow = findViewById(R.id.participants_table_row);
        joinBtn = findViewById(R.id.participate_btn);
        getParticipantListBtn = findViewById(R.id.participants_list);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng rmit = new LatLng(10.729567,106.6908816);
//        mMap.addMarker(new MarkerOptions().position(rmit).title("Marker in RMIT"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(rmit));

        requestPermission();
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        onMapClick();
        startLocationUpdate();
        onMarkClick();
        onCameraMove();
    }

    private void onMapClick(){
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(MapsActivity.this, AddSite.class);
                intent.putExtra("latitude", latLng.latitude);
                intent.putExtra("longitude", latLng.longitude);
                startActivity(intent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint({"MissingPermission", "RestrictedApi"})
    private void startLocationUpdate() {
        onConfirmAddSite();
        locationRequest = new LocationRequest();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(30*1000); //30s
//        locationRequest.setFastestInterval(15*1000); //15s
        locationClient.requestLocationUpdates(locationRequest,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                //zoom in the map
                 mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker()));
                Toast.makeText(MapsActivity.this,"(" + location.getLatitude() + ","+location.getLongitude() +")",Toast.LENGTH_SHORT).show();
            }
        },null);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        onConfirmAddSite();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_LOCATION_REQUEST);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vector) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vector);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onConfirmAddSite() {
        db = FirebaseFirestore.getInstance();

        db.collection("site")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: task.getResult()){
                                LatLng position = new LatLng(Double.parseDouble(Objects.requireNonNull(document.getData().get("latitude")).toString()),Double.parseDouble(Objects.requireNonNull(document.getData().get("longitude")).toString()));
                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(position)
                                        .icon(bitmapDescriptorFromVector(MapsActivity.this,R.drawable.ic_baseline_battery_charging_full_24)));
                                marker.setTag(document.getId());
                                map.put(marker,document);
                            }
//                             taskLength = task.getResult().size();
                        } else {
                            Log.d("hello", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void onMarkClick(){
        userEmail.clear();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onMarkerClick(Marker marker) {
                try{
                    table.setVisibility(View.VISIBLE);
                    Log.d("hello",marker.getId()+" "+ Objects.requireNonNull(map.get(marker)).getData().get("name"));
                    siteName.setText((String) Objects.requireNonNull(map.get(marker)).getData().get("name"));
                    address.setText((String) Objects.requireNonNull(map.get(marker)).getData().get("address"));
                    setGetParticipantListBtn(marker);
                    if(Objects.requireNonNull(Objects.requireNonNull(map.get(marker)).getData().get("participants")).getClass().getSimpleName().equals("String")){
                        checkParticipant = 1;
                        setParticipantsOnClick(map.get(marker));
                    }
                    else{
                        checkParticipant = 2;
                        userEmail = (HashMap<String, String>) map.get(marker).getData().get("participants");
                        setParticipantsOnClick(map.get(marker));
                        getParticipantsAfterUpdating();
                        Log.d("hello","line "+userEmail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        map.clear();
    }

    public void onCameraMove(){
       mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
           @Override
           public void onCameraMoveStarted(int i) {
               if(i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE){
                   table.setVisibility(View.GONE);
               }
           }
       });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setParticipantsOnClick(QueryDocumentSnapshot documentSnapshot){
        userRecord = FirebaseAuth.getInstance().getCurrentUser();

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkParticipant == 1){
                    addOneParticipants(userRecord,documentSnapshot);
                    table.setVisibility(View.GONE);
                }
                else if(checkParticipant == 2){
                    addParticipants(userRecord,documentSnapshot);
                    table.setVisibility(View.GONE);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addParticipants(FirebaseUser user, DocumentSnapshot documentSnapshot){
        try{
            if(Objects.equals(userEmail.get(user.getEmail()), user.getUid())){
                Log.d("hello","this is "+user.getEmail());
                Log.d("hello","userEmail "+userEmail.get(user.getEmail()));
                Log.d("hello","userRecord "+user.getUid());
                Toast.makeText(this,"You have already joined this site",Toast.LENGTH_SHORT).show();
            }
            else if(Objects.equals(ownerEmail.get(user.getEmail()), user.getUid())){
                Toast.makeText(this,"You are owner of this site.",Toast.LENGTH_SHORT).show();
            }
            else{
                Log.d("hello","now this is "+user.getEmail());
                Log.d("hello","userEmail1 "+userEmail.get(user.getEmail()));
                Log.d("hello","userRecord1 "+user.getUid());
                userEmail.put(user.getEmail(),user.getUid());
                Toast.makeText(MapsActivity.this,"Thank you for joining us",Toast.LENGTH_SHORT).show();
                db.collection("site").document(documentSnapshot.getId()).update("participants",userEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("hello","Updated successfully1");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("hello","Error updating document",e);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        userEmail.clear();
        ownerEmail.clear();
    }

    public void addOneParticipants(FirebaseUser user, DocumentSnapshot documentSnapshot){
        HashMap<String,String> map = new HashMap<>();
        map.put(user.getEmail(),user.getUid());
        try {
            db.collection("site").document(documentSnapshot.getId()).update("participants",map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("hello","Updated successfully");
                            participants.setText(user.getEmail());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("hello","Error updating document",e);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getParticipantsAfterUpdating() {
        db = FirebaseFirestore.getInstance();
        db.collection("site")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                for (QueryDocumentSnapshot document: task.getResult()){
                                    if(document.getData().get("participants") instanceof HashMap){
                                        hashMap = (HashMap<String, String>) document.getData().get("participants");
                                    }
                                }
                                assert hashMap != null;
                                List<String> str = new ArrayList<>(hashMap.keySet());
                                String string = String.valueOf(str).replaceAll("[{}\\\\[\\\\]]","");
                                Log.d("hello", string);
                                participants.setText(string);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d("hello", "Error getting documents.", task.getException());
                        }
                    }
                });
        hashMap.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setGetParticipantListBtn(Marker marker){
        userRecord = FirebaseAuth.getInstance().getCurrentUser();
        if(map.get(marker).getData().get("owner") instanceof HashMap){
            ownerEmail =  (HashMap<String, String>) map.get(marker).getData().get("owner");
        }
        getParticipantListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParticipantList(userRecord);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getParticipantList(FirebaseUser user){
        if(Objects.equals(ownerEmail.get(user.getEmail()), user.getUid())) {
            tableRow.setVisibility(View.VISIBLE);
        }
        else{
            Toast.makeText(this,"You don't have the authorization to do this.",Toast.LENGTH_SHORT).show();
        }
    }

//TODO: filter/search by name,address, and current participants
    //TODO: There should be notifications to users when there is changes in a site


}