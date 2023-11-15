package com.example.crittercalls;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.text.TextWatcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EditAccountActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextView title;
    private EditText firstName, lastName, email, utaID, profession;
    private TextView changeProfileImgLink;
    private ImageView profilePicture;
    private Uri selectedImageUri;
    private Button saveChangesButton;
    private ProgressBar progressBar;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private ActivityResultLauncher<Intent> imagePickLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            setProfilePic(getApplicationContext(), selectedImageUri, profilePicture);
                            saveChangesButton.setVisibility(View.VISIBLE);
                        }
                    }
                }
            );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        backButton = findViewById(R.id.back_btn);
        title = findViewById(R.id.toolbar_title);
        title.setText("Edit Account");

        firstName = findViewById(R.id.edit_firstname);
        lastName = findViewById(R.id.edit_lastname);
        email = findViewById(R.id.edit_email);
        utaID = findViewById(R.id.edit_UTA_ID);
        profession = findViewById(R.id.edit_profession);

        Intent profileData = getIntent();
        String firstNameData = profileData.getStringExtra("firstName");
        String lastNameData = profileData.getStringExtra("lastName");
        String emailData = profileData.getStringExtra("email");
        String utaIDData = profileData.getStringExtra("utaID");
        String professionData = profileData.getStringExtra("profession");

        firstName.setText(firstNameData);
        lastName.setText(lastNameData);
        email.setText(emailData);
        utaID.setText(utaIDData);
        profession.setText(professionData);

        changeProfileImgLink = findViewById(R.id.edit_profile_img_link);
        profilePicture = findViewById(R.id.edit_profile_img);
        saveChangesButton = findViewById(R.id.edit_save_changes_btn);
        progressBar = findViewById(R.id.edit_account_progress);

        saveChangesButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();

        StorageReference profileRef = storageReference.child("users/" + firebaseAuth.getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> setProfilePic(getApplicationContext(), uri, profilePicture));

        addListeners();
        watchText(firstName);
        watchText(lastName);
        watchText(email);
        watchText(utaID);
        watchText(profession);
    }

    private void addListeners() {
        backButton.setOnClickListener(v -> {
            Intent redirectToProfile = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(redirectToProfile);
            finish();
        });

        changeProfileImgLink.setOnClickListener(v -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(intent -> {
                        imagePickLauncher.launch(intent);
                        return null;
                    });
        });

        saveChangesButton.setOnClickListener(v -> {
            saveChangesButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            if (selectedImageUri != null) {
                uploadImageToFirebase(selectedImageUri);
            }
            saveEditAccount();
        });
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private static void setProfilePic(Context context, Uri imageURI, ImageView imageView) {
        Glide.with(context).load(imageURI).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageReference.child("users/" + firebaseAuth.getUid() + "/profile.jpg");
        fileRef.putFile(imageUri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage(e.getMessage());
            }
        });
    }

    private void saveEditAccount() {
        if (firstName.getText().toString().isEmpty()) {
            showMessage("First Name Must be Filled!");
            return;
        }
        else if (lastName.getText().toString().isEmpty()) {
            showMessage("Last Name Must be Filled!");
            return;
        }
        else if (email.getText().toString().isEmpty()) {
            showMessage("Email must be filled");
            return;
        }
        user.updateEmail(email.getText().toString()).addOnSuccessListener(unused -> {
            DocumentReference documentReference = firestore.collection("users").document(user.getUid());
            Map<String, Object> edits = new HashMap<>();
            edits.put("email", email.getText().toString());
            edits.put("firstName", firstName.getText().toString());
            edits.put("lastName", lastName.getText().toString());
            edits.put("utaID", utaID.getText().toString());
            edits.put("profession", profession.getText().toString());
            documentReference.update(edits).addOnSuccessListener(unused1 -> {
                delayTransition();
            });
        }).addOnFailureListener(e -> {
            saveChangesButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            showMessage(e.getMessage());
        });
    }

    private void watchText(EditText text) {
       text.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

           }

           @Override
           public void afterTextChanged(Editable s) {
               saveChangesButton.setVisibility(View.VISIBLE);
           }
       });
    }

    private void delayTransition() {
        Runnable runnable = () -> {
            showMessage("Account successfully updated.");
            Intent redirectToProfile = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(redirectToProfile);
            finish();
        };
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, 500); // delayed by 0.5 seconds
    }
}