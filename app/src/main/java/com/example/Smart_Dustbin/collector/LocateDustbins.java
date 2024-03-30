package com.example.Smart_Dustbin.collector;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Smart_Dustbin.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LocateDustbins extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private Map<String, Marker> markerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_dustbins);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        markerMap = new HashMap<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Retrieve collector document
        mDatabase.child("collectors").child("9373197797").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve dustbin IDs
                for (DataSnapshot ds : dataSnapshot.child("dustbinIds").getChildren()) {
                    String dustbinId = ds.getValue(String.class);
                    // Retrieve dustbin details (latitude, longitude, weight, distance, percentage)
                    mDatabase.child("dustbins").child(dustbinId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                            Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                            Float weight = dataSnapshot.child("currentweight").getValue(Float.class);
                            Float distance = dataSnapshot.child("currentdistance").getValue(Float.class);
                            Float percentage = dataSnapshot.child("currentpercentage").getValue(Float.class);
                            if (latitude != null && longitude != null) {
                                LatLng location = new LatLng(latitude, longitude);
                                // Create marker with title and snippet
                                MarkerOptions markerOptions = new MarkerOptions().position(location).title("Dustbin " + dustbinId);
                                if (weight != null) {
                                    markerOptions.snippet("Weight: " + weight + "kg");
                                }
                                if (distance != null) {
                                    markerOptions.snippet(markerOptions.getSnippet() + ", Distance: " + distance + "cm");
                                }
                                if (percentage != null) {
                                    markerOptions.snippet(markerOptions.getSnippet() + ", Percentage: " + percentage + "%");
                                }
                                // Check if marker exists in the map
                                Marker marker = markerMap.get(dustbinId);
                                if (marker != null) {
                                    // If marker exists, update its position and snippet
                                    marker.setPosition(location);
                                    marker.setSnippet(markerOptions.getSnippet());
                                } else {
                                    // If marker doesn't exist, create new marker
                                    marker = mMap.addMarker(markerOptions);
                                    markerMap.put(dustbinId, marker);
                                }
                                // Move camera to the marker
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

        // Set up marker click listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Open marker info window
                marker.showInfoWindow();
                return true;
            }
        });
    }
}
