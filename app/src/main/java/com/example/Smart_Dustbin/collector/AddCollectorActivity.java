// AddCollectorActivity.java
package com.example.Smart_Dustbin.collector;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Smart_Dustbin.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCollectorActivity extends AppCompatActivity {

    private EditText nameEditText, aadharEditText, mobileEditText, passwordEditText; // Added passwordEditText
    private Button addCollectorButton;
    private DatabaseReference collectorsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collector);

        // Initialize Firebase Database
        collectorsRef = FirebaseDatabase.getInstance().getReference().child("collectors");

        // Initialize Views
        nameEditText = findViewById(R.id.edit_text_name);
        aadharEditText = findViewById(R.id.edit_text_aadhar);
        mobileEditText = findViewById(R.id.edit_text_mobile);
        passwordEditText = findViewById(R.id.edit_text_password); // Initialize passwordEditText
        addCollectorButton = findViewById(R.id.btn_add_collector);

        addCollectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCollector();
            }
        });
    }

    private void addCollector() {
        String name = nameEditText.getText().toString().trim();
        String aadhar = aadharEditText.getText().toString().trim();
        String phoneNumber = mobileEditText.getText().toString().trim(); // Use mobileEditText for phone number
        String password = passwordEditText.getText().toString().trim(); // Use passwordEditText for password

        if (name.isEmpty() || aadhar.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save collector details to Firebase Database
        String collectorId = phoneNumber; // Use phone number as the collector ID
        Collector collector = new Collector(collectorId, name, aadhar, phoneNumber, password);
        collectorsRef.child(collectorId).setValue(collector);

        Toast.makeText(this, "Collector added successfully", Toast.LENGTH_SHORT).show();
        finish(); // Close the activity after adding the collector
    }
}
