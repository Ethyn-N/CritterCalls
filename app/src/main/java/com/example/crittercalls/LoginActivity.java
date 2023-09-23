package com.example.crittercalls;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
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
    private TextView registerLink;
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

        homeIntent = new Intent(this, MainActivity.class);

        firebaseAuth = FirebaseAuth.getInstance();

        addListeners();
    }

    private void addListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailValue = email.getText().toString();
                String passwordValue = password.getText().toString();

                loginButton.setVisibility(View.INVISIBLE);
                loginProgressBar.setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(), "Button Clicked", Toast.LENGTH_LONG).show();

                if(emailValue.isEmpty() || passwordValue.isEmpty()) {
                    loginButton.setVisibility(View.VISIBLE);
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    showMessage("Invalid Email or Password Value!");
                }
                else {
                    login(emailValue, passwordValue);
                }
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirectToRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(redirectToRegister);
                finish();
            }
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
                        if(task.isSuccessful()) {
                            loginButton.setVisibility(View.VISIBLE);
                            loginProgressBar.setVisibility(View.INVISIBLE);
                            //Redirect to Main Screen
                            startActivity(homeIntent);
                            finish();
                        }
                        else {
                            String errorMsg = task.getException().getMessage();
                            loginButton.setVisibility(View.VISIBLE);
                            loginProgressBar.setVisibility(View.INVISIBLE);
                            showMessage(errorMsg);
                        }
                    }
                });
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_login);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}