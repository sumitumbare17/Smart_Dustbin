package com.example.Smart_Dustbin.collector;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Smart_Dustbin.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WasteVisualizationActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private BarChart mMonthlyBarChart;
    private BarChart mYearlyBarChart;
    private BarChart mWeeklyBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waste_visualization);
        Intent intent = getIntent();
        String dustId = intent.getStringExtra("dustbinId");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("dustbins").child(dustId);

        mMonthlyBarChart = findViewById(R.id.monthly_bar_chart);
        mYearlyBarChart = findViewById(R.id.yearly_bar_chart);
        mWeeklyBarChart = findViewById(R.id.weekly_bar_chart);

        fetchAndVisualizeData();
    }

    private void fetchAndVisualizeData() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Float> monthlyWeightList = new ArrayList<>();
                List<Float> yearlyWeightList = new ArrayList<>();
                List<Float> weeklyWeightList = new ArrayList<>();

                Calendar cal = Calendar.getInstance();

                // Monthly data
                for (int i = 0; i < 12; i++) {
                    monthlyWeightList.add(0f); // Initialize with 0
                }

                // Yearly data
                int currentYear = cal.get(Calendar.YEAR);
                for (int i = currentYear - 4; i <= currentYear; i++) {
                    yearlyWeightList.add(0f); // Initialize with 0
                }

                // Weekly data
                for (int i = 0; i < 7; i++) {
                    weeklyWeightList.add(0f); // Initialize with 0
                }

                for (DataSnapshot snapshot : dataSnapshot.child("readings").getChildren()) {
                    float weight = snapshot.child("weight").getValue(Float.class);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    try {
                        cal.setTime(dateFormat.parse(snapshot.getKey()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    // Monthly data
                    int month = cal.get(Calendar.MONTH);
                    monthlyWeightList.set(month, monthlyWeightList.get(month) + weight);

                    // Yearly data
                    int year = cal.get(Calendar.YEAR);
                    if (year >= currentYear - 4 && year <= currentYear) {
                        yearlyWeightList.set(year - (currentYear - 4), yearlyWeightList.get(year - (currentYear - 4)) + weight);
                    }

                    // Weekly data
                    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // Adjust for array index
                    weeklyWeightList.set(dayOfWeek, weeklyWeightList.get(dayOfWeek) + weight);
                }

                // Visualize data
                visualizeData(monthlyWeightList, mMonthlyBarChart, "Monthly Waste Generated (kg)");
                visualizeData(yearlyWeightList, mYearlyBarChart, "Yearly Waste Generated (kg)");
                visualizeData(weeklyWeightList, mWeeklyBarChart, "Weekly Waste Generated (kg)");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void visualizeData(List<Float> dataList, BarChart barChart, String label) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            entries.add(new BarEntry(i, dataList.get(i)));
        }
        BarDataSet dataSet = new BarDataSet(entries, label);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS); // Use predefined colors for better visualization
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // Customize appearance
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getXAxisValues(dataList.size())));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.animateY(1000);
        barChart.getLegend().setEnabled(false); // Disable legend
        barChart.invalidate();
    }

    private List<String> getXAxisValues(int dataSize) {
        // Return labels for X axis
        List<String> labels = new ArrayList<>();
        if (dataSize == 12) { // Monthly data
            labels.add("Jan");
            labels.add("Feb");
            labels.add("Mar");
            labels.add("Apr");
            labels.add("May");
            labels.add("Jun");
            labels.add("Jul");
            labels.add("Aug");
            labels.add("Sep");
            labels.add("Oct");
            labels.add("Nov");
            labels.add("Dec");
        } else if (dataSize == 5) { // Yearly data
            Calendar cal = Calendar.getInstance();
            int currentYear = cal.get(Calendar.YEAR);
            for (int i = currentYear - 4; i <= currentYear; i++) {
                labels.add(String.valueOf(i));
            }
        } else if (dataSize == 7) { // Weekly data
            labels.add("Sun");
            labels.add("Mon");
            labels.add("Tue");
            labels.add("Wed");
            labels.add("Thu");
            labels.add("Fri");
            labels.add("Sat");
        }
        return labels;
    }
}
