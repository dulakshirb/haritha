package com.haritha.haritha_farmer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ReportsFragment extends Fragment {

    private ImageView img_rCrops;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        img_rCrops = view.findViewById(R.id.img_rCrops);

        img_rCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment reportCropFragment = new ReportCropFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, reportCropFragment).addToBackStack(null).commit();
            }
        });

        return view;
    }
}