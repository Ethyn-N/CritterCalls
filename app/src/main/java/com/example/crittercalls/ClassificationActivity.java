package com.example.crittercalls;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.crittercalls.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.tensorflow.lite.support.label.Category;

import java.util.ArrayList;
import java.util.List;

public abstract class ClassificationActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextView title;
    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;
    public final static int REQUEST_RECORD_AUDIO = 2033;
    protected ImageView animalImage;
    protected List<String> resultsList = new ArrayList<>();
    protected TextView outputTextView;
    protected Button startRecordingButton;
    protected Button stopRecordingButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_classification);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_classification);

        animalImage = findViewById(R.id.animalView);
        animalImage.setVisibility(View.VISIBLE);
        outputTextView = findViewById(R.id.textViewOutput);

        startRecordingButton = findViewById(R.id.buttonStartRecording);
        stopRecordingButton = findViewById(R.id.buttonStopRecording);

        stopRecordingButton.setEnabled(false);

//        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
//        }

        backButton = findViewById(R.id.back_btn);
        title = findViewById(R.id.toolbar_title);
        title.setText("Animal Classification");

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if(item.getItemId() == R.id.menu_classification)
            {
                if(oldFragment != null)
                {
                    getSupportFragmentManager().beginTransaction().remove(oldFragment).commit();
                }
                return true;
            }
            else if(item.getItemId() == R.id.menu_statistics)
            {
                oldFragment = new StatsFragment();
                fragmentTransaction.replace(R.id.frameLayout, oldFragment);
                fragmentTransaction.commit();
                return true;
            }
            else if (item.getItemId() == R.id.menu_list)
            {
                oldFragment = new ListFragment();
                fragmentTransaction.replace(R.id.frameLayout, oldFragment);
                fragmentTransaction.commit();
                return true;
            }
            return false;
        });

        addListeners();
    }

    public abstract void onStartRecording(View view);

    public abstract void onStopRecording(View view);

    private void addListeners() {
        backButton.setOnClickListener(v -> {
            Intent redirectToHome = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(redirectToHome);
            finish();
        });
    }
}