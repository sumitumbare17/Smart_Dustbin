package com.example.Smart_Dustbin.collector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Smart_Dustbin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DustbinListActivity extends AppCompatActivity {

    private ListView mDustbinListView;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dustbin_list);

        mDustbinListView = findViewById(R.id.dustbin_list_view);

        // Get current user (collector) ID
        Intent intent = getIntent();
        String collectorId = intent.getStringExtra("mobileNo"); // Replace "mobileNo" with the key you used in the sender activity

        // Initialize Firebase database reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child("collectors").child(collectorId);

        // Retrieve dustbin IDs from Firebase
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("dustbinIds")) {
                    ArrayList<String> dustbinIds = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.child("dustbinIds").getChildren()) {
                        dustbinIds.add(snapshot.getValue(String.class));
                    }
                    // Populate the ListView with dustbin IDs
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DustbinListActivity.this, android.R.layout.simple_list_item_1, dustbinIds);
                    mDustbinListView.setAdapter(adapter);

                    // Set click listener for ListView items
                    mDustbinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Get the selected dustbin ID
                            String selectedDustbinId = dustbinIds.get(position);

                            // Start the activity to display details of the selected dustbin
                            Intent detailsIntent = new Intent(DustbinListActivity.this, WasteVisualizationActivity.class);
                            detailsIntent.putExtra("dustbinId", selectedDustbinId);
                            startActivity(detailsIntent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}
