package com.example.Smart_Dustbin.collector;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("dustbins").child("26");

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

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Calendar cal = Calendar.getInstance();
                Date today = cal.getTime();

                // Find the date of the last Sunday
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                cal.add(Calendar.DATE, -7);
                Date lastSunday = cal.getTime();

                for (DataSnapshot snapshot : dataSnapshot.child("readings").getChildren()) {
                    Date readingDate = null;
                    try {
                        readingDate = dateFormat.parse(snapshot.getKey());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (readingDate != null) {
                        float weight = snapshot.child("weight").getValue(Float.class);

                        // Weekly data (from last Sunday to Saturday of this week)
                        if (readingDate.after(lastSunday) && readingDate.before(today)) {
                            weeklyWeightList.add(weight);
                        }

                        // Monthly data
                        cal.setTime(readingDate);
                        int month = cal.get(Calendar.MONTH);
                        if (monthlyWeightList.size() <= month) {
                            monthlyWeightList.add(weight);
                        } else {
                            monthlyWeightList.set(month, monthlyWeightList.get(month) + weight);
                        }

                        // Yearly data
                        int year = cal.get(Calendar.YEAR) % 2000; // Keep only last two digits of the year
                        if (yearlyWeightList.size() <= year) {
                            yearlyWeightList.add(weight);
                        } else {
                            yearlyWeightList.set(year, yearlyWeightList.get(year) + weight);
                        }
                    }
                }

                // Calculate averages
                float weeklyAverageWeight = calculateAverage(weeklyWeightList);
                for (int i = 0; i < monthlyWeightList.size(); i++) {
                    monthlyWeightList.set(i, monthlyWeightList.get(i) / 30f); // Assuming average weight per day
                }
                for (int i = 0; i < yearlyWeightList.size(); i++) {
                    yearlyWeightList.set(i, yearlyWeightList.get(i) / 365f); // Assuming average weight per day
                }

                // Visualize data
                visualizeData(monthlyWeightList, mMonthlyBarChart, "Average Monthly Waste Generated (kg)");
                visualizeData(yearlyWeightList, mYearlyBarChart, "Average Yearly Waste Generated (kg)");
                visualizeData(weeklyWeightList, mWeeklyBarChart, "Average Weekly Waste Generated (kg)");
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
        barChart.getLegend().setEnabled(true); // Enable legend
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
            int currentYear = cal.get(Calendar.YEAR) % 2000; // Keep only last two digits of the year
            for (int i = 0; i < dataSize; i++) {
                labels.add(String.valueOf(currentYear - i));
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

    private float calculateAverage(List<Float> dataList) {
        float sum = 0f;
        for (Float data : dataList) {
            sum += data;
        }
        return sum / dataList.size();
    }
}
