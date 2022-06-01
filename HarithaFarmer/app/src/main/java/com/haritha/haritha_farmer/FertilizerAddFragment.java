package com.haritha.haritha_farmer;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
import java.util.HashMap;

public class FertilizerAddFragment extends Fragment {

    private View view;
    private Button btnAddFertilizer;
    private EditText fertilizerDescription, fertilizerCropsToUse, fertilizerAmount, fertilizerRestockAmount;
    private Spinner fertilizerName;
    private ProgressDialog loadingBar;

    private String currentUserId, fertilizer_name, fertilizer_description, fertilizer_crops_touse;
    private Float fertilizer_amount, fertilizer_restock_amount;
    private FirebaseAuth mAuth;
    private DatabaseReference dbFertilizer;



    public FertilizerAddFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fertilizer_add, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        dbFertilizer = FirebaseDatabase.getInstance().getReference("Farmer").child("Fertilizer").child(currentUserId);

        initializeFields();

        btnAddFertilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFertilizer();
            }
        });
        return view;
    }

    private void initializeFields(){

        fertilizerName = (Spinner) view.findViewById(R.id.spinner_fertilizerName);
        fertilizerDescription = (EditText)view.findViewById(R.id.txt_fertilizerDescription);
        fertilizerCropsToUse = (EditText)view.findViewById(R.id.txt_fertilizerCrops);
        fertilizerAmount = (EditText)view.findViewById(R.id.txt_fertilizerAmount);
        fertilizerRestockAmount = (EditText)view.findViewById(R.id.txt_fertilizerRestockAmount);
        btnAddFertilizer = (Button) view.findViewById(R.id.btn_fertilizer_save);
        loadingBar = new ProgressDialog(getActivity());

    }

    private void sendUsertoFertilizerFragment() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment fertilizerFragment = new FertilizerFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame,fertilizerFragment).addToBackStack(null).commit();
    }

    private void addFertilizer(){
        fertilizer_name = fertilizerName.getSelectedItem().toString();
        fertilizer_description = fertilizerDescription.getText().toString().trim();
        fertilizer_crops_touse = fertilizerCropsToUse.getText().toString().trim();
        fertilizer_amount = Float.valueOf(0);
        fertilizer_restock_amount = Float.valueOf(0);


        if (TextUtils.isEmpty(fertilizer_description)) {
            fertilizerDescription.setError("This field is required!");
            fertilizerDescription.requestFocus();
            return;
        }

        if (fertilizer_crops_touse == null || fertilizer_crops_touse.isEmpty() || fertilizer_crops_touse.trim().isEmpty())
            fertilizer_crops_touse = "N/a";
        if (fertilizerAmount.length() > 0)
            fertilizer_amount = Float.parseFloat(fertilizerAmount.getText().toString().trim());
        if (fertilizerRestockAmount.length() > 0)
            fertilizer_restock_amount = Float.parseFloat(fertilizerRestockAmount.getText().toString().trim());

        loadingBar.setTitle("Please wait");
        loadingBar.setMessage("Adding...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        String fertilizer_id = dbFertilizer.push().getKey();
        Fertilizer fertilizer = new Fertilizer(fertilizer_id, fertilizer_name, fertilizer_description, fertilizer_crops_touse, fertilizer_amount, fertilizer_restock_amount);
        dbFertilizer.child(fertilizer_id).setValue(fertilizer);
        sendUsertoFertilizerFragment();
        loadingBar.dismiss();

    }
}