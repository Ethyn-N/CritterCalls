package com.example.crittercalls;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class UserProfile extends AppCompatActivity {

    private TextView name, email, profession, UTA_id;
    private ImageView photo;
    private Button logout_button, edit_acc_button;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        email = findViewById(R.id.profile_email);
        name = findViewById(R.id.profile_name);
        UTA_id = findViewById(R.id.profile_UTA_ID);
        profession = findViewById(R.id.profile_profession);
        photo = findViewById(R.id.profile_img);

        logout_button = findViewById(R.id.logout_btn);
        edit_acc_button = findViewById(R.id.profile_edit_account_link);

        auth = FirebaseAuth.getInstance();

        addListeners();
    }

    private void addListeners()
    {
        logout_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        }
        );
    }

    private void logout()
    {
        auth.signOut();
    }

}