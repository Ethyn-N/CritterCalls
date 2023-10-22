package com.example.crittercalls;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.core.view.WindowCompat;


import com.example.crittercalls.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private TextView registerLink, forgotPasswordLink;
    private ProgressBar loginProgressBar;
    private Button loginButton;
    private Intent homeIntent;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.btn_login);

        loginProgressBar = findViewById(R.id.login_progress);
        loginProgressBar.setVisibility(View.INVISIBLE);

        registerLink = findViewById(R.id.text_register);
        forgotPasswordLink = findViewById(R.id.login_forgot_password);

        homeIntent = new Intent(this, MainActivity.class);

        firebaseAuth = FirebaseAuth.getInstance();

        addListeners();
    }

    private void addListeners() {
        loginButton.setOnClickListener(v -> {
            String emailValue = email.getText().toString();
            String passwordValue = password.getText().toString();

            loginButton.setVisibility(View.INVISIBLE);
            loginProgressBar.setVisibility(View.VISIBLE);

            if (emailValue.isEmpty() || passwordValue.isEmpty()) {
                loginButton.setVisibility(View.VISIBLE);
                loginProgressBar.setVisibility(View.INVISIBLE);
                showMessage("Invalid Email or Password Value!");
            } else {
                login(emailValue, passwordValue);
            }
        });

        registerLink.setOnClickListener(v -> {
            Intent redirectToRegister = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(redirectToRegister);
            finish();
        });

        forgotPasswordLink.setOnClickListener(v -> {
            Intent redirectToForgotPassword = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
            startActivity(redirectToForgotPassword);
            finish();
        });
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loginButton.setVisibility(View.VISIBLE);
                            loginProgressBar.setVisibility(View.INVISIBLE);
                            //Redirect to Main Screen
                            startActivity(homeIntent);
                            finish();
                        } else {
                            String errorMsg = task.getException().getMessage();
                            loginButton.setVisibility(View.VISIBLE);
                            loginProgressBar.setVisibility(View.INVISIBLE);
                            showMessage(errorMsg);
                        }
                    }
                });
    }
}