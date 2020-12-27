package com.example.assignment2.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment2.R;
import com.example.assignment2.controller.RouteService.FetchURL;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;
    private static final int MY_LOCATION_REQUEST = 99;
    protected FusedLocationProviderClient locationClient;
    protected LocationRequest locationRequest;
    protected TableLayout table;
    protected TextView siteName;
    protected TextView address;
    protected TextView participants;
    protected Button joinBtn;
    protected TableRow tableRow;
    protected SearchView searchView;
    protected Button editBtn;
    protected Button routeBtn;


    protected Map<Marker, QueryDocumentSnapshot> map = new HashMap<>();
    protected FirebaseUser userRecord;
    protected FirebaseFirestore db;
    protected int checkParticipant;
    protected HashMap<String, String> hashMap = new HashMap<>();
    protected Map<String, String> userEmail = new HashMap<>();
    protected Map<String, String> ownerEmail = new HashMap<>();
    protected String id;
    protected Polyline currentPoly;
    protected double currentLatitude,currentLongitude;
    protected MarkerOptions place1,place2;
    private final Handler handler = new Handler();


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
        searchView = findViewById(R.id.map_search);
        editBtn = findViewById(R.id.edit_btn);
        routeBtn = findViewById(R.id.route_btn);
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

        requestPermission();
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        onMapClick();
        startLocationUpdate();
        onMarkClick();
        onCameraMove();
        onMapSearch();
    }

    protected void onMapSearch() {
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MapsActivity.this,FilteringData.class);
//                startActivity(intent);
            }
        });
    }

    private void onMapClick() {
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
        locationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                //zoom in the map
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker()));
                Toast.makeText(MapsActivity.this, "(" + location.getLatitude() + "," + location.getLongitude() + ")", Toast.LENGTH_SHORT).show();
            }
        }, null);
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
    private void onConfirmAddSite() {
        db = FirebaseFirestore.getInstance();

        db.collection("site")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("site").document(document.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (error != null) {
                                            Log.d("hello", error.toString());
                                            return;
                                        }
                                        try {
                                            if (value != null && value.exists()) {
                                                LatLng position = new LatLng(Double.parseDouble(Objects.requireNonNull(document.getData().get("latitude")).toString()), Double.parseDouble(Objects.requireNonNull(document.getData().get("longitude")).toString()));
                                                Marker marker = mMap.addMarker(new MarkerOptions()
                                                        .position(position)
                                                        .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_baseline_battery_charging_full_24)));
                                                map.put(marker, document);
                                            } else {
                                                Log.d("hello", "Current data: null");
                                            }
                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d("hello", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void onMarkClick() {
        userEmail.clear();
        ownerEmail.clear();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onMarkerClick(Marker marker) {
                userRecord = FirebaseAuth.getInstance().getCurrentUser();
                db.collection("site").document(Objects.requireNonNull(map.get(marker)).getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                                Toast.makeText(MapsActivity.this,"Listen Data in maps activity",Toast.LENGTH_SHORT).show();
                                onConfirmAddSite();

                                table.setVisibility(View.VISIBLE);
                                Log.d("hello", marker.getId() + " " + Objects.requireNonNull(map.get(marker)).getData().get("name"));
                                siteName.setText((String) Objects.requireNonNull(map.get(marker)).getData().get("name"));
                                address.setText((String) Objects.requireNonNull(map.get(marker)).getData().get("address"));
                                if (map.get(marker).getData().get("owner") instanceof HashMap) {
                                    ownerEmail = (HashMap<String, String>) map.get(marker).getData().get("owner");
                                    assert ownerEmail != null;
                                    if (Objects.equals(ownerEmail.get(userRecord.getEmail()), userRecord.getUid())) {
                                        tableRow.setVisibility(View.VISIBLE);
                                        editBtn.setVisibility(View.VISIBLE);
                                        id = Objects.requireNonNull(map.get(marker)).getId();
                                        setEditBtn();
                                    } else {
                                        Toast.makeText(MapsActivity.this, "You don't have the authorization to do this.", Toast.LENGTH_SHORT).show();
                                        tableRow.setVisibility(View.GONE);
                                        editBtn.setVisibility(View.GONE);
                                    }
                                }
                                if (Objects.requireNonNull(Objects.requireNonNull(map.get(marker)).getData().get("participants")).getClass().getSimpleName().equals("String")) {
                                    checkParticipant = 1;
                                    participants.setText("no one");
                                    setParticipantsOnClick(map.get(marker));
                                    onConfirmAddSite();
                                } else {
                                    checkParticipant = 2;
                                    userEmail = (HashMap<String, String>) map.get(marker).getData().get("participants");
                                    setParticipantsOnClick(map.get(marker));
                                    getParticipantsAfterUpdating(marker);
                                    onConfirmAddSite();
                                    Log.d("hello", "line " + userEmail);
                                }
                                onRoutingButtonClick(marker);
                            }
                            else{
                                Log.d("hello","Current data: null");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }
        });
        map.clear();
    }

    private void onCameraMove() {
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    table.setVisibility(View.GONE);
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setParticipantsOnClick(QueryDocumentSnapshot documentSnapshot) {
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkParticipant == 1) {
                    addOneParticipants(userRecord, documentSnapshot);
                    table.setVisibility(View.GONE);
                } else if (checkParticipant == 2) {
                    addParticipants(userRecord, documentSnapshot);
                    table.setVisibility(View.GONE);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addParticipants(FirebaseUser user, DocumentSnapshot documentSnapshot) {
        try {
            if (Objects.equals(userEmail.get(user.getEmail()), user.getUid())) {
                Toast.makeText(this, "You have already joined this site", Toast.LENGTH_SHORT).show();
            } else if (Objects.equals(ownerEmail.get(user.getEmail()), user.getUid())) {
                Toast.makeText(this, "You are owner of this site.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("hello", "now this is " + user.getEmail());
                Log.d("hello", "userEmail1 " + userEmail.get(user.getEmail()));
                Log.d("hello", "userRecord1 " + user.getUid());
                userEmail.put(user.getEmail(), user.getUid());
                Toast.makeText(MapsActivity.this, "Thank you for joining us", Toast.LENGTH_SHORT).show();
                db.collection("site").document(documentSnapshot.getId()).update("participants", userEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("hello", "Updated successfully1");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("hello", "Error updating document", e);
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addOneParticipants(FirebaseUser user, DocumentSnapshot documentSnapshot) {
        HashMap<String, String> map = new HashMap<>();
        map.put(user.getEmail(), user.getUid());
        try {
            if (Objects.equals(ownerEmail.get(user.getEmail()), user.getUid())) {
                Toast.makeText(this, "You are owner of this site.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapsActivity.this, "Thank you for joining us", Toast.LENGTH_SHORT).show();
                db.collection("site").document(documentSnapshot.getId()).update("participants", map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("hello", "Updated successfully");
                                participants.setText(user.getEmail());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("hello", "Error updating document", e);
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getParticipantsAfterUpdating(Marker marker) {
        db = FirebaseFirestore.getInstance();
        db.collection("site").document(map.get(marker).getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            try {
                                    if (document.getData().get("participants") instanceof HashMap) {
                                        hashMap = (HashMap<String, String>) document.getData().get("participants");
                                        List<String> str = new ArrayList<>(hashMap.keySet());
                                        String string = String.valueOf(str).replaceAll("\\[", "").replaceAll("\\]","");
                                        Log.d("hello", string);
                                        participants.setText(string);
                                    }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        hashMap.clear();
    }

    private void setEditBtn() {
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, EditData.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }

    private String getUrl(LatLng origin, LatLng destination) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;

        String mode = "mode=" + "driving";

        String parameters = str_origin + "&" + str_dest + "&" + mode;

        String output = "json";

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);

    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPoly != null)
            currentPoly.remove();
        currentPoly = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint({"MissingPermission", "RestrictedApi"})
    private void onRoutingButtonClick(Marker marker) {
        locationRequest = new LocationRequest();
        locationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
            }
        }, null);

        routeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                place1 = new MarkerOptions().position(new LatLng(currentLatitude,currentLongitude));
                place2 = new MarkerOptions().position(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude));
                String url = getUrl(place1.getPosition(),place2.getPosition());
                Log.d("hello", "url='" + url + "'");
                new FetchURL(MapsActivity.this).execute(url);
            }
        });
    }

    //TODO: filtering and UI

}
