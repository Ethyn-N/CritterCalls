package com.example.crittercalls;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.StartupTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileActivity extends AppCompatActivity {
    private TextView fullname, email, utaID, profession, editProfileLink;
    private ImageButton backButton;
    private Button logoutButton;
    private ImageView profilePicture;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        backButton = findViewById(R.id.profile_back_btn);
        logoutButton = findViewById(R.id.profile_logout_btn);
        profilePicture = findViewById(R.id.profile_img);
        editProfileLink = findViewById(R.id.profile_edit_account_link);

        fullname = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);
        utaID = findViewById(R.id.profile_UTA_ID);
        profession = findViewById(R.id.profile_profession);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid();

        addAccountInformation();
        addListeners();
    }

    private void addAccountInformation() {
        DocumentReference documentReference = firestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException e) {
                fullname.setText( "Name: " + value.getString("firstName") + " " + value.getString("lastName"));
                email.setText("Email: " + value.getString("email"));
                utaID.setText("UTA ID: " + value.getString("utaID"));
                profession.setText("Profession: " + value.getString("profession"));
            }
        });
    }

    private void addListeners() {
        backButton.setOnClickListener(v -> {
            Intent redirectToHome = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(redirectToHome);
            finish();
        });

        editProfileLink.setOnClickListener(v -> {
            Intent redirectToEditAccount = new Intent(getApplicationContext(), EditAccountActivity.class);
            startActivity((redirectToEditAccount));
            finish();
        });

        logoutButton.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Intent redirectToLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(redirectToLogin);
            finish();
        });

    }

}