package com.example.crittercalls;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.HandlerCompat;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AudioHelperActivity extends ClassificationActivity {
    float probabilityThreshold = 0.3f;
    private AudioClassifier audioClassifier;
    private TensorAudio tensorAudio;
    private TimerTask timerTask;

    private static final String MODEL_FILE = "yamnet_classification.tflite";
    private String[] permissions = {android.Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final float MINIMUM_DISPLAY_THRESHOLD = 0.3f;
    private AudioRecord audioRecord;
    private TensorAudio audioTensor;
    private String[] animals = {"dog", "cat", "rooster", "pig", "cow", "frog", "hen", "insects", "sheep", "crow", "chirping_birds"};
    private int bufferSize;
    private short[] audioBuffer;
    private boolean isRecording = false;
    private Interpreter yamNetInterpreter;

    private AudioClassifier mAudioClassifier;
    private AudioRecord mAudioRecord;
    private long classficationInterval = 500;       // 0.5 sec
    private Handler mHandler;
    private boolean permissionToRecordAccepted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        HandlerThread handlerThread = new HandlerThread("backgroundThread");
//        handlerThread.start();
//        mHandler = HandlerCompat.createAsync(handlerThread.getLooper());

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION);


        // Loading the model from the assets folder
        try {
            audioClassifier = AudioClassifier.createFromFile(this, MODEL_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        audioTensor = audioClassifier.createInputTensorAudio();

    }
    @Override
    public void onStartRecording(View view) {
        // Check for audio recording permissions
        if (checkAudioPermission()) {
            // Permission is already granted, proceed with your audio recording logic
            startRecordingButton.setEnabled(false);
            stopRecordingButton.setEnabled(true);
            startRecording();
        } else {
            // Permission has not been granted yet, request it
            requestAudioPermission();
            showMessage("Requesting Permissions");
        }

//        if(mAudioClassifier != null) return;
//
//        try {
//            AudioClassifier classifier = AudioClassifier.createFromFile(this, MODEL_FILE);
//            TensorAudio audioTensor = classifier.createInputTensorAudio();
//
//            AudioRecord record = classifier.createAudioRecord();
//            record.startRecording();
//
//            Runnable run = new Runnable() {
//                @Override
//                public void run() {
//                    audioTensor.load(record);
//                    List<Classifications> output = classifier.classify(audioTensor);
//                    List<Category> filterModelOutput = output.get(0).getCategories();
//                    for(Category c : filterModelOutput) {
//                        if (c.getScore() > MINIMUM_DISPLAY_THRESHOLD)
//                            outputTextView.setText(" label : " + c.getLabel() + " score : " + c.getScore());
////                            Log.d("tensorAudio_java", " label : " + c.getLabel() + " score : " + c.getScore());
//                    }
//
//                    mHandler.postDelayed(this,classficationInterval);
//                }
//            };
//
//            mHandler.post(run);
//            mAudioClassifier = classifier;
//            mAudioRecord = record;
//        }catch (IOException e){
//            e.printStackTrace();
//        }

//        // showing the audio recorder specification
//        TensorAudio.TensorAudioFormat format = audioClassifier.getRequiredTensorAudioFormat();
//        String specs = "Number of channels: " + format.getChannels() + "\n"
//                + "Sample Rate: " + format.getSampleRate();
//        specsTextView.setText(specs);
//
//        // Creating and start recording
//        audioRecord = audioClassifier.createAudioRecord();
//        audioRecord.startRecording();
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                // Classifying audio data
//                List<Classifications> output = audioClassifier.classify(tensorAudio);
//
//                // Filtering out classifications with low probability
//                List<Category> finalOutput = new ArrayList<>();
//                for (Classifications classifications : output) {
//                    for (Category category : classifications.getCategories()) {
//                        if (category.getScore() > probabilityThreshold) {
//                            finalOutput.add(category);
//                        }
//                    }
//                }
//                // Sorting the results
//                Collections.sort(finalOutput, (o1, o2) -> (int) (o1.getScore() - o2.getScore()));
//
//                // Creating a multiline string with the filtered results
//                StringBuilder outputStr = new StringBuilder();
//                for (Category category : finalOutput) {
//                    outputStr.append(category.getLabel())
//                            .append(": ").append(category.getScore()).append("\n");
//                }
//                // Updating the UI
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (finalOutput.isEmpty()) {
//                            outputTextView.setText("Could not classify");
//                        } else {
//                            outputTextView.setText(outputStr.toString());
//                        }
//                    }
//                });
//            }
//        };
//        new Timer().scheduleAtFixedRate(timerTask, 1, 500);
    }

    private void startRecording() {
        // Start recording
        AudioRecord audioRecord = audioClassifier.createAudioRecord();
        audioRecord.startRecording();
        isRecording = true;
        this.audioRecord = audioRecord;
    }
    @Override
    public void onStopRecording(View view) {
        startRecordingButton.setEnabled(true);
        stopRecordingButton.setEnabled(false);

        // Stop recording and release resources
        audioRecord.stop();
        classifyAudio();

        audioRecord.release();
        isRecording = false;



//        mHandler.removeCallbacksAndMessages(null);
//        mAudioRecord.stop();
//        mAudioRecord = null;
//        mAudioClassifier = null;

//        timerTask.cancel();
//        audioRecord.stop();
    }

    private void classifyAudio() {
        audioTensor.load(audioRecord);

        List<Classifications> output = audioClassifier.classify(audioTensor);

        // Filtering out classifications with low probability
        List<Category> finalOutput = new ArrayList<>();
        for (Classifications classifications : output) {
            for (Category category : classifications.getCategories()) {
                if (category.getScore() > MINIMUM_DISPLAY_THRESHOLD) {
                    for (String animal : animals) {
                        if (category.getLabel().equals(animal)) {
                            finalOutput.add(category);
                        }
                    }
                }
            }
        }

//        // Filtering out classifications with low probability
//        List<Category> finalOutput = new ArrayList<>();
//        for (Classifications classifications : output) {
//            for (Category category : classifications.getCategories()) {
//                if (category.getScore() > MINIMUM_DISPLAY_THRESHOLD) {
//                    finalOutput.add(category);
//                }
//            }
//        }

        // Sorting the results
        Collections.sort(finalOutput, (o1, o2) -> (int) (o1.getScore() - o2.getScore()));

        // Creating a multiline string with the filtered results
        StringBuilder outputStr = new StringBuilder();
        for (Category category : finalOutput) {
            outputStr.append(category.getLabel())
                    .append(": ").append(category.getScore()).append("\n");
        }

        if (finalOutput.isEmpty()) {
            outputTextView.setText("Could not classify");
        } else {
            outputTextView.setText(outputStr.toString());
        }



//        List<Classifications> output = audioClassifier.classify(audioTensor);
//        List<Category> filterModelOutput = output.get(0).getCategories();
//        for(Category c : filterModelOutput) {
//            if (c.getScore() > MINIMUM_DISPLAY_THRESHOLD)
//                outputTextView.setText(" label : " + c.getLabel() + " score : " + c.getScore());
//            else
//                outputTextView.setText("Could not classify");
//        }
    }

    private boolean checkAudioPermission() {
        // Check if the permission has been granted
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAudioPermission() {
        // Request audio recording permission
        requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your audio recording logic
                startRecordingButton.setEnabled(false);
                stopRecordingButton.setEnabled(true);
                startRecording();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    // First time user denied microphone access, explain the importance of the permission
                    // and ask again
                    showPermissionExplanationDialog();
                } else {
                    // Open the app settings to let the user grant the permission manually
                    showAppSettingsRedirect();
                }
                showMessage("Record Audio Permissions Denied");
            }
        }
    }

    private void showAppSettingsRedirect() {
        new AlertDialog.Builder(this)
                .setTitle("Allow Permissions")
                .setMessage("Manually grant the permission.")
                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Request the permission again
                        openAppSettings();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Needed")
                .setMessage("The app needs audio recording permission to function properly. Please grant the permission.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Request the permission again
                        requestAudioPermission();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        ActivityCompat.startActivityForResult(this, intent, REQUEST_RECORD_AUDIO, null);
    }
    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
