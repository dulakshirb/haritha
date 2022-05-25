package com.haritha.haritha_farmer;


import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CropsAddFragment extends Fragment {

    private AppCompatButton plantedDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private EditText cropName, variety, botanicalName, daysToEmerge, plantSpacing, rowSpacing, plantingDepth, plantingDetails, daysToMaturity, harvestWindow, estimatedRevenue, expectedYield;
    private Spinner cropType, startMethod, lightProfile, harvestUnit;
    private Button saveCrop;
    private String planteddate, today, crop_type, crop_name, planted_date, crop_variety, botanical_name, start_method, light_profile, planting_details, harvest_unit;
    private int days_to_emerge, days_to_maturity, harvest_window;
    private Float plant_spacing, row_spacing, planting_depth, estimated_revenue, expected_yield;

    DatabaseReference databaseReference;
    Crops crops;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crops_add, container, false);

        plantedDate = (AppCompatButton) view.findViewById(R.id.calendar_plantedDate);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        today = mdformat.format(calendar.getTime());
        plantedDate.setText(today);

        plantedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, dateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSer: yyyy-mm-dd: " + year + "/" + month + "/" + day);
                planteddate = year + "-" + month + "-" + day;
                plantedDate.setText(planteddate);
            }
        };

        cropName = (EditText) view.findViewById(R.id.txt_cropName);
        variety = (EditText) view.findViewById(R.id.txt_variety);
        botanicalName = (EditText) view.findViewById(R.id.txt_botanicalName);
        daysToEmerge = (EditText) view.findViewById(R.id.txt_daysToEmerge);
        plantSpacing = (EditText) view.findViewById(R.id.txt_plantSpacing);
        rowSpacing = (EditText) view.findViewById(R.id.txt_rowSpacing);
        plantingDepth = (EditText) view.findViewById(R.id.txt_plantingDepth);
        plantingDetails = (EditText) view.findViewById(R.id.txt_plantingDetails);
        daysToMaturity = (EditText) view.findViewById(R.id.txt_daysToMaturity);
        harvestWindow = (EditText) view.findViewById(R.id.txt_harvestWindow);
        estimatedRevenue = (EditText) view.findViewById(R.id.txt_estimatedRevenue);
        expectedYield = (EditText) view.findViewById(R.id.txt_expectedYield);
        cropType = (Spinner) view.findViewById(R.id.spinner_cropType);
        startMethod = (Spinner) view.findViewById(R.id.spinner_startMethod);
        lightProfile = (Spinner) view.findViewById(R.id.spinner_lightProfile);
        harvestUnit = (Spinner) view.findViewById(R.id.spinner_harvestUnit);
        saveCrop = (Button) view.findViewById(R.id.btn_saveCrop);

        crops = new Crops();
        databaseReference = FirebaseDatabase.getInstance().getReference("Farmer").child("crops");

        saveCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(cropName.getText().toString().trim())) {
                    cropName.setError("This field is required");
                    cropName.requestFocus();
                } else {
                    setFieldValues();
                    databaseReference.push().setValue(crops);
                }
            }
        });

        return view;
    }

    private void setFieldValues() {
        fieldValidation();
        crops.setCropType(crop_type);
        crops.setCropName(crop_name);
        crops.setVariety(crop_variety);
        crops.setBotanicalName(botanical_name);
        crops.setPlantedDate(planted_date);
        crops.setDaysToEmerge(days_to_emerge);
        crops.setPlantSpacing(plant_spacing);
        crops.setRowSpacing(row_spacing);
        crops.setPlantingDepth(planting_depth);
        crops.setStartMethod(start_method);
        crops.setLightProfile(light_profile);
        crops.setPlantingDetails(planting_details);
        crops.setDaysToMaturity(days_to_maturity);
        crops.setHarvestWindow(harvest_window);
        crops.setHarvestUnit(harvest_unit);
        crops.setEstimatedRevenue(estimated_revenue);
        crops.setExpectedYield(expected_yield);
    }

    private void fieldValidation() {
        //crop type
        crop_type = cropType.getSelectedItem().toString();

        //crop name
        crop_name = cropName.getText().toString().trim();

        //crop variety
        if (crop_variety == null || crop_variety.isEmpty() || crop_variety.trim().isEmpty()) {
            crop_variety = "Unknown";
        } else
            crop_variety = variety.getText().toString().trim();

        //botanical Name
        if (botanical_name == null || botanical_name.isEmpty() || botanical_name.trim().isEmpty()) {
            botanical_name = "Unknown";
        } else
            botanical_name = botanicalName.getText().toString().trim();

        //planted date
        if (planteddate == null || planteddate.isEmpty() || planteddate.trim().isEmpty())
            planted_date = today;
        else
            planted_date = planteddate;

        //start method
        start_method = startMethod.getSelectedItem().toString();

        //light profile
        light_profile = lightProfile.getSelectedItem().toString();

        //planting details
        planting_details = plantingDetails.getText().toString().trim();

        //harvest unit
        harvest_unit = harvestUnit.getSelectedItem().toString();

        //days to emerge
        if (daysToEmerge.length() > 0) {
            days_to_emerge = Integer.parseInt(daysToEmerge.getText().toString().trim());
        } else
            days_to_emerge = 0;

        //days to maturity
        if (daysToMaturity.length() > 0) {
            days_to_maturity = Integer.parseInt(daysToMaturity.getText().toString());
        } else
            days_to_maturity = 0;

        //harvest window
        if (harvestWindow.length() > 0) {
            harvest_window = Integer.parseInt(harvestWindow.getText().toString().trim());
        } else
            harvest_window = 0;

        //plant spacing
        if (plantSpacing.length() > 0) {
            plant_spacing = Float.parseFloat(plantSpacing.getText().toString());
        } else
            plant_spacing = Float.valueOf(0);

        //row spacing
        if (rowSpacing.length() > 0) {
            row_spacing = Float.parseFloat(rowSpacing.getText().toString());
        } else
            row_spacing = Float.valueOf(0);

        //planting depth
        if (plantingDepth.length() > 0) {
            planting_depth = Float.parseFloat(plantingDepth.getText().toString());
        } else
            planting_depth = Float.valueOf(0);

        //estimated revenue
        if (estimatedRevenue.length() > 0) {
            estimated_revenue = Float.parseFloat(estimatedRevenue.getText().toString());
        } else
            estimated_revenue = Float.valueOf(0);

        //expected yield
        if (expectedYield.length() > 0) {
            expected_yield = Float.parseFloat(expectedYield.getText().toString());
        } else
            expected_yield = Float.valueOf(0);
    }
}