package com.haritha.haritha_farmer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BioGasFragment extends Fragment {

    private View view;
    private TextView txt_view_lpg, txt_view_temperature, txt_view_humidity, register_title_one;
    private ToggleButton btn_on_off_bioGasUnit;
    private LinearLayout bg_status_of_plant;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference dbReferenceBioGas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bio_gas, container, false);

        txt_view_lpg = view.findViewById(R.id.txt_view_lpg);
        txt_view_temperature = view.findViewById(R.id.txt_view_temperature);
        txt_view_humidity = view.findViewById(R.id.txt_view_humidity);
        register_title_one = view.findViewById(R.id.register_title_one);
        btn_on_off_bioGasUnit = view.findViewById(R.id.btn_on_off_bioGasUnit);
        bg_status_of_plant = view.findViewById(R.id.bg_status_of_plant);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        dbReferenceBioGas = FirebaseDatabase.getInstance().getReference("Farmer").child("BioGas").child(userId).child("unit");

        /*btn_on_off_bioGasUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_on_off_bioGasUnit.isChecked()) {
                    //on bio gas unit
                    dbReferenceBioGas.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Biogas biogas = snapshot.getValue(Biogas.class);
                            if (biogas != null) {
                                if (biogas.status == 1) {
                                    showBioGasPlantStatus(currentUser);
                                    btn_on_off_bioGasUnit.setBackgroundResource(R.drawable.button_green);
                                    register_title_one.setText("Current status in Bio Gas Plant");
                                }else if(biogas.status == 0){
                                    btn_on_off_bioGasUnit.setBackgroundResource(R.drawable.button_red);
                                    register_title_one.setText("Last status in Bio Gas Plant");
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                } else {
                    //off bio gas unit
                    showBioGasPlantStatus(currentUser);
                    btn_on_off_bioGasUnit.setBackgroundResource(R.drawable.button_red);
                    register_title_one.setText("Last status in Bio Gas Plant");
                }
            }
        });*/

        //check the current status
        dbReferenceBioGas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Biogas biogas = snapshot.getValue(Biogas.class);
                if (biogas != null){
                    if(biogas.status == 0){
                        showBioGasPlantStatus(currentUser);
                        register_title_one.setText("Last status in Bio Gas Plant");
                        btn_on_off_bioGasUnit.setTextOff("OFF");
                    //    btn_on_off_bioGasUnit.setBackgroundResource(R.drawable.button_red);
                    }else if(biogas.status == 1){
                        showBioGasPlantStatus(currentUser);
                        register_title_one.setText("Current status in Bio Gas Plant");
                        btn_on_off_bioGasUnit.setTextOn("ON");
                    //    btn_on_off_bioGasUnit.setBackgroundResource(R.drawable.button_green);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //set button
        btn_on_off_bioGasUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_on_off_bioGasUnit.isChecked()){
                    showBioGasPlantStatus(currentUser);
                    register_title_one.setText("Current status in Bio Gas Plant");
                //    btn_on_off_bioGasUnit.setTextOn("ON");
                 //   btn_on_off_bioGasUnit.setBackgroundResource(R.drawable.button_red);
                    dbReferenceBioGas.child("status").setValue(1);
                }else {
                    showBioGasPlantStatus(currentUser);
                    register_title_one.setText("Last status in Bio Gas Plant");
                 //   btn_on_off_bioGasUnit.setTextOff("OFF");
                //    btn_on_off_bioGasUnit.setBackgroundResource(R.drawable.button_green);
                    dbReferenceBioGas.child("status").setValue(0);
                }
            }
        });

        if (currentUser == null) {
            sendUserToMainActivity();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dbReferenceBioGas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Biogas biogas = snapshot.getValue(Biogas.class);
                if (biogas != null){
                    if(biogas.status == 0){
                        showBioGasPlantStatus(currentUser);
                        register_title_one.setText("Last status in Bio Gas Plant");
                        btn_on_off_bioGasUnit.setTextOff("OFF");
                        //    btn_on_off_bioGasUnit.setBackgroundResource(R.drawable.button_red);
                    }else if(biogas.status == 1){
                        showBioGasPlantStatus(currentUser);
                        register_title_one.setText("Current status in Bio Gas Plant");
                        btn_on_off_bioGasUnit.setTextOn("ON");
                        //    btn_on_off_bioGasUnit.setBackgroundResource(R.drawable.button_green);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showBioGasPlantStatus(FirebaseUser currentUser) {
        dbReferenceBioGas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Biogas biogas = snapshot.getValue(Biogas.class);
                if (biogas != null) {
                    txt_view_lpg.setText(biogas.lpg.toString());
                    txt_view_temperature.setText(biogas.temperature.toString());
                    txt_view_humidity.setText(biogas.humidity.toString());

                    bg_status_of_plant.setBackgroundColor(Color.parseColor("#37C85E"));

                    if (biogas.lpg > 300) {
                        System.out.println(biogas.lpg);
                        bg_status_of_plant.setBackgroundColor(Color.parseColor("#EF9920"));
                    }
                } else {
                    sendUserToMainActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                sendUserToMainActivity();
            }
        });
    }


    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        getActivity().finish();
    }
}