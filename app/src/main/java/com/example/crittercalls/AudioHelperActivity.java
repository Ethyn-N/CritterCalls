package com.example.crittercalls;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioHelperActivity extends ClassificationActivity {
    private AudioClassifier audioClassifier;
    private static final String MODEL_FILE = "yamnet_classification.tflite";
    private String[] permissions = {android.Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final float MINIMUM_DISPLAY_THRESHOLD = 0.3f;
    private AudioRecord audioRecord;
    private TensorAudio audioTensor;
    private Map<String, Integer> categoryPictureMap;
    private String[] animals =
            {"Dog", "Cat", "Bird", "Livestock, farm animals, working animals",
            "Horse", "Cattle, bovinae", "Pig", "Goat", "Sheep", "Fowl", "Chicken, rooster",
            "Turkey", "Duck", "Goose", "Roaring cats (lions, tigers)", "Pigeon, dove",
            "Crow", "Owl", "Canidae, dogs, wolves", "Rodents, rats, mice", "Mouse",
            "Insect", "Cricket", "Mosquito", "Fly, housefly", "Bee, wasp, etc.", "Frog", "Snake",
                    "Whale vocalization"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeCategoryPictureMap();

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
            // Permission is already granted, proceed with audio recording logic
            startRecordingButton.setEnabled(false);
            stopRecordingButton.setEnabled(true);
            startRecording();
        } else {
            // Permission has not been granted yet, request it
            requestAudioPermission();
            showMessage("Requesting Permissions");
        }
    }
    private void startRecording() {
        // Start recording
        AudioRecord audioRecord = audioClassifier.createAudioRecord();
        audioRecord.startRecording();
        this.audioRecord = audioRecord;
    }
    @Override
    public void onStopRecording(View view) {
        startRecordingButton.setEnabled(true);
        stopRecordingButton.setEnabled(false);

        // Stop recording and release resources
        audioRecord.stop();

        // Start classification logic
        classifyAudio();

        audioRecord.release();
    }

    private void classifyAudio() {
        audioTensor.load(audioRecord);

        List<Classifications> output = audioClassifier.classify(audioTensor);

        // Filtering out classifications with low probability
        List<Category> finalOutput = new ArrayList<>();
        for (Classifications classifications : output) {
            for (Category category : classifications.getCategories()) {
                if (category.getScore() > MINIMUM_DISPLAY_THRESHOLD) {
                    finalOutput.add(category);
                }
            }
        }

        Category highestProbabilityCategory = null;
        float highestProbability = 0;

        // Finding the category with the highest probability
        for (Category category : finalOutput) {
            for (String animal : animals) {
                if (category.getLabel().equals(animal)) {
                    float currentProbability = category.getScore();
                    if (currentProbability > highestProbability) {
                        highestProbability = currentProbability;
                        highestProbabilityCategory = category;
                    }
                }
            }
        }

        // If highestProbability found, at it to the animalOutput
        List<Category> animalOutput = new ArrayList<>();
        if (highestProbability > MINIMUM_DISPLAY_THRESHOLD) {
            animalOutput.add(highestProbabilityCategory);
        }

        // Sorting the results
        Collections.sort(finalOutput, (o1, o2) -> (int) (o1.getScore() - o2.getScore()));

        // Send the finalOutput statistics to the statistics fragment
        statisticsList = finalOutput;

        // Initialize resultsList and counter
        loadResultsListAndCountFromFirebase();

        // Format the animalOutput to an outputStr
        StringBuilder outputStr = new StringBuilder();
        if (!animalOutput.isEmpty()) {
            Category firstCategory = animalOutput.get(0);
            outputStr.append(firstCategory.getLabel())
                    .append(": ").append(firstCategory.getScore());
            outputStr.insert(0, counter + ". ");
            counter++;
        }

        // If animal class could not be found, output "Could not classify".
        // Else, output the animal class result with its picture and add it to the resultsList.
        if (animalOutput.isEmpty()) {
            outputTextView.setText("Could not classify");
            Picasso.get().load(getPictureResourceId("Nothing")).into(animalImage);
        } else {
            resultsList.add(outputStr.toString());
            int pictureResourceId = getPictureResourceId(highestProbabilityCategory.getLabel());
            Picasso.get().load(pictureResourceId).into(animalImage);
            outputTextView.setText(outputStr.toString());
        }

        // Save resultsList and counter to Firebase
        saveResultsListAndCounterToFirebase();
    }
    private void initializeCategoryPictureMap() {
        categoryPictureMap = new HashMap<>();
        categoryPictureMap.put("Nothing", R.drawable.confused);
        categoryPictureMap.put("Cat", R.drawable.cat);
        categoryPictureMap.put("Dog", R.drawable.dog);
        categoryPictureMap.put("Pig", R.drawable.pig);
        categoryPictureMap.put("Bird", R.drawable.bird);
        categoryPictureMap.put("Livestock, farm animals, working animals", R.drawable.livestock);
        categoryPictureMap.put("Horse", R.drawable.horse);
        categoryPictureMap.put("Cattle, bovinae", R.drawable.cow);
        categoryPictureMap.put("Goat", R.drawable.goat);
        categoryPictureMap.put("Sheep", R.drawable.sheep);
        categoryPictureMap.put("Fowl", R.drawable.fowl);
        categoryPictureMap.put("Chicken, rooster", R.drawable.chicken);
        categoryPictureMap.put("Turkey", R.drawable.turkey);
        categoryPictureMap.put("Duck", R.drawable.duck);
        categoryPictureMap.put("Goose", R.drawable.goose);
        categoryPictureMap.put("Roaring cats (lions, tigers)", R.drawable.bigcat);
        categoryPictureMap.put("Pigeon, dove", R.drawable.pigeon);
        categoryPictureMap.put("Crow", R.drawable.crow);
        categoryPictureMap.put("Owl", R.drawable.owl);
        categoryPictureMap.put("Canidae, dogs, wolves", R.drawable.wolf);
        categoryPictureMap.put("Rodents, rats, mice", R.drawable.rodent);
        categoryPictureMap.put("Mouse", R.drawable.mouse);
        categoryPictureMap.put("Insect", R.drawable.insect);
        categoryPictureMap.put("Cricket", R.drawable.cricket);
        categoryPictureMap.put("Mosquito", R.drawable.mosquito);
        categoryPictureMap.put("Fly, housefly", R.drawable.fly);
        categoryPictureMap.put("Bee, wasp, etc.", R.drawable.bee);
        categoryPictureMap.put("Frog", R.drawable.frog);
        categoryPictureMap.put("Snake", R.drawable.snake);
        categoryPictureMap.put("Whale vocalization", R.drawable.whale);
    }
    private int getPictureResourceId(String categoryLabel) {
        // Make sure the map is initialized
        if (categoryPictureMap == null) {
            initializeCategoryPictureMap();
        }

        // Get the corresponding picture resource ID from the map
        Integer pictureResourceId = categoryPictureMap.get(categoryLabel);

        // If the label is not found in the map, provide a default picture resource ID
        if (pictureResourceId == null) {
            pictureResourceId = R.drawable.default_picture;
        }

        return pictureResourceId;
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with audio recording logic
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
    protected void onDestroy() {
        super.onDestroy();
    }
}
