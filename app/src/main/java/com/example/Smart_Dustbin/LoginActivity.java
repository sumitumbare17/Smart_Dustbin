package com.example.Smart_Dustbin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Smart_Dustbin.collector.CollectorDashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText email, pass;
    Button btn;
    TextView reg;
    FirebaseAuth mAuth;

    /*@Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this,Dashboard.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.login_email);
        pass = findViewById(R.id.login_password);

        btn = findViewById(R.id.login_button);
        reg = findViewById(R.id.register_text);

       *//* reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,registerActivity.class);
                startActivity(i);
            }
        });*//*


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String UEmail = email.getText().toString();
                String UPass = pass.getText().toString();
                Log.d(TAG, "onClick: " + UEmail);
                if (UEmail.length() == 0 || UPass.length() == 0) {
                    Toast.makeText(LoginActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(UEmail, UPass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                        finish();


                                    } else {
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }

            }
        });




    }*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email);
        pass = findViewById(R.id.login_password);

        btn = findViewById(R.id.login_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UEmail = email.getText().toString();
                String UPass = pass.getText().toString();

                if (UEmail.equals("admin") && UPass.equals("12345")) {
                    // Admin login
                    startActivity(new Intent(LoginActivity.this, Dashboard.class));
                } else {
                    // Collector login
                    DatabaseReference collectorsRef = FirebaseDatabase.getInstance().getReference("collectors");
                    collectorsRef.orderByChild("phoneNumber").equalTo(UEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String password = snapshot.child("password").getValue(String.class);
                                    if (password != null && password.equals(UPass)) {
                                        // Collector login successful
                                        Intent intent = new Intent(LoginActivity.this, CollectorDashboard.class);
                                        intent.putExtra("mobileNo", UEmail); // Replace "key" with your key and "value" with the actual data
                                        startActivity(intent);
                                        return;
                                    }
                                }
                                // If the password does not match
                                Toast.makeText(LoginActivity.this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                            } else {
                                // If the collector does not exist
                                Toast.makeText(LoginActivity.this, "Collector does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle database error
                            Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }



}