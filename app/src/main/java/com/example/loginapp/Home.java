package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {

    private TextView textViewMq6, textViewMq7, textViewMq135;
    private Button buttonLogout;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        textViewMq6 = findViewById(R.id.textView_mq6);
        textViewMq7 = findViewById(R.id.textView_mq7);
        textViewMq135 = findViewById(R.id.textView_mq135);
        buttonLogout = findViewById(R.id.button_login); // Logout button

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("data/sensors");

        // Fetch data for sensors
        fetchSensorData();

        // Set logout button click listener
        buttonLogout.setOnClickListener(v -> {
            // Redirect to main activity (login screen)
            Intent intent = new Intent(Home.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close the current activity
        });
    }

    private void fetchSensorData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Fetch and display data for MQ6
                    Long mq6Value = snapshot.child("MQ6").getValue(Long.class);
                    textViewMq6.setText(mq6Value != null ? String.valueOf(mq6Value) : "N/A");

                    // Fetch and display data for MQ7
                    Long mq7Value = snapshot.child("MQ7").getValue(Long.class);
                    textViewMq7.setText(mq7Value != null ? String.valueOf(mq7Value) : "N/A");

                    // Fetch and display data for MQ135
                    Long mq135Value = snapshot.child("MQ135").getValue(Long.class);
                    textViewMq135.setText(mq135Value != null ? String.valueOf(mq135Value) : "N/A");
                } else {
                    Toast.makeText(Home.this, "No data found in database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
