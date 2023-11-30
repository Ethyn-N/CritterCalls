package com.example.crittercalls;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.crittercalls.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.tensorflow.lite.support.label.Category;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class ClassificationActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextView title;
    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;
    public final static int REQUEST_RECORD_AUDIO = 2033;
    protected ImageView animalImage;
    protected ArrayList<String> resultsList = new ArrayList<>();
    protected List<Category> statisticsList = new ArrayList<>();
    protected int counter = 1;
    protected TextView outputTextView;
    protected Button startRecordingButton;
    protected Button stopRecordingButton;
    private static final String RESULTS_LIST_KEY = "resultsList";
    private static final String COUNTER_KEY = "counter";
    private static final String STATISTICS_KEY = "statisticsList";
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

        // Retrieve the resultsList from savedInstanceState or SharedPreferences
        if (savedInstanceState != null) {
            resultsList = savedInstanceState.getStringArrayList(RESULTS_LIST_KEY);
            counter = savedInstanceState.getInt(COUNTER_KEY, 1);
        } else {
            Pair<ArrayList<String>, Integer> data = getResultsListAndCounterFromSharedPreferences();
            if (data != null) {
                resultsList = data.first;
                counter = data.second;
            }
            else {
                initializeData();
            }
        }

        backButton = findViewById(R.id.back_btn);
        title = findViewById(R.id.toolbar_title);
        title.setText("Animal Classification");

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if(item.getItemId() == R.id.menu_classification) {
                if(oldFragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(oldFragment).commit();
                }
                return true;
            }
            else if(item.getItemId() == R.id.menu_statistics) {
                ParcelableCategory parcelableCategory = new ParcelableCategory(statisticsList);
                oldFragment = new StatsFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(STATISTICS_KEY, parcelableCategory);
                oldFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.frameLayout, oldFragment);
                fragmentTransaction.commit();
                return true;
            }
            else if (item.getItemId() == R.id.menu_list) {
                oldFragment = new ListFragment();

                Bundle bundle = new Bundle();
                bundle.putStringArrayList(RESULTS_LIST_KEY, resultsList);
                oldFragment.setArguments(bundle);

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
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the resultsList to the instance state
        outState.putStringArrayList(RESULTS_LIST_KEY, resultsList);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Save the resultsList to SharedPreferences before the activity is destroyed
        saveResultsListAndCounterToSharedPreferences(resultsList, counter);
    }

    // Initialize the resultsList and counter for the first time when using the app
    private void initializeData() {
        resultsList.clear();
        counter = 1;
        saveResultsListAndCounterToSharedPreferences(resultsList, counter);
    }
    public void saveResultsListAndCounterToSharedPreferences(ArrayList<String> resultsList, int counter) {
        // Save the resultsList and counter to SharedPreferences as a JSON string
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String resultsListJson = gson.toJson(resultsList);

        editor.putString(RESULTS_LIST_KEY, resultsListJson);
        editor.putInt(COUNTER_KEY, counter);
        editor.apply();
    }
    protected Pair<ArrayList<String>, Integer> getResultsListAndCounterFromSharedPreferences() {
        // Retrieve the resultsList and counter from SharedPreferences and convert them back from JSON
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        String resultsListJson = preferences.getString(RESULTS_LIST_KEY, null);
        int counter = preferences.getInt(COUNTER_KEY, 1);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> resultsList = gson.fromJson(resultsListJson, type);

        // Check if resultsList is null, and return null in that case
        if (resultsList == null) {
            return null;
        }

        return new Pair<>(resultsList, counter);
    }
    private void addListeners() {
        backButton.setOnClickListener(v -> {
            saveResultsListAndCounterToSharedPreferences(resultsList, counter);
            Intent redirectToHome = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(redirectToHome);
            finish();
        });
    }

    protected void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}