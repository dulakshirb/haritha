package com.haritha.haritha_farmer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class CropsFragment extends Fragment {

    private CropsAdapter cropsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crops, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference dbCrop = FirebaseDatabase.getInstance().getReference("Farmer").child("Crop").child(currentUserID);

        //Add New button
        Button btn_add_crop = view.findViewById(R.id.btn_add_crop);

        btn_add_crop.setOnClickListener(addView -> {
            AppCompatActivity activity = (AppCompatActivity) addView.getContext();
            Fragment cropsAddFragment = new CropsAddFragment();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, cropsAddFragment).addToBackStack(null).commit();
        });

        RecyclerView cropRecyclerView = view.findViewById(R.id.rv_crops);
        cropRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<Crop> options = new FirebaseRecyclerOptions.Builder<Crop>()
                .setQuery(dbCrop, Crop.class)
                .build();


        cropsAdapter = new CropsAdapter(options);
        cropRecyclerView.setAdapter(cropsAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        cropsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        cropsAdapter.stopListening();
    }

}