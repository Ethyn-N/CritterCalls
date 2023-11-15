package com.example.crittercalls;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioRecord;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class ClassificationActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextView title;
    public final static int REQUEST_RECORD_AUDIO = 2033;
    protected TextView outputTextView;
    protected TextView specsTextView;
    protected Button startRecordingButton;
    protected Button stopRecordingButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classification);

        outputTextView = findViewById(R.id.textViewOutput);
        specsTextView = findViewById(R.id.textViewSpec);
        startRecordingButton = findViewById(R.id.buttonStartRecording);
        stopRecordingButton = findViewById(R.id.buttonStopRecording);

        stopRecordingButton.setEnabled(false);

//        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
//        }

        backButton = findViewById(R.id.back_btn);
        title = findViewById(R.id.toolbar_title);
        title.setText("Animal Classification");
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