package com.haritha.haritha_farmer;

import android.annotation.SuppressLint;
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



public class FertilizerFragment extends Fragment {

   // private FertilizerAdapter fertilizerAdapter;
    private DatabaseReference dbFertilizer;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fertilizer, container, false);

//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        String currentUserID = mAuth.getCurrentUser().getUid();
//        dbFertilizer = FirebaseDatabase.getInstance().getReference("Farmer").child("Fertilizer").child(currentUserID);

        Button btnAddFertilizer = (Button) view.findViewById(R.id.btn_add_fertilizer);

        btnAddFertilizer.setOnClickListener(addView -> {
            AppCompatActivity activity = (AppCompatActivity) addView.getContext();
            Fragment fertilizerAddFragment = new FertilizerAddFragment();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, fertilizerAddFragment).addToBackStack(null).commit();

        });

//        RecyclerView fertilizerRecylerView = (RecyclerView) view.findViewById(R.id.rv_Fertilizer);
//        fertilizerRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        FirebaseRecyclerOptions<Fertilizer> options = new FirebaseRecyclerOptions.Builder<Fertilizer>()
//                .setQuery(dbFertilizer, Fertilizer.class)
//                .build();
//
//        fertilizerAdapter = new FertilizerAdapter(options);
//        fertilizerRecylerView.setAdapter(fertilizerAdapter);
        return view;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        fertilizerAdapter.startListening();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        fertilizerAdapter.stopListening();
//    }
}