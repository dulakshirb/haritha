package com.haritha.haritha_farmer;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CropViewFragment extends Fragment {

    private String updated_planted_date, currentUserID, cropId, cropName, cropType, cropVariety, botanicalName, plantedDate, startMethod, lightProfile, plantingDetails, harvestUnit;
    private Integer daysToEmerge, daysToMaturity, harvestWindow;
    private Float plantSpacing, rowSpacing, plantingDepth, estimatedRevenue, expectedYield;

    private Button btnEdit, btnUpdateCrop;
    private AppCompatButton btnPlantedDate, btnRemove, btnCancel, btnDelete;
    private Dialog deleteDialog;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TextView txt_view_cropsName, txt_view_cropType, txt_view_cropVariety, txt_view_botanicalName,
            txt_view_plantedDate, txt_view_startMethod, txt_view_LightProfile, txt_view_plantingDetails,
            txt_view_daysToEmerge, txt_view_harvestUnit, txt_view_daysToMaturity, txt_view_harvestWindow,
            txt_view_plantSpacing, txt_view_rowSpacing, txt_view_plantingDepth, txt_view_estimatedRevenue,
            txt_view_expectedYield;

    private View view;
    private DatabaseReference dbCrop;
    private FirebaseAuth mAuth;

    public CropViewFragment() {
        // Required empty public constructor
    }

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
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        dbCrop = FirebaseDatabase.getInstance().getReference("Farmer").child("Crop").child(currentUserID).child(cropId);

        cropViewFragmentFieldsInitializing();
        cropViewFragmentSetValues();

        //Remove button on cropViewFragment
        deleteDialogInitializing();
        btnRemove.setOnClickListener(remove -> {
            deleteDialog.show();
        });

        btnCancel.setOnClickListener(cancelBtn -> {
            deleteDialog.dismiss();
        });

        btnDelete.setOnClickListener(deleteBtn -> {
            deleteCrop(cropId);
            deleteDialog.dismiss();
        });

        //Edit button on cropViewFragment
        btnEdit.setOnClickListener(edit -> {

            /*onEditPressed(cropId, cropName, cropType, cropVariety, botanicalName, plantedDate, startMethod
                    , lightProfile, plantingDetails, daysToEmerge, harvestUnit, daysToMaturity, harvestWindow, plantSpacing,
                    rowSpacing, plantingDepth, estimatedRevenue, expectedYield);*/
            onEditPressed();
        });


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
        txt_view_cropsName = (TextView) view.findViewById(R.id.txt_view_cropsName);
        txt_view_cropType = (TextView) view.findViewById(R.id.txt_view_cropType);
        txt_view_cropVariety = (TextView) view.findViewById(R.id.txt_view_cropVariety);
        txt_view_botanicalName = (TextView) view.findViewById(R.id.txt_view_botanicalName);
        txt_view_plantedDate = (TextView) view.findViewById(R.id.txt_view_plantedDate);
        txt_view_startMethod = (TextView) view.findViewById(R.id.txt_view_startMethod);
        txt_view_LightProfile = (TextView) view.findViewById(R.id.txt_view_LightProfile);
        txt_view_plantingDetails = (TextView) view.findViewById(R.id.txt_view_plantingDetails);
        txt_view_daysToEmerge = (TextView) view.findViewById(R.id.txt_view_daysToEmerge);
        txt_view_harvestUnit = (TextView) view.findViewById(R.id.txt_view_harvestUnit);
        txt_view_daysToMaturity = (TextView) view.findViewById(R.id.txt_view_daysToMaturity);
        txt_view_harvestWindow = (TextView) view.findViewById(R.id.txt_view_harvestWindow);
        txt_view_plantSpacing = (TextView) view.findViewById(R.id.txt_view_plantSpacing);
        txt_view_rowSpacing = (TextView) view.findViewById(R.id.txt_view_rowSpacing);
        txt_view_plantingDepth = (TextView) view.findViewById(R.id.txt_view_plantingDepth);
        txt_view_estimatedRevenue = (TextView) view.findViewById(R.id.txt_view_estimatedRevenue);
        txt_view_expectedYield = (TextView) view.findViewById(R.id.txt_view_expectedYield);

        btnEdit = (Button) view.findViewById(R.id.btn_edit_crop);
        btnRemove = (AppCompatButton) view.findViewById(R.id.btn_remove_crop);
    }

    private void cropViewFragmentSetValues() {
        txt_view_cropsName.setText(cropName);
        txt_view_cropType.setText(cropType);
        txt_view_cropVariety.setText(cropVariety);
        txt_view_botanicalName.setText(botanicalName);
        txt_view_plantedDate.setText(plantedDate);
        txt_view_startMethod.setText(startMethod);
        txt_view_LightProfile.setText(lightProfile);
        txt_view_plantingDetails.setText(plantingDetails);
        txt_view_daysToEmerge.setText(daysToEmerge.toString());
        txt_view_harvestUnit.setText(harvestUnit);
        txt_view_daysToMaturity.setText(daysToMaturity.toString());
        txt_view_harvestWindow.setText(harvestWindow.toString());
        txt_view_plantSpacing.setText(plantSpacing.toString());
        txt_view_rowSpacing.setText(rowSpacing.toString());
        txt_view_plantingDepth.setText(plantingDepth.toString());
        txt_view_estimatedRevenue.setText(estimatedRevenue.toString());
        txt_view_expectedYield.setText(expectedYield.toString());
    }

    private void deleteCrop(String cropId) {
        dbCrop.removeValue();
        sendUsertoCropsFragment();
    }

    public void onEditPressed() {
        //open cropEditFragment dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_crop_edit, null);
        dialogBuilder.setView(dialogView);

        //initialized fields
        final TextView txt_cropTypeOld = (TextView) dialogView.findViewById(R.id.txt_cropTypeOld);
        final Spinner spinner_cropType = (Spinner) dialogView.findViewById(R.id.spinner_cropType);
        final EditText txt_cropName = (EditText) dialogView.findViewById(R.id.txt_cropName);
        final EditText txt_variety = (EditText) dialogView.findViewById(R.id.txt_variety);
        final EditText txt_botanicalName = (EditText) dialogView.findViewById(R.id.txt_botanicalName);
        btnPlantedDate = (AppCompatButton) dialogView.findViewById(R.id.calendar_plantedDate);
        final TextView txt_startMethodOld = (TextView) dialogView.findViewById(R.id.txt_startMethodOld);
        final Spinner spinner_startMethod = (Spinner) dialogView.findViewById(R.id.spinner_startMethod);
        final TextView txt_lightProfileOld = (TextView) dialogView.findViewById(R.id.txt_lightProfileOld);
        final Spinner spinner_lightProfile = (Spinner) dialogView.findViewById(R.id.spinner_lightProfile);
        final EditText txt_plantingDetails = (EditText) dialogView.findViewById(R.id.txt_plantingDetails);
        final EditText txt_daysToEmerge = (EditText) dialogView.findViewById(R.id.txt_daysToEmerge);
        final TextView txt_harvestUnitOld = (TextView) dialogView.findViewById(R.id.txt_harvestUnitOld);
        final Spinner spinner_harvestUnit = (Spinner) dialogView.findViewById(R.id.spinner_harvestUnit);
        final EditText txt_daysToMaturity = (EditText) dialogView.findViewById(R.id.txt_daysToMaturity);
        final EditText txt_harvestWindow = (EditText) dialogView.findViewById(R.id.txt_harvestWindow);
        final EditText txt_plantSpacing = (EditText) dialogView.findViewById(R.id.txt_plantSpacing);
        final EditText txt_rowSpacing = (EditText) dialogView.findViewById(R.id.txt_rowSpacing);
        final EditText txt_plantingDepth = (EditText) dialogView.findViewById(R.id.txt_plantingDepth);
        final EditText txt_estimatedRevenue = (EditText) dialogView.findViewById(R.id.txt_estimatedRevenue);
        final EditText txt_expectedYield = (EditText) dialogView.findViewById(R.id.txt_expectedYield);

        btnUpdateCrop = (Button) dialogView.findViewById(R.id.btn_updateCrop);

        dialogBuilder.setTitle("Updating " + cropName);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        selectPlantedDate();

        //setText Values
        txt_cropTypeOld.setText(cropType);
        txt_cropName.setText(cropName);
        txt_variety.setText(cropVariety);
        txt_botanicalName.setText(botanicalName);
        btnPlantedDate.setText(plantedDate);
        txt_startMethodOld.setText(startMethod);
        txt_lightProfileOld.setText(lightProfile);
        txt_plantingDetails.setText(plantingDetails);
        txt_daysToEmerge.setText(daysToEmerge.toString());
        txt_harvestUnitOld.setText(harvestUnit);
        txt_daysToMaturity.setText(daysToMaturity.toString());
        txt_harvestWindow.setText(harvestWindow.toString());
        txt_plantSpacing.setText(plantSpacing.toString());
        txt_rowSpacing.setText(rowSpacing.toString());
        txt_plantingDepth.setText(plantingDepth.toString());
        txt_estimatedRevenue.setText(estimatedRevenue.toString());
        txt_expectedYield.setText(expectedYield.toString());

        btnUpdateCrop.setOnClickListener(view -> {
            if (cropType != spinner_cropType.getSelectedItem().toString())
                cropType = spinner_cropType.getSelectedItem().toString();
            if (txt_cropName.getText().toString().trim().isEmpty()) {
                txt_cropName.setError("This field is required!");
                txt_cropName.requestFocus();
                return;
            }
            cropName = txt_cropName.getText().toString().trim();
            cropVariety = txt_variety.getText().toString().trim();
            botanicalName = txt_botanicalName.getText().toString().trim();
            plantedDate = updated_planted_date;
            if (startMethod != spinner_startMethod.getSelectedItem().toString())
                startMethod = spinner_startMethod.getSelectedItem().toString();
            if (lightProfile != spinner_lightProfile.getSelectedItem().toString())
                startMethod = spinner_lightProfile.getSelectedItem().toString();
            plantingDetails = txt_plantingDetails.getText().toString().trim();
            daysToEmerge = Integer.parseInt(txt_daysToEmerge.getText().toString().trim());
            if (harvestUnit != spinner_harvestUnit.getSelectedItem().toString())
                harvestUnit = spinner_harvestUnit.getSelectedItem().toString();
            daysToMaturity = Integer.parseInt(txt_daysToMaturity.getText().toString().trim());
            harvestWindow = Integer.parseInt(txt_harvestWindow.getText().toString().trim());
            plantSpacing = Float.parseFloat(txt_plantSpacing.getText().toString().trim());
            rowSpacing = Float.parseFloat(txt_rowSpacing.getText().toString().trim());
            plantingDepth = Float.parseFloat(txt_plantingDepth.getText().toString().trim());
            estimatedRevenue = Float.parseFloat(txt_estimatedRevenue.getText().toString().trim());
            expectedYield = Float.parseFloat(txt_expectedYield.getText().toString().trim());

            if (updateCrop(cropId, cropType, cropName, cropVariety, botanicalName, plantedDate, startMethod, lightProfile, plantingDetails,
                    daysToEmerge, harvestUnit, daysToMaturity, harvestWindow, plantSpacing, rowSpacing, plantingDepth, estimatedRevenue, expectedYield) == true) {
                alertDialog.dismiss();
                sendUsertoCropsFragment();
            } else {
                Toast.makeText(getActivity(), "Failed to update, please try again!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void sendUsertoCropsFragment() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment cropsFragment = new CropsFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, cropsFragment).addToBackStack(null).commit();
    }

    private void selectPlantedDate() {
        //Set current date by default
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(calendar.getTime());
        btnPlantedDate.setText(today);
        updated_planted_date = today;

        //open DatePickerDialog
        btnPlantedDate.setOnClickListener(new View.OnClickListener() {
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
                String plantedDate = year + "-" + month + "-" + day;
                btnPlantedDate.setText(plantedDate);
                updated_planted_date = plantedDate;
            }
        };
    }

    private boolean updateCrop(String cropId, String cropType, String cropName, String cropVariety, String botanicalName,
                               String planted_date, String startMethod, String lightProfile, String plantingDetails,
                               Integer daysToEmerge, String harvestUnit, Integer daysToMaturity, Integer harvestWindow,
                               Float plantSpacing, Float rowSpacing, Float plantingDepth, Float estimatedRevenue, Float expectedYield) {

        Crop crop = new Crop(cropId, cropType, cropName, cropVariety, botanicalName, planted_date,
                startMethod, lightProfile, plantingDetails, daysToEmerge, harvestUnit, daysToMaturity,
                harvestWindow, plantSpacing, rowSpacing, plantingDepth, estimatedRevenue, expectedYield);

        dbCrop.setValue(crop);

        return true;

    }
}