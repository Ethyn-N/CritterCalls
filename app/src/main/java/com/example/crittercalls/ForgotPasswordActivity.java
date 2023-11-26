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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.List;

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
            String emailValue = email.getText().toString().trim();
            if (emailValue.isEmpty()) {
                showMessage("Please fill the email field.");
            }
            else {
                resetPassword(emailValue);
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

        // Check if the email is associated with a user in Firebase Authentication
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(signInMethodsTask -> {
                    if (signInMethodsTask.isSuccessful()) {
                        List<String> signInMethods = signInMethodsTask.getResult().getSignInMethods();

                        if (signInMethods != null && !signInMethods.isEmpty()) {
                            // Email exists in the system, send the password reset email
                            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    showMessage("Reset Password link has been sent to email.");
                                    Intent redirectToLogin = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(redirectToLogin);
                                    finish();
                                } else {
                                    showMessage(task.getException().getLocalizedMessage());
                                    progressBar.setVisibility(View.INVISIBLE);
                                    resetPasswordButton.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            // Email not found in the system
                            showMessage("Sorry, the provided email was not found in our system.");
                            progressBar.setVisibility(View.INVISIBLE);
                            resetPasswordButton.setVisibility(View.VISIBLE);
                        }
                    } else {
                        showMessage(signInMethodsTask.getException().getLocalizedMessage());
                        progressBar.setVisibility(View.INVISIBLE);
                        resetPasswordButton.setVisibility(View.VISIBLE);
                    }
                });
    }

//                .addOnSuccessListener(unused -> {
//                    showMessage("Reset Password link has been sent to email.");
//                    Intent redirectToLogin = new Intent(getApplicationContext(), LoginActivity.class);
//                    startActivity(redirectToLogin);
//                    finish();            })
//                .addOnFailureListener(e -> {
//                    showMessage(e.getMessage());
//                    progressBar.setVisibility(View.INVISIBLE);
//                    resetPasswordButton.setVisibility(View.VISIBLE);
//                });

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}