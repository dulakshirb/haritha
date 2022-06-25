package com.haritha.harithaagriofficer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FarmViewFragment extends Fragment {

    String farmId, farmName, userName, email, mobile, gender, district, location;
    private FarmBiogasSellAdapter farmBiogasSellAdapter;

    public FarmViewFragment(String farmId, String farmName, String userName, String email, String mobile, String gender, String district, String location) {
        this.farmId = farmId;
        this.farmName = farmName;
        this.userName = userName;
        this.email = email;
        this.mobile = mobile;
        this.gender = gender;
        this.district = district;
        this.location = location;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_farm_view, container, false);

        TextView txt_view_farmName = view.findViewById(R.id.txt_view_farmName);
        TextView txt_view_ownerName = view.findViewById(R.id.txt_view_ownerName);
        TextView txt_view_farmEmail = view.findViewById(R.id.txt_view_farmEmail);
        TextView txt_view_farmPhone = view.findViewById(R.id.txt_view_farmPhone);
        TextView txt_view_farmOwnerGender = view.findViewById(R.id.txt_view_farmOwnerGender);
        TextView txt_view_farmDistrict = view.findViewById(R.id.txt_view_farmDistrict);
        TextView txt_view_location = view.findViewById(R.id.txt_view_location);

        txt_view_farmName.setText(farmName);
        txt_view_ownerName.setText(userName);
        txt_view_farmEmail.setText(email);
        txt_view_farmPhone.setText(mobile);
        txt_view_farmOwnerGender.setText(gender);
        txt_view_farmDistrict.setText(district + " District");
        txt_view_location.setText(location);

        DatabaseReference dbReferenceBioGas = FirebaseDatabase.getInstance().getReference("Farmer").child("BioGas").child(farmId).child("sell");

        //view sell cylinders of farm
        RecyclerView biogasSellRecyclerView = view.findViewById(R.id.rv_cylinderSells);
        biogasSellRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseRecyclerOptions<FarmBiogasSell> options = new FirebaseRecyclerOptions.Builder<FarmBiogasSell>()
                .setQuery(dbReferenceBioGas, FarmBiogasSell.class)
                .build();

        farmBiogasSellAdapter = new FarmBiogasSellAdapter(options);
        biogasSellRecyclerView.setAdapter(farmBiogasSellAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        farmBiogasSellAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        farmBiogasSellAdapter.stopListening();
    }
}