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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CropsAddFragment extends Fragment {

    private View view;
    private AppCompatButton plantedDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Button btnAddCrop;
    private ProgressDialog loadingBar;

    private String today, planteddate, currentUserId, cropId;
    private FirebaseAuth mAuth;


    public CropsAddFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_crops_add, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();


        // initializeFields();
        selectPlantedDate();

        final EditText cropName = (EditText) view.findViewById(R.id.txt_cropName);
        final EditText variety = (EditText) view.findViewById(R.id.txt_variety);
        final EditText botanicalName = (EditText) view.findViewById(R.id.txt_botanicalName);
        final EditText daysToEmerge = (EditText) view.findViewById(R.id.txt_daysToEmerge);
        final EditText plantSpacing = (EditText) view.findViewById(R.id.txt_plantSpacing);
        final EditText rowSpacing = (EditText) view.findViewById(R.id.txt_rowSpacing);
        final EditText plantingDepth = (EditText) view.findViewById(R.id.txt_plantingDepth);
        final EditText plantingDetails = (EditText) view.findViewById(R.id.txt_plantingDetails);
        final EditText daysToMaturity = (EditText) view.findViewById(R.id.txt_daysToMaturity);
        final EditText harvestWindow = (EditText) view.findViewById(R.id.txt_harvestWindow);
        final EditText estimatedRevenue = (EditText) view.findViewById(R.id.txt_estimatedRevenue);
        final EditText expectedYield = (EditText) view.findViewById(R.id.txt_expectedYield);
        final Spinner cropType = (Spinner) view.findViewById(R.id.spinner_cropType);
        final Spinner startMethod = (Spinner) view.findViewById(R.id.spinner_startMethod);
        final Spinner lightProfile = (Spinner) view.findViewById(R.id.spinner_lightProfile);
        final Spinner harvestUnit = (Spinner) view.findViewById(R.id.spinner_harvestUnit);
        btnAddCrop = (Button) view.findViewById(R.id.btn_saveCrop);
        loadingBar = new ProgressDialog(getActivity());

        DAOCrop dao = new DAOCrop();

        btnAddCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get value and validate fields
                String crop_type = cropType.getSelectedItem().toString();
                String crop_name = cropName.getText().toString().trim();
                String crop_variety = null, botanical_name = null, planting_details = null, planted_date = null;
                int days_to_emerge = 0, days_to_maturity = 0, harvest_window = 0;
                Float plant_spacing = Float.valueOf(0), row_spacing = Float.valueOf(0), planting_depth = Float.valueOf(0), estimated_revenue = Float.valueOf(0), expected_yield = Float.valueOf(0);
                String start_method = startMethod.getSelectedItem().toString();
                String light_profile = lightProfile.getSelectedItem().toString();
                String harvest_unit = harvestUnit.getSelectedItem().toString();

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
                    plant_spacing = Float.parseFloat(plantSpacing.getText().toString());
                if (rowSpacing.length() > 0)
                    row_spacing = Float.parseFloat(rowSpacing.getText().toString());
                if (plantingDepth.length() > 0)
                    planting_depth = Float.parseFloat(plantingDepth.getText().toString());
                if (estimatedRevenue.length() > 0)
                    estimated_revenue = Float.parseFloat(estimatedRevenue.getText().toString());
                if (expectedYield.length() > 0)
                    expected_yield = Float.parseFloat(expectedYield.getText().toString());
                crop_variety = variety.getText().toString().trim();
                botanical_name = botanicalName.getText().toString().trim();
                planting_details = plantingDetails.getText().toString().trim();
                if (planteddate == null || planteddate.isEmpty() || planteddate.trim().isEmpty())
                    planted_date = today;
                else
                    planted_date = planteddate;

                /*String crop_type = cropType.getSelectedItem().toString();
                String crop_name = cropName.getText().toString().trim();
                if (TextUtils.isEmpty(crop_name)) {
                    cropName.setError("This field is required!");
                    cropName.requestFocus();
                    return;
                }
                String crop_variety = variety.getText().toString().trim();
                String botanical_name = botanicalName.getText().toString().trim();
                String planted_date = planteddate;
                String start_method = startMethod.getSelectedItem().toString();
                String light_profile = lightProfile.getSelectedItem().toString();
                String planting_details = plantingDetails.getText().toString().trim();
                int days_to_emerge = Integer.parseInt(daysToEmerge.getText().toString().trim());
                String harvest_unit = harvestUnit.getSelectedItem().toString();
                int days_to_maturity = Integer.parseInt(daysToMaturity.getText().toString().trim());
                int harvest_window = Integer.parseInt(harvestWindow.getText().toString().trim());
                Float plant_spacing = Float.parseFloat(plantSpacing.getText().toString().trim());
                Float row_spacing = Float.parseFloat(rowSpacing.getText().toString().trim());
                Float planting_depth = Float.parseFloat(plantingDepth.getText().toString().trim());
                Float estimated_revenue = Float.parseFloat(estimatedRevenue.getText().toString().trim());
                Float expected_yield = Float.parseFloat(expectedYield.getText().toString().trim());*/

                Crop crop = new Crop(currentUserId, crop_type, crop_name, crop_variety, botanical_name, planted_date,
                        start_method, light_profile, planting_details, days_to_emerge, harvest_unit, days_to_maturity,
                        harvest_window, plant_spacing, row_spacing, planting_depth, estimated_revenue, expected_yield);

                loadingBar.setTitle("Please wait");
                loadingBar.setMessage("Adding...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                dao.insert(crop).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Crop added successfully...", Toast.LENGTH_SHORT).show();
                        sendUsertoCropsFragment();
                        loadingBar.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to add new crop...", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                });
            }
        });

        return view;
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

}