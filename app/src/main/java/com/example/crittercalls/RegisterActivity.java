package com.example.crittercalls;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private EditText firstName, lastName, userEmail, userPassword, confirmPassword;
    private Button regBtn;
    private TextView loginLink;
    private Intent homeIntent;
    private FirebaseAuth firebaseAuth;

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

        loginLink = findViewById(R.id.text_login);

        homeIntent = new Intent(this, MainActivity.class);

        firebaseAuth = FirebaseAuth.getInstance();

        addListeners();
    }

    private void addListeners() {
        regBtn.setOnClickListener(v -> {
            String first = firstName.getText().toString();
            String last = lastName.getText().toString();
            String email = userEmail.getText().toString();
            String password = userPassword.getText().toString();
            String confirmPass = confirmPassword.getText().toString();

            if(first.isEmpty() || last.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                 showMessage("Invalid Inputs!");
            }
            else if (!password.equals(confirmPass))
            {
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
                        startActivity(homeIntent);
                        finish();
                    }
                    else {
//                        registerUser(name, email, password);
                        showMessage((task.getException().getMessage()));
                    }
                });
    }

}