package com.example.crittercalls;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class EditAccountActivity extends AppCompatActivity {
    private EditText firstName, lastName, email, utaID, profession;
    private TextView changeProfileImgLink;
    private ImageView profilePicture;
    private ImageButton backButton;
    private Button saveChangesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        firstName = findViewById(R.id.edit_firstname);
        lastName = findViewById(R.id.edit_lastname);
        email = findViewById(R.id.edit_email);
        utaID = findViewById(R.id.edit_UTA_ID);
        profession = findViewById(R.id.edit_profession);

        changeProfileImgLink = findViewById(R.id.edit_profile_img_link);
        profilePicture = findViewById(R.id.edit_profile_img);
        backButton = findViewById(R.id.edit_back_btn);
        saveChangesButton = findViewById(R.id.edit_save_changes_btn);

        addListeners();
    }

    private void addListeners() {
        backButton.setOnClickListener(v -> {
            Intent redirectToProfile = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(redirectToProfile);
            finish();
        });
    }



}