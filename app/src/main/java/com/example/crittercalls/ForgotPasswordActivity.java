package com.example.crittercalls;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText email;
    private Button resetPasswordButton;
    private ImageButton backButton;
    private ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.forgot_pass_email);
        resetPasswordButton = findViewById(R.id.forgot_pass_reset_btn);
        backButton = findViewById(R.id.forgot_pass_back_btn);
        progressBar = findViewById(R.id.forgot_pass_progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();

        addListeners();

    }

    private void addListeners() {
        resetPasswordButton.setOnClickListener(v -> {
            String emailValue = email.getText().toString();
            if (!emailValue.isEmpty()) {
                resetPassword(emailValue);
            }
            else {
                showMessage("Please fill the email field.");
            }
        });


        backButton.setOnClickListener(v -> {
            Intent redirectToLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(redirectToLogin);
            finish();
        });
    }

    private void resetPassword(String email) {
        progressBar.setVisibility(View.VISIBLE);
        resetPasswordButton.setVisibility(View.INVISIBLE);

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    showMessage("Reset Password link has been sent to email.");
                    Intent redirectToLogin = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(redirectToLogin);
                    finish();            })
                .addOnFailureListener(e -> {
                    showMessage(e.getMessage());
                    progressBar.setVisibility(View.INVISIBLE);
                    resetPasswordButton.setVisibility(View.VISIBLE);
                });

    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}