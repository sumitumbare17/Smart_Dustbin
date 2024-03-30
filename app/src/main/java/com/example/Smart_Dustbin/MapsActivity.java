package com.example.Smart_Dustbin;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.Smart_Dustbin.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private List<String> collectorNames;

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private Marker currentLocationMarker;
    private Marker clickedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupCollectorSpinner();
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button setCurrentLocationButton = findViewById(R.id.btn_set_current_location);

        setCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCurrentLocationMarker();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                clickedMarker = marker;
                showInputDialog(marker.getPosition());
                return true;
            }
        });
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // Remove currentLocationMarker if exists
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        addMarker(latLng);
    }

    private void addMarker(LatLng latLng) {
        // Add marker with custom icon
        currentLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        Toast.makeText(this, "Custom Location - Latitude: " + latLng.latitude + ", Longitude: " + latLng.longitude, Toast.LENGTH_SHORT).show();
    }

    private void addCurrentLocationMarker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                // Remove currentLocationMarker if exists
                                if (currentLocationMarker != null) {
                                    currentLocationMarker.remove();
                                }
                                currentLocationMarker = mMap.addMarker(new MarkerOptions()
                                        .position(currentLatLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
        }
    }

    private void showInputDialog(LatLng latLng) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.dialog_input, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final EditText dustbinIdEditText = promptView.findViewById(R.id.edit_text_dustbin_id);
        final Spinner collectorSpinner = promptView.findViewById(R.id.spinner_collector);

        // Populate the spinner with collector names fetched from the Realtime Database
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, collectorNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        collectorSpinner.setAdapter(adapter);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Submit details to Firebase
                        submitToFirebase(dustbinIdEditText.getText().toString(), collectorSpinner.getSelectedItem().toString(), latLng);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void setupCollectorSpinner() {
        // Fetch collector names from Firebase Realtime Database and populate the spinner
        collectorNames = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("collectors");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String collectorName = snapshot.child("phoneNumber").getValue(String.class);
                        collectorNames.add(collectorName);
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "No collectors found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, "Failed to fetch collector names", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitToFirebase(String dustbinId, String selectedCollector, LatLng location) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dustbinRef = database.getReference("dustbins").child(dustbinId);
        DatabaseReference collectorRef = database.getReference("collectors").child(selectedCollector);

        // Save dustbin details
        dustbinRef.child("selectedCollector").setValue(selectedCollector);
        dustbinRef.child("latitude").setValue(location.latitude);
        dustbinRef.child("longitude").setValue(location.longitude);

        // Update collector's list of dustbin IDs
        collectorRef.child("dustbinIds").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> dustbinIds = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String existingDustbinId = snapshot.getValue(String.class);
                        dustbinIds.add(existingDustbinId);
                    }
                }
                dustbinIds.add(dustbinId); // Add the new dustbin ID
                collectorRef.child("dustbinIds").setValue(dustbinIds)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MapsActivity.this, "Dustbin details submitted successfully", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MapsActivity.this, "Failed to update collector's dustbin IDs", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, "Failed to retrieve collector's dustbin IDs", Toast.LENGTH_LONG).show();
            }
        });
    }

}