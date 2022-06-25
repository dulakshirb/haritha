package com.haritha.haritha_farmer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ControlWateringFragment extends Fragment {

    private View view;
    private TextView text_soilmoisture, txt_watering;
    private String soilmoisture;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference dref;


    private AppCompatButton btn_waterOn;
    private AppCompatButton btn_waterOff;

    public ControlWateringFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_control_watering, container, false);
        text_soilmoisture = view.findViewById(R.id.txt_soil_health);
        btn_waterOn = view.findViewById(R.id.btn_waterOn);
        btn_waterOff = view.findViewById(R.id.btn_waterOff);
        txt_watering = view.findViewById(R.id.txt_watering);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        dref = FirebaseDatabase.getInstance().getReference("Farmer").child("SoilHealth").child(userId);

        dref.child("SoilData").addValueEventListener(new ValueEventListener(){
      //  dref = FirebaseDatabase.getInstance().getReference();
        //dref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                soilmoisture = dataSnapshot.child("SoilData").getValue().toString();
                int soilData = Integer.parseInt(soilmoisture);


                if (soilData == 0) {

                    text_soilmoisture.setText("Moist - No watering required.");
                  //  text_soilmoisture.setTextColor(col2);

                }
                else {
                    text_soilmoisture.setText("Dry - Watering required!");
                   // text_soilmoisture.setTextColor(col1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_waterOn.setOnClickListener(view1 -> {
            txt_watering.setText("Watering has Started");
            dref.child("motor").child("status").setValue(1);
            //dref.child("time").setValue()
        });

        btn_waterOff.setOnClickListener(view1 -> {
            txt_watering.setText("Watering has Stopped");
            dref.child("motor").child("status").setValue(0);
        });

        return view;
    }


}