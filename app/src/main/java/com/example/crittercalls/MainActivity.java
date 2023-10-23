package com.example.crittercalls;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity {
    private ImageButton profile_btn;
    private TextView welcome;
    private Button classificationButton, modulesButton, statsButton;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile_btn = findViewById(R.id.redirect_profile_btn);
        classificationButton = findViewById(R.id.btn_classification);
        modulesButton = findViewById(R.id.btn_module_info);
        statsButton = findViewById(R.id.btn_statistics);
        welcome = findViewById(R.id.text_welcome);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        welcomeMessage();
        addListeners();
    }

    private void welcomeMessage()
    {
        DocumentReference documentReference = firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                welcome.setText("Welcome, " + value.getString("firstName"));
            }
        });
    }

    private void addListeners() {
        profile_btn.setOnClickListener(v -> {
            Intent redirectToProfile = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(redirectToProfile);
            finish();
        });

        classificationButton.setOnClickListener(v -> {
            //Intent redirectToClassification = new Intent(getApplicationContext(), ClassificationActivity.class);
            //startActivity(redirectToClassification);
            finish();
        });

        modulesButton.setOnClickListener(v -> {
           //Intent redirectToModules = new Intent(getApplicationContext(), ModuleActivity.class);
           //startActivity(redirectToModules);
           finish();
        });

        statsButton.setOnClickListener(v -> {
            //Intent redirectToStats = new Intent(getApplicationContext(), StatsActivity.class);
            //startActivity(redirectToStats);
            finish();
        });
    }
}