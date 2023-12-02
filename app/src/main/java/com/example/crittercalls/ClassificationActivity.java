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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.crittercalls.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.tensorflow.lite.support.label.Category;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private String userID;
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

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();
        userID = firebaseAuth.getCurrentUser().getUid();

        stopRecordingButton.setEnabled(false);

        loadResultsListAndCountFromFirebase();

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
    protected void onDestroy() {
        super.onDestroy();

        saveResultsListAndCounterToFirebase();
    }
    protected void saveResultsListAndCounterToFirebase() {
        DocumentReference documentReference = firestore.collection("users").document(user.getUid());

        // Convert resultsList to a JSON string
        Gson gson = new Gson();
        String resultsListJson = gson.toJson(resultsList);

        Map<String, Object> data = new HashMap<>();
        data.put("resultsList", resultsListJson);
        data.put("counter", counter);
        documentReference.update(data);
    }
    protected void loadResultsListAndCountFromFirebase() {
        DocumentReference documentReference = firestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException e) {
                if (value.exists()) {
                    // Retrieve the resultsList as a JSON string
                    String resultsListJson = (String) value.get("resultsList");

                    // Deserialize the JSON string to an ArrayList<String> using Gson
                    if (resultsListJson != null && !resultsListJson.isEmpty()) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
                        resultsList = gson.fromJson(resultsListJson, listType);
                    } else {
                        resultsList = new ArrayList<>();
                    }

                    // Retrieve the counter as Long and convert to int
                    Long counterLong = (Long) value.get("counter");
                    counter = counterLong != null ? counterLong.intValue() : 0;
                }
                else {
                    resultsList.clear();
                    counter = 1;
                }
            }
        });
    }
    private void addListeners() {
        backButton.setOnClickListener(v -> {
            saveResultsListAndCounterToFirebase();
            Intent redirectToHome = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(redirectToHome);
            finish();
        });
    }
    protected void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}