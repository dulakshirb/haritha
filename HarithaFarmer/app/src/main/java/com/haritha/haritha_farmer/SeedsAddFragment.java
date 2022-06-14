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

public class SeedsAddFragment extends Fragment {

    private View view;
    private Button btnAddSeeds;
    private EditText seedsName, seedsDescription, seedsVariety, seedsType, seedsAmount, seedsRestockAmount;

    private ProgressDialog loadingBar;

    private String currentUserId, seed_name, seed_description, seed_variety, seed_type;
    private Float seed_amount, seed_restock_amount;
    private FirebaseAuth mAuth;
    private DatabaseReference dbSeeds;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_seeds_add, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        dbSeeds = FirebaseDatabase.getInstance().getReference("Farmer").child("Seeds").child(currentUserId);

        initializeFields();

        btnAddSeeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSeeds();
            }
        });
        return view;

    }
    private void initializeFields(){

        seedsName = (EditText)view.findViewById(R.id.txt_seedName);
        seedsDescription = (EditText)view.findViewById(R.id.txt_seedDescription);
        seedsVariety = (EditText)view.findViewById(R.id.txt_seedVariety);
        seedsType = (EditText) view.findViewById(R.id.txt_seedType);
        seedsAmount = (EditText) view.findViewById(R.id.txt_seedAmount);
        seedsRestockAmount = (EditText) view.findViewById(R.id.txt_seedRestockAmount);
        btnAddSeeds = (Button) view.findViewById(R.id.btn_seed_save);
        loadingBar = new ProgressDialog(getActivity());
    }
    private void sendUsertoFertilizerFragment() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment fertilizerFragment = new FertilizerFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame,fertilizerFragment).addToBackStack(null).commit();
    }

    private void addSeeds(){
        seed_name = seedsName.getText().toString().trim();
        seed_description = seedsDescription.getText().toString().trim();
        seed_variety = seedsVariety.getText().toString().trim();
        seed_type = seedsType.getText().toString().trim();
        seed_amount = Float.valueOf(0);
        seed_restock_amount = Float.valueOf(0);


        if (TextUtils.isEmpty(seed_name)) {
            seedsName.setError("This field is required!");
            seedsName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(seed_description)) {
            seedsDescription.setError("This field is required!");
            seedsDescription.requestFocus();
            return;
        }

        if (seed_variety == null || seed_variety.isEmpty() || seed_variety.trim().isEmpty())
            seed_variety = "N/a";
        if (seed_type == null || seed_type.isEmpty() || seed_type.trim().isEmpty())
            seed_type = "N/a";
        if (seedsAmount.length() > 0)
            seed_amount = Float.parseFloat(seedsAmount.getText().toString().trim());
        if (seedsRestockAmount.length() > 0)
            seed_restock_amount = Float.parseFloat(seedsRestockAmount.getText().toString().trim());

        loadingBar.setTitle("Please wait");
        loadingBar.setMessage("Adding...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        String seed_id = dbSeeds.push().getKey();
        Seeds seeds = new Seeds(seed_id, seed_name, seed_description, seed_variety, seed_type, seed_amount, seed_restock_amount);
        dbSeeds.child(seed_id).setValue(seeds);
        sendUsertoFertilizerFragment();
        loadingBar.dismiss();

    }
}