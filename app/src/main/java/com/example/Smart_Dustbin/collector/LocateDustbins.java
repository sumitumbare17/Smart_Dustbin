package com.example.Smart_Dustbin.collector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Smart_Dustbin.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocateDustbins extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private Map<String, Marker> markerMap;
    private String mobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_dustbins);
        Intent intent = getIntent();
        mobileNo = intent.getStringExtra("mobileNo"); // Replace "key" with the key you used in the sender activity

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

        // Enable marker labels
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.marker_info_window, null);
                // Get the views from the layout
                TextView titleTextView = view.findViewById(R.id.title);
                TextView snippetTextView = view.findViewById(R.id.snippet);
                // Set the title and snippet
                titleTextView.setText(marker.getTitle());
                snippetTextView.setText(marker.getSnippet());
                return view;
            }
        });

        // Set up marker click listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Zoom towards the marker
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                // Open dialog to display pie chart
                showPieChartDialog(marker);
                return true;
            }
        });

        // Fetch dustbin data and add markers
        fetchDustbinData();
    }

    private void fetchDustbinData() {
        // Retrieve collector document
        mDatabase.child("collectors").child(mobileNo).addListenerForSingleValueEvent(new ValueEventListener() {
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
    }

    private void showPieChartDialog(Marker marker) {
        String dustbinId = marker.getTitle().replace("Dustbin ", "");
        // Fetch percentage data from Firebase for the selected dustbin
        mDatabase.child("dustbins").child(dustbinId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the percentage value from the dataSnapshot
                Float percentage = dataSnapshot.child("currentpercentage").getValue(Float.class);
                // Get other dustbin details
                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                Float weight = dataSnapshot.child("currentweight").getValue(Float.class);
                Float distance = dataSnapshot.child("currentdistance").getValue(Float.class);

                // Create and show the dialog with the pie chart and dustbin details
                showPercentageDialog(percentage, latitude, longitude, weight, distance, dustbinId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void showPercentageDialog(Float percentage, Double latitude, Double longitude, Float weight, Float distance, String dustbinId) {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_pie_chart, null);
        // Initialize the PieChart from the layout
        PieChart pieChart = dialogView.findViewById(R.id.pie_chart);
        // Customize pie chart with percentage data
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(percentage, "Filled"));
        entries.add(new PieEntry(100 - percentage, "Empty"));
        PieDataSet dataSet = new PieDataSet(entries, "Dustbin Fullness");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate(); // Refresh chart

        // Build dustbin details message
        StringBuilder detailsBuilder = new StringBuilder();
        detailsBuilder.append("Dustbin ").append(dustbinId).append("\n");
        if (weight != null) {
            detailsBuilder.append("Weight: ").append(weight).append("kg\n");
        }
        if (distance != null) {
            detailsBuilder.append("Distance: ").append(distance).append("cm\n");
        }
        // Add other dustbin details as needed

        // Set dustbin details message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setMessage(detailsBuilder.toString())
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}