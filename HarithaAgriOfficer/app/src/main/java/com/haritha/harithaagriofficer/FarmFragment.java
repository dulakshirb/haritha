package com.haritha.harithaagriofficer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FarmFragment extends Fragment {

    private FarmAdapter farmAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_farm, container, false);

        DatabaseReference dbReferenceFarm = FirebaseDatabase.getInstance().getReference("Farmer")
                .child("Users");

        RecyclerView farmRecyclerView = view.findViewById(R.id.rv_farms);
        farmRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<Farm> options = new FirebaseRecyclerOptions.Builder<Farm>()
                .setQuery(dbReferenceFarm, Farm.class)
                .build();

        farmAdapter = new FarmAdapter(options);
        farmRecyclerView.setAdapter(farmAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        farmAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        farmAdapter.stopListening();
    }
}