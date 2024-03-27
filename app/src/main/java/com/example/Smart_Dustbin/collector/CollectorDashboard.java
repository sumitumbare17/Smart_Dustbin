package com.example.Smart_Dustbin.collector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Smart_Dustbin.LoginActivity;
import com.example.Smart_Dustbin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CollectorDashboard extends AppCompatActivity {

    TextView textViewCollectorName;
    DatabaseReference collectorsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector_dashboard);

        textViewCollectorName = findViewById(R.id.tv_wel);

        String collectorPhoneNumber = "9373197797";
        collectorsRef = FirebaseDatabase.getInstance().getReference("collectors").child(collectorPhoneNumber);
        collectorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String collectorName = dataSnapshot.child("name").getValue(String.class);
                    if (collectorName != null) {
                        textViewCollectorName.setText("Welcome, " + collectorName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        findViewById(R.id.imgLogOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CollectorDashboard.this, LoginActivity.class));
                finish();
            }
        });

        findViewById(R.id.cardViewDustbinStatus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.cardViewStatistics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
