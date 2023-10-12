package com.example.crittercalls;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;

public class UserProfile extends AppCompatActivity {

    private String fname;
    private String lname;
    private String email;
    private Image photo;
    private int id;
    private String profession;

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

    public void logout()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
    }



}