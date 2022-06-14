package com.haritha.haritha_farmer;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class CropsAddFragment extends Fragment {

    private View view;
    private AppCompatButton calendar_plantedDate;
    private EditText txt_cropName, txt_variety, txt_botanicalName, txt_daysToEmerge, txt_plantSpacing, txt_rowSpacing,
            txt_plantingDepth, txt_plantingDetails, txt_daysToMaturity, txt_harvestWindow, txt_estimatedRevenue, txt_expectedYield;
    private Spinner spinner_cropType, spinner_startMethod, spinner_lightProfile, spinner_harvestUnit;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Button btn_saveCrop;
    private ProgressDialog loadingBar;

    private String planted_date;
    private DatabaseReference dbReferenceCrop;

    public CropsAddFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_crops_add, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId;
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        dbReferenceCrop = FirebaseDatabase.getInstance().getReference("Farmer").child("Crop").child(currentUserId);

        initializeFields();
        selectPlantedDate();

        btn_saveCrop.setOnClickListener(view -> addCrop());

        return view;
    }

    private void initializeFields() {
        txt_cropName = view.findViewById(R.id.txt_cropName);
        txt_variety = view.findViewById(R.id.txt_variety);
        txt_botanicalName = view.findViewById(R.id.txt_botanicalName);
        txt_daysToEmerge = view.findViewById(R.id.txt_daysToEmerge);
        txt_plantSpacing = view.findViewById(R.id.txt_plantSpacing);
        txt_rowSpacing = view.findViewById(R.id.txt_rowSpacing);
        txt_plantingDepth = view.findViewById(R.id.txt_plantingDepth);
        txt_plantingDetails = view.findViewById(R.id.txt_plantingDetails);
        txt_daysToMaturity = view.findViewById(R.id.txt_daysToMaturity);
        txt_harvestWindow = view.findViewById(R.id.txt_harvestWindow);
        txt_estimatedRevenue = view.findViewById(R.id.txt_estimatedRevenue);
        txt_expectedYield = view.findViewById(R.id.txt_expectedYield);
        spinner_cropType = view.findViewById(R.id.spinner_cropType);
        spinner_startMethod = view.findViewById(R.id.spinner_startMethod);
        spinner_lightProfile = view.findViewById(R.id.spinner_lightProfile);
        spinner_harvestUnit = view.findViewById(R.id.spinner_harvestUnit);
        btn_saveCrop = view.findViewById(R.id.btn_saveCrop);
        loadingBar = new ProgressDialog(getActivity());
    }

    private void selectPlantedDate() {
        //Set current date by default
        calendar_plantedDate = view.findViewById(R.id.calendar_plantedDate);
        Calendar calendarToday = Calendar.getInstance();
        long currentDateInMillis = calendarToday.getTimeInMillis();
        Date currentDate = new Date(currentDateInMillis);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strCurrentDate = dateFormat.format(currentDate);
        calendar_plantedDate.setText(strCurrentDate);
        planted_date = String.valueOf(currentDateInMillis);

        //open DatePickerDialog
        calendar_plantedDate.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, dateSetListener, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        //Set Selected Date
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            final Calendar calendarPlantedDate = Calendar.getInstance();

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendarPlantedDate.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                long longPlantedDate = calendarPlantedDate.getTimeInMillis();
                Date datePlantedDate = new Date(longPlantedDate);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String strPlantedDate = dateFormat.format(datePlantedDate);
                calendar_plantedDate.setText(strPlantedDate);
                System.out.println("calPlanted" + strPlantedDate);
                planted_date = String.valueOf(longPlantedDate);
            }
        };
    }

    private void sendUserCropsFragment() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment cropsFragment = new CropsFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, cropsFragment).addToBackStack(null).commit();
    }

    private void addCrop() {

        String crop_type = spinner_cropType.getSelectedItem().toString();
        String crop_name = txt_cropName.getText().toString().trim();
        String crop_variety = txt_variety.getText().toString().trim();
        String botanical_name = txt_botanicalName.getText().toString().trim();
        String planting_details = txt_plantingDetails.getText().toString().trim();
        int days_to_emerge = 0;
        int days_to_maturity = 0;
        int harvest_window = 0;
        float plant_spacing = (float) 0;
        float row_spacing = (float) 0;
        float planting_depth = (float) 0;
        float estimated_revenue = (float) 0;
        float expected_yield = (float) 0;
        String start_method = spinner_startMethod.getSelectedItem().toString();
        String light_profile = spinner_lightProfile.getSelectedItem().toString();
        String harvest_unit = spinner_harvestUnit.getSelectedItem().toString();


        if (TextUtils.isEmpty(crop_name)) {
            txt_cropName.setError("This field is required!");
            txt_cropName.requestFocus();
            return;
        }

        if (crop_variety.isEmpty() || crop_variety.trim().isEmpty())
            crop_variety = "N/a";
        if (botanical_name.isEmpty() || botanical_name.trim().isEmpty())
            botanical_name = "N/a";
        if (planting_details.isEmpty() || planting_details.trim().isEmpty())
            planting_details = "N/a";
        if (txt_daysToEmerge.length() > 0)
            days_to_emerge = Integer.parseInt(txt_daysToEmerge.getText().toString().trim());
        if (txt_daysToMaturity.length() > 0)
            days_to_maturity = Integer.parseInt(txt_daysToMaturity.getText().toString().trim());
        if (txt_harvestWindow.length() > 0)
            harvest_window = Integer.parseInt(txt_harvestWindow.getText().toString().trim());
        if (txt_plantSpacing.length() > 0)
            plant_spacing = Float.parseFloat(txt_plantSpacing.getText().toString().trim());
        if (txt_rowSpacing.length() > 0)
            row_spacing = Float.parseFloat(txt_rowSpacing.getText().toString().trim());
        if (txt_plantingDepth.length() > 0)
            planting_depth = Float.parseFloat(txt_plantingDepth.getText().toString().trim());
        if (txt_estimatedRevenue.length() > 0)
            estimated_revenue = Float.parseFloat(txt_estimatedRevenue.getText().toString().trim());
        if (txt_expectedYield.length() > 0)
            expected_yield = Float.parseFloat(txt_expectedYield.getText().toString().trim());

        loadingBar.setTitle("Please wait");
        loadingBar.setMessage("Adding...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        String crop_id = dbReferenceCrop.push().getKey();
        Crop crop = new Crop(crop_id, crop_type, crop_name, crop_variety, botanical_name, planted_date,
                start_method, light_profile, planting_details, days_to_emerge, harvest_unit, days_to_maturity,
                harvest_window, plant_spacing, row_spacing, planting_depth, estimated_revenue, expected_yield);

        assert crop_id != null;
        dbReferenceCrop.child(crop_id).setValue(crop);
        System.out.println(dbReferenceCrop.child(crop_id).setValue(crop));
        sendUserCropsFragment();
        loadingBar.dismiss();

    }

}