package com.example.crittercalls;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class UserProfile extends AppCompatActivity {

    private String fname;
    private String lname;
    private String email;
    private Image photo;
    private int id;
    private String profession;
    private FirebaseAuth auth;

    public UserProfile(String fname, String lname, String email, Image photo, int id, String profession)
    {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.photo = photo;
        this.id = id;
        this.profession = profession;
    }

    public UserProfile(String fname, String lname)
    {
        this.fname = fname;
        this.lname = lname;
    }

    private void logout()
    {
        auth.signOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //this.email = findViewById(R.id.login_email);
    }



}