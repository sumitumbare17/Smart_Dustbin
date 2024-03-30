package com.example.Smart_Dustbin.collector;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Smart_Dustbin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DustbinStatusActivity extends AppCompatActivity {

    private DatabaseReference dustbinsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dustbin_status);

        // Initialize Firebase Realtime Database reference
        dustbinsRef = FirebaseDatabase.getInstance().getReference("dustbins");

        // Retrieve dustbin data for the selected collector
        String collectorId = "9373197797"; // Replace with actual collector ID
        retrieveDustbinStatus(collectorId);
    }

    private void retrieveDustbinStatus(String collectorId) {
        dustbinsRef.child(collectorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterate through dustbins data and update visualization
                for (DataSnapshot dustbinSnapshot : dataSnapshot.getChildren()) {
                    String dustbinId = dustbinSnapshot.getKey();
                    // Retrieve dustbin status fields (e.g., distance, weight) and update visualization
                    // Example:
                    Long distance = dustbinSnapshot.child("distance").getValue(Long.class);
                    Long weight = dustbinSnapshot.child("weight").getValue(Long.class);
                    // Update visualization with dustbin status (e.g., set TextViews)
                    // Example:
                    updateDustbinVisualization(dustbinId, distance, weight);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void updateDustbinVisualization(String dustbinId, Long distance, Long weight) {
        // Update visualization with dustbin status (e.g., set TextViews)
        // Example:
        TextView dustbinTextView = findViewById(R.id.dustbinTextView);
        dustbinTextView.setText("Dustbin ID: " + dustbinId + "\nDistance: " + distance + "\nWeight: " + weight);
        // You can customize this method based on your visualization requirements
    }
}
