package com.haritha.haritha_farmer;


import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CropsAddFragment extends Fragment {

    private View view;
    private AppCompatButton plantedDate;
    private EditText cropName, variety, botanicalName, daysToEmerge, plantSpacing, rowSpacing, plantingDepth, plantingDetails, daysToMaturity, harvestWindow, estimatedRevenue, expectedYield;
    private Spinner cropType, startMethod, lightProfile, harvestUnit;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Button btnAddCrop;
    private ProgressDialog loadingBar;

    private String today, planteddate, currentUserId, crop_type, crop_name, crop_variety, botanical_name, planted_date,
            start_method, light_profile, planting_details, harvest_unit;
    private int days_to_emerge, days_to_maturity, harvest_window;
    private Float plant_spacing, row_spacing, planting_depth, estimated_revenue, expected_yield;
    private FirebaseAuth mAuth;
    private DatabaseReference dbCrop;

    public CropsAddFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_crops_add, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        dbCrop = FirebaseDatabase.getInstance().getReference("Farmer").child("Crop").child(currentUserId);

        initializeFields();
        selectPlantedDate();

        btnAddCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addCrop();
            }
        });

        return view;
    }

    private void initializeFields() {
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
        btnAddCrop = (Button) view.findViewById(R.id.btn_saveCrop);
        loadingBar = new ProgressDialog(getActivity());
    }

    private void selectPlantedDate() {
        //Set current date by default
        plantedDate = (AppCompatButton) view.findViewById(R.id.calendar_plantedDate);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        today = dateFormat.format(calendar.getTime());
        plantedDate.setText(today);

        //open DatePickerDialog
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

        //Set Selected Date
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSer: yyyy-mm-dd: " + year + "/" + month + "/" + day);
                planteddate = year + "-" + month + "-" + day;
                plantedDate.setText(planteddate);
            }
        };
    }

    private void sendUsertoCropsFragment() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment cropsFragment = new CropsFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, cropsFragment).addToBackStack(null).commit();
    }

    private void addCrop() {

        crop_type = cropType.getSelectedItem().toString();
        crop_name = cropName.getText().toString().trim();
        days_to_emerge = 0;
        days_to_maturity = 0;
        harvest_window = 0;
        plant_spacing = Float.valueOf(0);
        row_spacing = Float.valueOf(0);
        planting_depth = Float.valueOf(0);
        estimated_revenue = Float.valueOf(0);
        expected_yield = Float.valueOf(0);
        start_method = startMethod.getSelectedItem().toString();
        light_profile = lightProfile.getSelectedItem().toString();
        harvest_unit = harvestUnit.getSelectedItem().toString();


        if (TextUtils.isEmpty(crop_name)) {
            cropName.setError("This field is required!");
            cropName.requestFocus();
            return;
        }

        if (crop_variety == null || crop_variety.isEmpty() || crop_variety.trim().isEmpty())
            crop_variety = "Unknown";
        if (botanical_name == null || botanical_name.isEmpty() || botanical_name.trim().isEmpty())
            botanical_name = "Unknown";
        if (planting_details == null || planting_details.isEmpty() || planting_details.trim().isEmpty())
            planting_details = "N/a";
        if (daysToEmerge.length() > 0)
            days_to_emerge = Integer.parseInt(daysToEmerge.getText().toString().trim());
        if (daysToMaturity.length() > 0)
            days_to_maturity = Integer.parseInt(daysToMaturity.getText().toString().trim());
        if (harvestWindow.length() > 0)
            harvest_window = Integer.parseInt(harvestWindow.getText().toString().trim());
        if (plantSpacing.length() > 0)
            plant_spacing = Float.parseFloat(plantSpacing.getText().toString().trim());
        if (rowSpacing.length() > 0)
            row_spacing = Float.parseFloat(rowSpacing.getText().toString().trim());
        if (plantingDepth.length() > 0)
            planting_depth = Float.parseFloat(plantingDepth.getText().toString().trim());
        if (estimatedRevenue.length() > 0)
            estimated_revenue = Float.parseFloat(estimatedRevenue.getText().toString().trim());
        if (expectedYield.length() > 0)
            expected_yield = Float.parseFloat(expectedYield.getText().toString().trim());
        crop_variety = variety.getText().toString().trim();
        botanical_name = botanicalName.getText().toString().trim();
        planting_details = plantingDetails.getText().toString().trim();
        if (planteddate == null || planteddate.isEmpty() || planteddate.trim().isEmpty())
            planted_date = today;
        else
            planted_date = planteddate;

        loadingBar.setTitle("Please wait");
        loadingBar.setMessage("Adding...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        String crop_id = dbCrop.push().getKey();
        Crop crop = new Crop(crop_id, crop_type, crop_name, crop_variety, botanical_name, planted_date,
                start_method, light_profile, planting_details, days_to_emerge, harvest_unit, days_to_maturity,
                harvest_window, plant_spacing, row_spacing, planting_depth, estimated_revenue, expected_yield);

        dbCrop.child(crop_id).setValue(crop);
        sendUsertoCropsFragment();
        loadingBar.dismiss();

    }

}