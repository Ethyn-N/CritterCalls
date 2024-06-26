package com.example.crittercalls;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View view;
    private ImageButton backButton;
    private Button clearHistoryButton;
    private ArrayList<String> resultsList = new ArrayList<>();
    private static final String RESULTS_LIST_KEY = "resultsList";
    private static final String COUNTER_KEY = "counter";
    protected int counter = 1;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private String userID;
    private TextView title;
    private String mParam1;
    private String mParam2;

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        // Save the resultsList to the instance state
//        outState.putStringArrayList(RESULTS_LIST_KEY, resultsList);
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
//        if (savedInstanceState != null) {
//            // Restore the resultsList from the saved instance state
//            resultsList = savedInstanceState.getStringArrayList(RESULTS_LIST_KEY);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list, container, false);

        backButton = view.findViewById(R.id.back_btn);
        title = view.findViewById(R.id.toolbar_title);
        title.setText("Animal List");

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();
        userID = firebaseAuth.getCurrentUser().getUid();

        clearHistoryButton = view.findViewById(R.id.clearHistoryButton);
        clearHistoryButton.setVisibility(View.INVISIBLE);

        clearHistoryButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("Clear History");
            builder.setMessage("Are you sure you want to clear your classification history?");
            builder.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clearHistory();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog mDialog = builder.create();
            mDialog.show();
        });

        loadResultsListAndCountFromFirebase();

//        showMessage(counter + "");


//            // Display the numbered list
//            StringBuilder numberedList = new StringBuilder();
//            for (int i = 0; i < resultsList.size(); i++) {
//                numberedList.append((i + 1)).append(". ").append(resultsList.get(i)).append("\n");
//            }

//            ListView listView = view.findViewById(R.id.fragment_listView);
//
//            // Create an ArrayAdapter to populate data into the ListView
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, resultsList);
//
//            // Set the adapter to the ListView
//            listView.setAdapter(adapter);


//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            resultsList = bundle.getStringArrayList(RESULTS_LIST_KEY);
//
//            // Display the numbered list
//            StringBuilder numberedList = new StringBuilder();
//            for (int i = 0; i < resultsList.size(); i++) {
//                numberedList.append((i + 1)).append(". ").append(resultsList.get(i)).append("\n");
//            }
//
//            ListView listView = view.findViewById(R.id.fragment_listView);
//
//            // Create an ArrayAdapter to populate data into the ListView
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, resultsList);
//
//            // Set the adapter to the ListView
//            listView.setAdapter(adapter);
//        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
//                saveResultsListAndCounterToSharedPreferences(resultsList, counter);
//                saveResultsListAndCounterToFirebase();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
    private void clearHistory() {
        resultsList = new ArrayList<>();
        counter = 1;

        saveResultsListAndCounterToFirebase();

        loadResultsListAndCountFromFirebase();

        showMessage("History cleared");
    }
    public void saveResultsListAndCounterToSharedPreferences(ArrayList<String> resultsList, int counter) {
        // Save the resultsList and counter to SharedPreferences as a JSON string
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String resultsListJson = gson.toJson(resultsList);

        editor.putString(RESULTS_LIST_KEY, resultsListJson);
        editor.putInt(COUNTER_KEY, counter);
        editor.apply();
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

    public void loadResultsListAndCountFromFirebase() {
        DocumentReference documentReference = firestore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    // Retrieve the resultsList as a JSON string
                    String resultsListJson = (String) value.get("resultsList");

                    // Deserialize the JSON string to an ArrayList<String> using Gson
                    if (resultsListJson != null && !resultsListJson.isEmpty()) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
                        resultsList = gson.fromJson(resultsListJson, listType);
                        updateListView();
                    } else {
                        resultsList = new ArrayList<>();
                        counter = 1;
                    }

                    // Retrieve the counter as Long and convert to int
                    Long counterLong = (Long) value.get("counter");
                    counter = counterLong != null ? counterLong.intValue() : 0;
                    if (counter > 1) {
                        clearHistoryButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        clearHistoryButton.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    resultsList.clear();
                    counter = 1;
                }
            }
        });
    }
    private void updateListView() {
        ListView listView = view.findViewById(R.id.fragment_listView);

        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, resultsList);
            listView.setAdapter(adapter);
        }
    }
    private void showMessage(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }
}