package com.haritha.haritha_farmer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ReportCropFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_crop, container, false);

        Button btn_viewRevenueReport = view.findViewById(R.id.btn_viewRevenueReport);
        Button btn_viewCropTypeReport = view.findViewById(R.id.btn_viewCropTypeReport);

        btn_viewRevenueReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment reportCropsRevenueFragment = new ReportCropsRevenueFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, reportCropsRevenueFragment).addToBackStack(null).commit();
            }
        });

        return view;
    }
}