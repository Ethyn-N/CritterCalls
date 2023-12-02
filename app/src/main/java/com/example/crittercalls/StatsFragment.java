package com.example.crittercalls;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private ImageButton backButton;
    private TextView title;
    private BarChart barChart;

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_stats, container, false);

        backButton = (ImageButton) view.findViewById(R.id.back_btn);
        title = view.findViewById(R.id.toolbar_title);
        title.setText("Classification Stats");
        barChart = view.findViewById(R.id.barChart);

        // Retrieve the statisticsList from the bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            ParcelableCategory parcelableCategory = (ParcelableCategory) bundle.getParcelable("statisticsList");
            if (parcelableCategory != null) {
                List<String> categoryLabels = processCategoryLabels(parcelableCategory.getCategoryLabels());
                List<Float> categoryScores = parcelableCategory.getCategoryScores();

                showChart(categoryLabels, categoryScores);
            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
    private List<String> processCategoryLabels(List<String> originalLabels) {
        List<String> processedLabels = new ArrayList<>();

        for (String label : originalLabels) {
            // Cut off the label at the position of the comma or opening parenthesis (if present)
            int commaIndex = label.indexOf(",");
            int parenthesisIndex = label.indexOf("(");

            if (commaIndex != -1 && parenthesisIndex != -1) {
                // Cut off at the position of the comma or opening parenthesis
                int cutOffIndex = Math.min(commaIndex, parenthesisIndex);
                processedLabels.add(label.substring(0, cutOffIndex));
            }
            else if (commaIndex != -1) {
                processedLabels.add(label.substring(0, commaIndex));
            }
            else if (parenthesisIndex != -1) {
                processedLabels.add(label.substring(0, parenthesisIndex));
            }
            else {
                processedLabels.add(label);
            }
        }

        return processedLabels;
    }

    private void showChart(List<String> categoryLabels, List<Float> categoryScores) {
        if (categoryLabels.size() == categoryScores.size()) {
            barChart.setDrawBarShadow(false);
            barChart.setDrawValueAboveBar(true);
            barChart.getDescription().setEnabled(false);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(categoryLabels));
            xAxis.setLabelCount(categoryLabels.size());
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(true);
            xAxis.setGranularity(1f);

            List<Integer> barColors = generateRandomColors(categoryLabels.size()); // Generate a list of random colors
            BarDataSet dataSet = new BarDataSet(getEntries(categoryScores), "Probability");
            dataSet.setColors(barColors); // Set the colors for each bar
            BarData barData = new BarData(dataSet);

            barData.setBarWidth(0.4f);

            barChart.setData(barData);

            // Adjust the axis scale and granularity
            barChart.getAxisRight().setEnabled(false);
            barChart.getAxisLeft().setAxisMinimum(0f);
            barChart.getAxisLeft().setAxisMaximum(1.1f);
            barChart.getAxisLeft().setGranularity(0.1f);

            Legend legend = barChart.getLegend();
            legend.setEnabled(false);

            barChart.invalidate();
        }
    }
    private List<Integer> generateRandomColors(int count) {
        List<Integer> colors = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            colors.add(color);
        }

        return colors;
    }
    private List<BarEntry> getEntries(List<Float> categoryScores) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < categoryScores.size(); i++) {
            entries.add(new BarEntry(i, categoryScores.get(i)));
        }
        return entries;
    }
}