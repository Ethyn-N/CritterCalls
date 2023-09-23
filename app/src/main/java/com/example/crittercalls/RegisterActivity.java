package com.example.crittercalls;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private EditText userName, userEmail, userPassword, confirmPassword;
    private Button regBtn;
    private TextView loginLink;
    private Intent homeIntent;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.reg_name);
        userName = findViewById(R.id.reg_email);
        userPassword = findViewById(R.id.reg_password);
        confirmPassword = findViewById(R.id.reg_confirm_password);
        regBtn = findViewById(R.id.btn_reg);

        homeIntent = new Intent(this, MainActivity.class);

        firebaseAuth = FirebaseAuth.getInstance();

        addListeners();
    }

    private void addListeners() {
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString();
                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();
                String confirmPass = confirmPassword.getText().toString();

                if(name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                     showMessage("Invalid Inputs!");
                }
                else {
                    registerUser(name, email, password);
                }
            }
        });
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private void registerUser(String name, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // Redirect to main
                        }
                        else {
//                            registerUser(name, email, password);
                            showMessage((task.getException().getMessage()));
                        }
                    }
                });
    }

}