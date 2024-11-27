package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginapp.R;
import com.example.loginapp.RegisterActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        getSupportActionBar().setTitle("Environment Monitoring System");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Environment Monitoring System");
        }
        //Open Login Activity
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class); // Consider renaming LoginActivity
            startActivity(intent);
        });


        //Open register Activity
        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class); // Consider renaming LoginActivity
            startActivity(intent);
        });
    }
}