package com.example.Smart_Dustbin;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DustbinWasteDataActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;
    private DustbinDataAdapter mAdapter;
    private List<DustbinData> mDustbinDataList;
    private BarChart mBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dustbin_waste_data);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("dustbins");

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDustbinDataList = new ArrayList<>();
        mAdapter = new DustbinDataAdapter(this, mDustbinDataList);
        mRecyclerView.setAdapter(mAdapter);

        mBarChart = findViewById(R.id.bar_chart);

        fetchAndDisplayData();
    }

    private void fetchAndDisplayData() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dustbinSnapshot : dataSnapshot.getChildren()) {
                    String dustbinId = dustbinSnapshot.getKey();
                    float totalWaste = 0;
                    for (DataSnapshot readingSnapshot : dustbinSnapshot.child("readings").getChildren()) {
                        float weight = readingSnapshot.child("weight").getValue(Float.class);
                        totalWaste += weight;
                    }
                    DustbinData dustbinData = new DustbinData(dustbinId, totalWaste);
                    mDustbinDataList.add(dustbinData);
                }
                mAdapter.notifyDataSetChanged();
                visualizeData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void visualizeData() {
        List<String> dustbinIds = new ArrayList<>();
        List<Float> totalWasteList = new ArrayList<>();

        for (DustbinData dustbinData : mDustbinDataList) {
            dustbinIds.add(dustbinData.getDustbinId());
            totalWasteList.add(dustbinData.getTotalWaste());
        }

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < totalWasteList.size(); i++) {
            entries.add(new BarEntry(i, totalWasteList.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Total Waste Generated (kg)");
        BarData barData = new BarData(dataSet);

        mBarChart.setData(barData);
        mBarChart.getDescription().setEnabled(false);
        mBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dustbinIds));
        mBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mBarChart.getXAxis().setGranularity(1f);
        mBarChart.animateY(1000);
        mBarChart.invalidate();
    }
}
