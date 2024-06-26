package com.example.crittercalls;

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
import android.widget.TableLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabItem;
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
//import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.ml.modeldownloader.CustomModel;
//import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
//import com.google.firebase.ml.modeldownloader.DownloadType;
//import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.tensorflow.lite.Interpreter;
import org.w3c.dom.Text;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextView title;
    private ImageButton profileBtn, classBtn;
    private Button infoBtn;
    private TextView welcome;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backButton = findViewById(R.id.back_btn);
        title = findViewById(R.id.toolbar_title);

        backButton.setEnabled(false);
        backButton.setVisibility(View.INVISIBLE);
        title.setText("Critter Calls");

        profileBtn = findViewById(R.id.redirect_profile_btn);
        welcome = findViewById(R.id.text_welcome);
        classBtn = findViewById(R.id.btn_classification);
        infoBtn = findViewById(R.id.btn_module_info);

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
                setProfilePic(getApplicationContext(), uri, profileBtn);
            }
        });
    }

    private void addListeners() {
        profileBtn.setOnClickListener(v -> {
            Intent redirectToProfile = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(redirectToProfile);
            finish();
        });

        classBtn.setOnClickListener(v -> {
            Intent redirectToClassification = new Intent(getApplicationContext(), AudioHelperActivity.class);
            startActivity(redirectToClassification);
            finish();
        });

        infoBtn.setOnClickListener(v -> {
            Intent redirectToInfo = new Intent(getApplicationContext(), InfoModuleActivity.class);
            startActivity(redirectToInfo);
            finish();
        });
    }

    private static void setProfilePic(Context context, Uri imageURI, ImageView imageView) {
        Glide.with(context).load(imageURI).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

}