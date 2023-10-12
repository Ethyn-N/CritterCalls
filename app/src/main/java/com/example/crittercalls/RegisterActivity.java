package com.example.crittercalls;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class RegisterActivity extends AppCompatActivity {
    private EditText firstName, lastName, userEmail, userPassword, confirmPassword;
    private String userID;
    private Button regBtn;
    private ProgressBar registerProgressBar;
    private TextView loginLink;
    private Intent homeIntent;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstName = findViewById(R.id.reg_firstname);
        lastName = findViewById(R.id.reg_lastname);
        userEmail = findViewById(R.id.reg_email);
        userPassword = findViewById(R.id.reg_password);
        confirmPassword = findViewById(R.id.reg_confirm_password);
        regBtn = findViewById(R.id.btn_reg);
        registerProgressBar = findViewById(R.id.reg_progress);

        loginLink = findViewById(R.id.text_login);

        homeIntent = new Intent(this, MainActivity.class);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        addListeners();
    }

    private void addListeners() {
        regBtn.setOnClickListener(v -> {
            String first = firstName.getText().toString();
            String last = lastName.getText().toString();
            String email = userEmail.getText().toString();
            String password = userPassword.getText().toString();
            String confirmPass = confirmPassword.getText().toString();

            regBtn.setVisibility(View.INVISIBLE);
            registerProgressBar.setVisibility(View.VISIBLE);

            if(first.isEmpty() || last.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                regBtn.setVisibility(View.VISIBLE);
                registerProgressBar.setVisibility(View.INVISIBLE);
                showMessage("Invalid Inputs!");
            }
            else if (!password.equals(confirmPass))
            {
                regBtn.setVisibility(View.VISIBLE);
                registerProgressBar.setVisibility(View.INVISIBLE);
                showMessage("Password does not match Confirm Password");
            }
            else {
                registerUser(first, last, email, password);
            }
        });

        loginLink.setOnClickListener(v -> {
            Intent redirectToLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(redirectToLogin);
            finish();
        });
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private void registerUser(String first, String last, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        userID = firebaseAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = firestore.collection("users").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("firstName", first);
                        user.put("lastName", last);
                        user.put("email", email);
                        user.put("password", password);

                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.toString());
                            }
                        });

                        startActivity(homeIntent);
                        finish();
                    }
                    else {
                        String errorMsg = task.getException().getMessage();
                        regBtn.setVisibility(View.VISIBLE);
                        registerProgressBar.setVisibility(View.INVISIBLE);
                        showMessage(errorMsg);
                    }
                });
    }

}