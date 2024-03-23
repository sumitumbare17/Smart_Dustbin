package com.example.demo_app;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        Log.d(TAG, "onClick: " + UEmail);
        if (UEmail.length() == 0 || UPass.length() == 0) {
            Toast.makeText(LoginActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
        } else {

            if(UEmail.equals("admin") && UPass.equals("12345")){


                startActivity(new Intent(LoginActivity.this, Dashboard.class));
            }
            else{
                Toast.makeText(LoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        }

            }
        });

    }



}