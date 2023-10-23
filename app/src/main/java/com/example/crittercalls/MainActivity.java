package com.example.crittercalls;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private ImageButton profile_btn;
    private TextView welcome;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile_btn = findViewById(R.id.redirect_profile_btn);
        welcome = findViewById(R.id.text_welcome);

        addListeners();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userID = firebaseAuth.getCurrentUser().getUid();

        DocumentReference documentReference = firestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, (value, e) -> welcome.setText("Welcome,\n" + value.getString("firstName")));

        StorageReference profileRef = storageReference.child("users/" + firebaseAuth.getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setProfilePic(getApplicationContext(), uri, profile_btn);
            }
        });
    }

    private void addListeners() {
        profile_btn.setOnClickListener(v -> {
            Intent redirectToProfile = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(redirectToProfile);
            finish();
        });
    }

    private static void setProfilePic(Context context, Uri imageURI, ImageView imageView) {
        Glide.with(context).load(imageURI).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}