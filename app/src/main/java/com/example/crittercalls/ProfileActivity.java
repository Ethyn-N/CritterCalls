package com.example.crittercalls;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.StartupTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private TextView fullname, email, utaID, profession, editProfileLink;
    private String firstNameData, lastNameData, emailData, utaIDData, professionData;
    private ImageButton backButton;
    private Button logoutButton;
    private ImageView profilePicture;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

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
        storageReference = FirebaseStorage.getInstance().getReference();


        userID = firebaseAuth.getCurrentUser().getUid();

        addAccountInformation();
        addListeners();

        StorageReference profileRef = storageReference.child("users/" + firebaseAuth.getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setProfilePic(getApplicationContext(), uri, profilePicture);
            }
        });
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

                firstNameData = value.getString("firstName");
                lastNameData = value.getString("lastName");
                emailData = value.getString("email");
                utaIDData = value.getString("utaID");
                professionData = value.getString("profession");
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
            redirectToEditAccount.putExtra("firstName", firstNameData);
            redirectToEditAccount.putExtra("lastName", lastNameData);
            redirectToEditAccount.putExtra("email", emailData);
            redirectToEditAccount.putExtra("utaID", utaIDData);
            redirectToEditAccount.putExtra("profession", professionData);
            startActivity((redirectToEditAccount));
            finish();
        });

        logoutButton.setOnClickListener(v -> {
            Intent redirectToLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(redirectToLogin);
            finish();
        });

    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private static void setProfilePic(Context context, Uri imageURI, ImageView imageView) {
        Glide.with(context).load(imageURI).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

}