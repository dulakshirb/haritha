package com.haritha.haritha_farmer;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ReportCropsTypeFragment extends Fragment {

    PieChart pieChart_reportCropType;
    int[] colorClassArray = new int[]{Color.rgb(45, 94, 92), Color.rgb(232, 117, 53), Color.rgb(252, 204, 121), Color.rgb(212, 186, 185), Color.rgb(108, 108, 172), Color.rgb(44, 68, 76)};
    DatabaseReference dbReferenceCrop;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_crops_type, container, false);

        pieChart_reportCropType = view.findViewById(R.id.pieChart_reportCropType);

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
                ArrayList<PieEntry> dataValues = new ArrayList<>();

                if(snapshot.hasChildren()){
                    for (DataSnapshot myDataSnapshot : snapshot.getChildren()){
                        Crop cropData = myDataSnapshot.getValue(Crop.class);
                        dataValues.add(new PieEntry(cropData.getEstimated_revenue(), cropData.getCrop_type()));
                    }
                    showChart(dataValues);
                }else {
                    pieChart_reportCropType.clear();
                    pieChart_reportCropType.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showChart(ArrayList<PieEntry> dataValues) {
        PieDataSet pieDataSet = new PieDataSet(dataValues, "");
        pieDataSet.setColors(colorClassArray);

        pieChart_reportCropType.setDrawEntryLabels(true);
        pieChart_reportCropType.setUsePercentValues(false);
        pieChart_reportCropType.setCenterText("Crop Types");
        pieChart_reportCropType.setCenterTextSize(16);
        pieChart_reportCropType.setCenterTextRadiusPercent(50);
        pieChart_reportCropType.setHoleRadius(30);
        pieChart_reportCropType.setTransparentCircleRadius(40);
        pieChart_reportCropType.setTransparentCircleColor(Color.RED);
        pieChart_reportCropType.setTransparentCircleAlpha(50);

        PieData pieData = new PieData(pieDataSet);
        pieChart_reportCropType.setData(pieData);
        pieChart_reportCropType.invalidate();
    }

}