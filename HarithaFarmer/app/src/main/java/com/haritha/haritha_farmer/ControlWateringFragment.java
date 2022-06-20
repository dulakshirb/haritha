package com.haritha.haritha_farmer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.graphics.Color;
import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ControlWateringFragment extends Fragment {

    private View view;
    private TextView text_soilmoisture;
    private String soilmoisture;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference dref;

    public ControlWateringFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_control_watering, container, false);
        text_soilmoisture = view.findViewById(R.id.txt_soil_health);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        dref = FirebaseDatabase.getInstance().getReference("Farmer").child("SoilHealth").child(userId);

        dref.child("health").addValueEventListener(new ValueEventListener(){
      //  dref = FirebaseDatabase.getInstance().getReference();
        //dref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                soilmoisture = dataSnapshot.child("health").getValue().toString();
                int soilData = Integer.parseInt(soilmoisture);


                if (soilData == 0) {

                    text_soilmoisture.setText("No watering required.");
                  //  text_soilmoisture.setTextColor(col2);

                }
                else {
                    text_soilmoisture.setText("Watering required!");
                   // text_soilmoisture.setTextColor(col1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
}