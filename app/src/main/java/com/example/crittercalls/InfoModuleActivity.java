package com.example.crittercalls;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

public class InfoModuleActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_module);

        backButton = findViewById(R.id.back_btn);
        title = findViewById(R.id.toolbar_title);
        title.setText("Machine Learning Information");
        title.setTextSize(20);

        addListeners();
    }

    private void addListeners() {
        backButton.setOnClickListener(v -> {
            Intent redirectToHome = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(redirectToHome);
            finish();
        });
    }
}