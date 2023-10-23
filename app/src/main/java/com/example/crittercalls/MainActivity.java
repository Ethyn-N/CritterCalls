package com.example.crittercalls;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private ImageButton profile_btn;
    private Button classificationButton, modulesButton, statsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile_btn = findViewById(R.id.redirect_profile_btn);
        classificationButton = findViewById(R.id.btn_classification);
        modulesButton = findViewById(R.id.btn_module_info);
        statsButton = findViewById(R.id.btn_statistics);

        welcomeMessage();
        addListeners();


    }

    private void welcomeMessage()
    {

    }

    private void addListeners() {
        profile_btn.setOnClickListener(v -> {
            Intent redirectToProfile = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(redirectToProfile);
            finish();
        });


    }
}