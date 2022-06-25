package com.haritha.haritha_farmer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ReportCropsRevenueFragment extends Fragment {

    LineChart lineChart_reportCrop;
    DatabaseReference dbReferenceCrop;
    LineDataSet lineDataSet = new LineDataSet(null, null);
    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    LineData lineData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_crops_revenue, container, false);

        lineChart_reportCrop = view.findViewById(R.id.lineChart_reportCrop);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        dbReferenceCrop = FirebaseDatabase.getInstance().getReference("Farmer").child("Crop").child(currentUserID);

        retrieveData();

        return view;
    }

    private void retrieveData() {
        dbReferenceCrop.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> dataValues = new ArrayList<Entry>();

                if (snapshot.hasChildren()) {
                    for (DataSnapshot myDataSnapshot : snapshot.getChildren()) {
                        Crop cropData = myDataSnapshot.getValue(Crop.class);
                        dataValues.add(new Entry(cropData.getDays_to_maturity(), cropData.getEstimated_revenue()));
                    }
                    showChart(dataValues);
                } else {
                    lineChart_reportCrop.clear();
                    lineChart_reportCrop.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showChart(ArrayList<Entry> dataValues) {
        lineDataSet.setValues(dataValues);
        lineDataSet.setLabel("Crop Report");
        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);
        lineData = new LineData(iLineDataSets);
        lineChart_reportCrop.clear();
        lineChart_reportCrop.setData(lineData);
        lineChart_reportCrop.invalidate();
    }


}