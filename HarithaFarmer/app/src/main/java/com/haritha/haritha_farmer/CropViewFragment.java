package com.haritha.haritha_farmer;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CropViewFragment extends Fragment {

    private AppCompatButton plantedDate;
    private String today, planteddate, currentUserID, cropId, cropName, cropType, cropVariety;
    //  private EditText crop_name, crop_variety, botanicalName, daysToEmerge, plantSpacing, rowSpacing, plantingDepth, plantingDetails, daysToMaturity, harvestWindow, estimatedRevenue, expectedYield;
    // private Spinner crop_type, startMethod, lightProfile, harvestUnit;

    private Button btnEdit, btnUpdateCrop;
    private AppCompatButton btnRemove;
    private Dialog deleteDialog;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private View view;
    DatabaseReference dbCrop;

    public CropViewFragment() {
        // Required empty public constructor
    }

    public CropViewFragment(String cropId, String cropName, String cropType, String cropVariety) {
        this.cropId = cropId;
        this.cropName = cropName;
        this.cropType = cropType;
        this.cropVariety = cropVariety;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_crop_view, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        dbCrop = FirebaseDatabase.getInstance().getReference("Farmer").child("Crop").child(currentUserID).child(cropId);

        selectPlantedDate();

        TextView txt_view_cropsName = (TextView) view.findViewById(R.id.txt_view_cropsName);
        TextView txt_view_cropType = (TextView) view.findViewById(R.id.txt_view_cropType);
        TextView txt_view_cropVariety = (TextView) view.findViewById(R.id.txt_view_cropVariety);
        btnEdit = (Button) view.findViewById(R.id.btn_edit_crop);
        btnRemove = (AppCompatButton) view.findViewById(R.id.btn_remove_crop);

        txt_view_cropsName.setText(cropName);
        txt_view_cropType.setText(cropType);
        txt_view_cropVariety.setText(cropVariety);

        deleteDialog = new Dialog(getActivity());
        deleteDialog.setContentView(R.layout.delete_dialog);
        deleteDialog.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_background);
        deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteDialog.setCancelable(false);
        deleteDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        AppCompatButton cancel = deleteDialog.findViewById(R.id.btn_dialog_cancel);
        AppCompatButton delete = deleteDialog.findViewById(R.id.btn_dialog_delete);

        delete.setOnClickListener(deleteBtn -> {
            deleteCrop(cropId);
            deleteDialog.dismiss();
        });

        cancel.setOnClickListener(cancelBtn -> {
            deleteDialog.dismiss();
        });

        btnEdit.setOnClickListener(edit -> {
            onEditPressed(cropId, cropName, cropType, cropVariety);
        });

        btnRemove.setOnClickListener(remove -> {
            deleteDialog.show();
        });

        return view;
    }

    private void deleteCrop(String cropId) {
        dbCrop.removeValue();
        sendUsertoCropsFragment();
    }

    public void onEditPressed(String cropId, String cropName, String cropType, String cropVariety) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_crop_edit, null);
        dialogBuilder.setView(dialogView);

        final EditText cropname = (EditText) dialogView.findViewById(R.id.txt_cropName);
        final EditText cropvariety = (EditText) dialogView.findViewById(R.id.txt_variety);
        final Spinner croptype = (Spinner) dialogView.findViewById(R.id.spinner_cropType);
        btnUpdateCrop = (Button) dialogView.findViewById(R.id.btn_updateCrop);
        /*variety = (EditText) view.findViewById(R.id.txt_variety);
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
        loadingBar = new ProgressDialog(getActivity());*/

        dialogBuilder.setTitle("Updating " + cropName);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnUpdateCrop.setOnClickListener(view -> {
            String crop_name = cropname.getText().toString().trim();
            String updated_crop_type;
            String crop_type = croptype.getSelectedItem().toString();
            if (crop_type != cropType) {
                updated_crop_type = crop_type;
            } else
                updated_crop_type = cropType;
            String crop_variety = cropvariety.getText().toString().trim();
            String botanical_name = null;
            String planted_date =

            updateCrop(cropId, updated_crop_type, crop_name, crop_variety, botanical_name);
        });

    }

    private void sendUsertoCropsFragment() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment cropsFragment = new CropsFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, cropsFragment).addToBackStack(null).commit();
    }

    private void selectPlantedDate() {
        //Set current date by default
        plantedDate = (AppCompatButton) view.findViewById(R.id.calendar_plantedDate);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        today = dateFormat.format(calendar.getTime());
        plantedDate.setText(today);
        planteddate = today;

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