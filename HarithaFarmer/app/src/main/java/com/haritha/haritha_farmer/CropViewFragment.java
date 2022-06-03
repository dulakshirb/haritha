package com.haritha.haritha_farmer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class CropViewFragment extends Fragment {

    private final String cropId;
    private String cropName;
    private String cropType;
    private String cropVariety;
    private String botanicalName;
    private String startMethod;
    private final String lightProfile;
    private String plantingDetails;
    private String plantedDate;
    private String harvestUnit;
    private Integer daysToEmerge, daysToMaturity, harvestWindow;
    private Float plantSpacing, rowSpacing, plantingDepth, estimatedRevenue, expectedYield;

    private Button btnEdit;
    private AppCompatButton calendar_plantedDate, btnRemove, btnCancel, btnDelete;
    private Dialog deleteDialog;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TextView txt_view_cropsName, txt_view_cropType, txt_view_cropVariety, txt_view_botanicalName,
            txt_view_plantedDate, txt_view_startMethod, txt_view_LightProfile, txt_view_plantingDetails,
            txt_view_daysToEmerge, txt_view_harvestUnit, txt_view_daysToMaturity, txt_view_harvestWindow,
            txt_view_plantSpacing, txt_view_rowSpacing, txt_view_plantingDepth, txt_view_estimatedRevenue,
            txt_view_expectedYield;

    private View view;
    private DatabaseReference dbCrop;

    public CropViewFragment(String cropId, String cropType, String cropName, String cropVariety, String botanicalName,
                            String plantedDate, String startMethod, String lightProfile, String plantingDetails,
                            Integer daysToEmerge, String harvestUnit, Integer daysToMaturity, Integer harvestWindow,
                            Float plantSpacing, Float rowSpacing, Float plantingDepth, Float estimatedRevenue, Float expectedYield) {
        this.cropId = cropId;
        this.cropType = cropType;
        this.cropName = cropName;
        this.cropVariety = cropVariety;
        this.botanicalName = botanicalName;
        this.plantedDate = plantedDate;
        this.startMethod = startMethod;
        this.lightProfile = lightProfile;
        this.plantingDetails = plantingDetails;
        this.daysToEmerge = daysToEmerge;
        this.harvestUnit = harvestUnit;
        this.daysToMaturity = daysToMaturity;
        this.harvestWindow = harvestWindow;
        this.plantSpacing = plantSpacing;
        this.rowSpacing = rowSpacing;
        this.plantingDepth = plantingDepth;
        this.estimatedRevenue = estimatedRevenue;
        this.expectedYield = expectedYield;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_crop_view, container, false);

        //Database reference and get current user Id
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        dbCrop = FirebaseDatabase.getInstance().getReference("Farmer").child("Crop").child(currentUserID).child(cropId);

        cropViewFragmentFieldsInitializing();
        cropViewFragmentSetValues();

        //Remove button on cropViewFragment
        deleteDialogInitializing();
        btnRemove.setOnClickListener(remove -> deleteDialog.show());

        btnCancel.setOnClickListener(cancelBtn -> deleteDialog.dismiss());

        btnDelete.setOnClickListener(deleteBtn -> {
            deleteCrop();
            deleteDialog.dismiss();
        });

        //Edit button on cropViewFragment
        btnEdit.setOnClickListener(edit -> onEditPressed());

        return view;
    }

    private void deleteDialogInitializing() {
        deleteDialog = new Dialog(getActivity());
        deleteDialog.setContentView(R.layout.delete_dialog);
        deleteDialog.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_background);
        deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteDialog.setCancelable(false);
        deleteDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        btnCancel = deleteDialog.findViewById(R.id.btn_dialog_cancel);
        btnDelete = deleteDialog.findViewById(R.id.btn_dialog_delete);
    }

    private void cropViewFragmentFieldsInitializing() {
        txt_view_cropsName = view.findViewById(R.id.txt_view_cropsName);
        txt_view_cropType = view.findViewById(R.id.txt_view_cropType);
        txt_view_cropVariety = view.findViewById(R.id.txt_view_cropVariety);
        txt_view_botanicalName = view.findViewById(R.id.txt_view_botanicalName);
        txt_view_plantedDate = view.findViewById(R.id.txt_view_plantedDate);
        txt_view_startMethod = view.findViewById(R.id.txt_view_startMethod);
        txt_view_LightProfile = view.findViewById(R.id.txt_view_LightProfile);
        txt_view_plantingDetails = view.findViewById(R.id.txt_view_plantingDetails);
        txt_view_daysToEmerge = view.findViewById(R.id.txt_view_daysToEmerge);
        txt_view_harvestUnit = view.findViewById(R.id.txt_view_harvestUnit);
        txt_view_daysToMaturity = view.findViewById(R.id.txt_view_daysToMaturity);
        txt_view_harvestWindow = view.findViewById(R.id.txt_view_harvestWindow);
        txt_view_plantSpacing = view.findViewById(R.id.txt_view_plantSpacing);
        txt_view_rowSpacing = view.findViewById(R.id.txt_view_rowSpacing);
        txt_view_plantingDepth = view.findViewById(R.id.txt_view_plantingDepth);
        txt_view_estimatedRevenue = view.findViewById(R.id.txt_view_estimatedRevenue);
        txt_view_expectedYield = view.findViewById(R.id.txt_view_expectedYield);

        btnEdit = view.findViewById(R.id.btn_edit_crop);
        btnRemove = view.findViewById(R.id.btn_remove_crop);
    }

    private void cropViewFragmentSetValues() {
        txt_view_cropsName.setText(cropName);
        txt_view_cropType.setText(cropType);
        txt_view_cropVariety.setText(cropVariety);
        txt_view_botanicalName.setText(botanicalName);
        long longPlantedDate = Long.parseLong(plantedDate);
        System.out.println("longPlantedDate " + longPlantedDate);
        Date datePlantedDate = new Date(longPlantedDate);
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        txt_view_plantedDate.setText(simpleDateFormat.format(datePlantedDate));
        txt_view_startMethod.setText(startMethod);
        txt_view_LightProfile.setText(lightProfile);
        txt_view_plantingDetails.setText(plantingDetails);
        txt_view_daysToEmerge.setText(String.valueOf(daysToEmerge));
        txt_view_harvestUnit.setText(harvestUnit);
        txt_view_daysToMaturity.setText(String.valueOf(daysToMaturity));
        txt_view_harvestWindow.setText(String.valueOf(harvestWindow));
        txt_view_plantSpacing.setText(String.valueOf(plantSpacing));
        txt_view_rowSpacing.setText(String.valueOf(rowSpacing));
        txt_view_plantingDepth.setText(String.valueOf(plantingDepth));
        txt_view_estimatedRevenue.setText(String.valueOf(estimatedRevenue));
        txt_view_expectedYield.setText(String.valueOf(expectedYield));
    }

    private void deleteCrop() {
        if (dbCrop != null)
            dbCrop.removeValue();
        sendUserCropsFragment();
    }

    public void onEditPressed() {
        //open cropEditFragment dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_crop_edit, null);
        dialogBuilder.setView(dialogView);

        //initialized fields
        final TextView txt_cropTypeOld = dialogView.findViewById(R.id.txt_cropTypeOld);
        final Spinner spinner_cropType = dialogView.findViewById(R.id.spinner_cropType);
        final EditText txt_cropName = dialogView.findViewById(R.id.txt_cropName);
        final EditText txt_variety = dialogView.findViewById(R.id.txt_variety);
        final EditText txt_botanicalName = dialogView.findViewById(R.id.txt_botanicalName);
        calendar_plantedDate = dialogView.findViewById(R.id.calendar_plantedDate);
        final TextView txt_startMethodOld = dialogView.findViewById(R.id.txt_startMethodOld);
        final Spinner spinner_startMethod = dialogView.findViewById(R.id.spinner_startMethod);
        final TextView txt_lightProfileOld = dialogView.findViewById(R.id.txt_lightProfileOld);
        final Spinner spinner_lightProfile = dialogView.findViewById(R.id.spinner_lightProfile);
        final EditText txt_plantingDetails = dialogView.findViewById(R.id.txt_plantingDetails);
        final EditText txt_daysToEmerge = dialogView.findViewById(R.id.txt_daysToEmerge);
        final TextView txt_harvestUnitOld = dialogView.findViewById(R.id.txt_harvestUnitOld);
        final Spinner spinner_harvestUnit = dialogView.findViewById(R.id.spinner_harvestUnit);
        final EditText txt_daysToMaturity = dialogView.findViewById(R.id.txt_daysToMaturity);
        final EditText txt_harvestWindow = dialogView.findViewById(R.id.txt_harvestWindow);
        final EditText txt_plantSpacing = dialogView.findViewById(R.id.txt_plantSpacing);
        final EditText txt_rowSpacing = dialogView.findViewById(R.id.txt_rowSpacing);
        final EditText txt_plantingDepth = dialogView.findViewById(R.id.txt_plantingDepth);
        final EditText txt_estimatedRevenue = dialogView.findViewById(R.id.txt_estimatedRevenue);
        final EditText txt_expectedYield = dialogView.findViewById(R.id.txt_expectedYield);
        calendar_plantedDate = dialogView.findViewById(R.id.calendar_plantedDate);

        Button btnUpdateCrop = dialogView.findViewById(R.id.btn_updateCrop);

        dialogBuilder.setTitle("Updating " + cropName);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //setText Values
        txt_cropTypeOld.setText(cropType);
        txt_cropName.setText(cropName);
        txt_variety.setText(cropVariety);
        txt_botanicalName.setText(botanicalName);
        txt_startMethodOld.setText(startMethod);
        txt_lightProfileOld.setText(lightProfile);
        txt_plantingDetails.setText(plantingDetails);
        txt_daysToEmerge.setText(String.valueOf(daysToEmerge));
        txt_harvestUnitOld.setText(harvestUnit);
        txt_daysToMaturity.setText(String.valueOf(daysToMaturity));
        txt_harvestWindow.setText(String.valueOf(harvestWindow));
        txt_plantSpacing.setText(String.valueOf(plantSpacing));
        txt_rowSpacing.setText(String.valueOf(rowSpacing));
        txt_plantingDepth.setText(String.valueOf(plantingDepth));
        txt_estimatedRevenue.setText(String.valueOf(estimatedRevenue));
        txt_expectedYield.setText(String.valueOf(expectedYield));

        selectPlantedDate();

        btnUpdateCrop.setOnClickListener(view -> {
            if (!Objects.equals(cropType, spinner_cropType.getSelectedItem().toString()))
                cropType = spinner_cropType.getSelectedItem().toString();
            if (txt_cropName.getText().toString().trim().isEmpty()) {
                txt_cropName.setError("This field is required!");
                txt_cropName.requestFocus();
                return;
            }
            cropName = txt_cropName.getText().toString().trim();
            cropVariety = txt_variety.getText().toString().trim();
            botanicalName = txt_botanicalName.getText().toString().trim();

            if (!Objects.equals(startMethod, spinner_startMethod.getSelectedItem().toString()))
                startMethod = spinner_startMethod.getSelectedItem().toString();
            if (!Objects.equals(lightProfile, spinner_lightProfile.getSelectedItem().toString()))
                startMethod = spinner_lightProfile.getSelectedItem().toString();
            plantingDetails = txt_plantingDetails.getText().toString().trim();
            daysToEmerge = Integer.parseInt(txt_daysToEmerge.getText().toString().trim());
            if (!Objects.equals(harvestUnit, spinner_harvestUnit.getSelectedItem().toString()))
                harvestUnit = spinner_harvestUnit.getSelectedItem().toString();
            daysToMaturity = Integer.parseInt(txt_daysToMaturity.getText().toString().trim());
            harvestWindow = Integer.parseInt(txt_harvestWindow.getText().toString().trim());
            plantSpacing = Float.parseFloat(txt_plantSpacing.getText().toString().trim());
            rowSpacing = Float.parseFloat(txt_rowSpacing.getText().toString().trim());
            plantingDepth = Float.parseFloat(txt_plantingDepth.getText().toString().trim());
            estimatedRevenue = Float.parseFloat(txt_estimatedRevenue.getText().toString().trim());
            expectedYield = Float.parseFloat(txt_expectedYield.getText().toString().trim());

            if (updateCrop(cropId, cropType, cropName, cropVariety, botanicalName, plantedDate, startMethod, lightProfile, plantingDetails,
                    daysToEmerge, harvestUnit, daysToMaturity, harvestWindow, plantSpacing, rowSpacing, plantingDepth, estimatedRevenue, expectedYield)) {
                alertDialog.dismiss();
                sendUserCropsFragment();
            } else {
                Toast.makeText(getActivity(), "Failed to update, please try again!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void sendUserCropsFragment() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment cropsFragment = new CropsFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, cropsFragment).addToBackStack(null).commit();
    }

    private void selectPlantedDate() {
        //get old planted date
        long longPlantedDate = Long.parseLong(plantedDate);
        System.out.println("longPlantedDate " + longPlantedDate);
        Date datePlantedDate = new Date(longPlantedDate);
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar_plantedDate.setText(simpleDateFormat.format(datePlantedDate));

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
                plantedDate = String.valueOf(longPlantedDate);
            }
        };
    }

    private boolean updateCrop(String cropId, String cropType, String cropName, String cropVariety, String botanicalName,
                               String plantedDate, String startMethod, String lightProfile, String plantingDetails,
                               Integer daysToEmerge, String harvestUnit, Integer daysToMaturity, Integer harvestWindow,
                               Float plantSpacing, Float rowSpacing, Float plantingDepth, Float estimatedRevenue, Float expectedYield) {

        Crop crop = new Crop(cropId, cropType, cropName, cropVariety, botanicalName, plantedDate,
                startMethod, lightProfile, plantingDetails, daysToEmerge, harvestUnit, daysToMaturity,
                harvestWindow, plantSpacing, rowSpacing, plantingDepth, estimatedRevenue, expectedYield);

        dbCrop.setValue(crop);
        return true;
    }
}