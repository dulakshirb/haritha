package com.haritha.haritha_farmer;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BioGasFragment extends Fragment {

    private TextView txt_view_lpg, txt_view_temperature, txt_view_humidity, register_title_one, txt_view_currentInputStatus, txt_view_daysSpend, txt_view_startDateToCollectWastage;
    private EditText txt_input_wastage, txt_cylinders;
    private Button btn_dialog_no;
    private Button btn_dialog_yes;
    private Button btn_dialog_resetNo;
    private Button btn_dialog_resetYes;
    private ToggleButton btn_on_off_bioGasUnit;
    private Dialog addDialogConfirmation, resetDialogConfirmation;
    private LinearLayout bg_status_of_plant, bg_of_other_details;
    private DatabaseReference dbReferenceBioGas;
    private float current_wastage_amount = (float) 0 , input_wastage = (float) 0;
    private BiogasAdapter biogasAdapter;

    public BioGasFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bio_gas, container, false);

        txt_view_lpg = view.findViewById(R.id.txt_view_lpg);
        txt_view_temperature = view.findViewById(R.id.txt_view_temperature);
        txt_view_humidity = view.findViewById(R.id.txt_view_humidity);
        register_title_one = view.findViewById(R.id.register_title_one);
        btn_on_off_bioGasUnit = view.findViewById(R.id.btn_on_off_bioGasUnit);
        bg_status_of_plant = view.findViewById(R.id.bg_of_plant_status);
        txt_input_wastage = view.findViewById(R.id.txt_input_wastage);
        txt_view_currentInputStatus = view.findViewById(R.id.txt_view_currentInputStatus);
        txt_view_daysSpend = view.findViewById(R.id.txt_view_daysSpend);
        txt_view_startDateToCollectWastage = view.findViewById(R.id.txt_view_startDateToCollectWastage);
        Button btn_addWastage = view.findViewById(R.id.btn_addWastage);
        Button btn_resetWastage = view.findViewById(R.id.btn_resetWastage);
        bg_of_other_details = view.findViewById(R.id.bg_of_other_details);
        Button btn_submitToSell = view.findViewById(R.id.btn_submitToSell);
        txt_cylinders = view.findViewById(R.id.txt_cylinders);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        String userId = currentUser.getUid();
        dbReferenceBioGas = FirebaseDatabase.getInstance().getReference("Farmer").child("BioGas").child(userId);

        //check the current status
        dbReferenceBioGas.child("unit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BiogasUnit biogasUnit = snapshot.getValue(BiogasUnit.class);
                if (biogasUnit != null) {
                    if (biogasUnit.status == 0) {
                        showBioGasPlantStatus();
                        register_title_one.setText("Last status in Bio Gas Plant");
                        btn_on_off_bioGasUnit.setTextOff("Unit Off");
                        //    btn_on_off_bioGasUnit.setBackgroundResource(R.drawable.button_red);
                    } else if (biogasUnit.status == 1) {
                        showBioGasPlantStatus();
                        register_title_one.setText("Current status in Bio Gas Plant");
                        btn_on_off_bioGasUnit.setTextOn("Unit On");
                        //    btn_on_off_bioGasUnit.setBackgroundResource(R.drawable.button_green);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //set button
        btn_on_off_bioGasUnit.setOnClickListener(view1 -> {
            if (btn_on_off_bioGasUnit.isChecked()) {
                showBioGasPlantStatus();
                register_title_one.setText("Current status in Bio Gas Plant");
                dbReferenceBioGas.child("unit").child("status").setValue(1);
            } else {
                showBioGasPlantStatus();
                register_title_one.setText("Last status in Bio Gas Plant");
                dbReferenceBioGas.child("unit").child("status").setValue(0);
            }
        });

        //view current wastage amount
        showCurrentWastage();

        //input wastage
        addDialogInitializing();
        btn_addWastage.setOnClickListener(view12 -> {
            if (txt_input_wastage.length() > 0) {
                input_wastage = Float.parseFloat(txt_input_wastage.getText().toString().trim());
                addDialogConfirmation.show();
            } else {
                txt_input_wastage.setError("This field is required.");
                txt_input_wastage.requestFocus();
            }
        });
        btn_dialog_no.setOnClickListener(view13 -> addDialogConfirmation.dismiss());
        btn_dialog_yes.setOnClickListener(view14 -> {
            inputWastage();
            addDialogConfirmation.dismiss();
            txt_input_wastage.setText("");
        });

        //reset wastage
        resetDialogInitializing();
        btn_resetWastage.setOnClickListener(view15 -> resetDialogConfirmation.show());
        btn_dialog_resetNo.setOnClickListener(view16 -> resetDialogConfirmation.dismiss());
        btn_dialog_resetYes.setOnClickListener(view17 -> {
            resetWastage();
            resetDialogConfirmation.dismiss();
            txt_input_wastage.setText("");
        });

        //sell cylinders
        btn_submitToSell.setOnClickListener(view18 -> {
            if((txt_cylinders.getText().toString().trim()).isEmpty()){
                txt_cylinders.setError("This field is required.");
                txt_cylinders.requestFocus();
            }
            listToSellCylinder();
        });

        //view sell cylinders
        RecyclerView biogasSellRecyclerView = view.findViewById(R.id.rv_sells);
        biogasSellRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseRecyclerOptions<BiogasSell> options = new FirebaseRecyclerOptions.Builder<BiogasSell>()
                .setQuery(dbReferenceBioGas.child("sell"), BiogasSell.class)
                .build();
        biogasAdapter = new BiogasAdapter(options);
        biogasSellRecyclerView.setAdapter(biogasAdapter);

        if (currentUser == null) {
            sendUserToMainActivity();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        biogasAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        biogasAdapter.stopListening();
    }

    private void listToSellCylinder() {
        int cylinders = Integer.parseInt(txt_cylinders.getText().toString());
        String sell_id = dbReferenceBioGas.child("sell").push().getKey();
        Calendar calendarToday = Calendar.getInstance();
        long today = calendarToday.getTimeInMillis();
        BiogasSell biogasSell = new BiogasSell(cylinders, String.valueOf(today));
        assert sell_id != null;
        dbReferenceBioGas.child("sell").child(sell_id).setValue(biogasSell);
        txt_cylinders.setText("");
    }

    private void resetWastage() {
        dbReferenceBioGas.child("wastage").child("wastage_amount").setValue(0);
        bg_of_other_details.setVisibility(View.GONE);
        current_wastage_amount = 0;
    }

    private void inputWastage() {
        if (current_wastage_amount == 0) {
            //current date
            Calendar calendarToday = Calendar.getInstance();
            long today = calendarToday.getTimeInMillis();
            current_wastage_amount = current_wastage_amount + input_wastage;
            System.out.println("current_wastage_amount " + current_wastage_amount);
            dbReferenceBioGas.child("wastage").child("wastage_amount").setValue(current_wastage_amount);
            dbReferenceBioGas.child("wastage").child("started_date").setValue(String.valueOf(today));
        } else {
            current_wastage_amount = current_wastage_amount + input_wastage;
            dbReferenceBioGas.child("wastage").child("wastage_amount").setValue(current_wastage_amount);
        }
    }

    private void addDialogInitializing() {
        addDialogConfirmation = new Dialog(getActivity());
        addDialogConfirmation.setContentView(R.layout.add_dialog);
        addDialogConfirmation.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_background);
        addDialogConfirmation.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addDialogConfirmation.setCancelable(false);
        addDialogConfirmation.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        btn_dialog_no = addDialogConfirmation.findViewById(R.id.btn_dialog_no);
        btn_dialog_yes = addDialogConfirmation.findViewById(R.id.btn_dialog_yes);
    }

    private void resetDialogInitializing() {
        resetDialogConfirmation = new Dialog(getActivity());
        resetDialogConfirmation.setContentView(R.layout.reset_dialog);
        resetDialogConfirmation.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_background);
        resetDialogConfirmation.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        resetDialogConfirmation.setCancelable(false);
        resetDialogConfirmation.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        btn_dialog_resetNo = resetDialogConfirmation.findViewById(R.id.btn_dialog_resetNo);
        btn_dialog_resetYes = resetDialogConfirmation.findViewById(R.id.btn_dialog_resetYes);
    }

    @Override
    public void onResume() {
        super.onResume();
        dbReferenceBioGas.child("unit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showCurrentWastage();
                BiogasUnit biogasUnit = snapshot.getValue(BiogasUnit.class);
                if (biogasUnit != null) {
                    if (biogasUnit.status == 0) {
                        showBioGasPlantStatus();
                        register_title_one.setText("Last status in Bio Gas Plant");
                        btn_on_off_bioGasUnit.setTextOff("Unit Off");
                    } else if (biogasUnit.status == 1) {
                        showBioGasPlantStatus();
                        register_title_one.setText("Current status in Bio Gas Plant");
                        btn_on_off_bioGasUnit.setTextOn("Unit On");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showBioGasPlantStatus() {

        dbReferenceBioGas.child("unit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                BiogasUnit biogasUnit = snapshot.getValue(BiogasUnit.class);
                if (biogasUnit != null) {

                    txt_view_lpg.setText(biogasUnit.lpg.toString());
                    txt_view_temperature.setText(biogasUnit.temperature.toString());
                    txt_view_humidity.setText(biogasUnit.humidity.toString());
                    bg_status_of_plant.setBackgroundColor(Color.parseColor("#37C85E"));

                    if (biogasUnit.lpg > 300) {
                        System.out.println(biogasUnit.lpg);
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

    private void showCurrentWastage() {
        dbReferenceBioGas.child("wastage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("wastage_amount"))) {
                    current_wastage_amount = Float.parseFloat(Objects.requireNonNull(snapshot.child("wastage_amount").getValue()).toString());

                    //days pass calculate
                    Calendar calendarToday = Calendar.getInstance();
                    long today = calendarToday.getTimeInMillis();
                    long startedDate = Long.parseLong(Objects.requireNonNull(snapshot.child("started_date").getValue()).toString());
                    long duration = today - startedDate;
                    long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

                    Date dateStartedDate = new Date(startedDate);
                    DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    txt_view_daysSpend.setText(String.valueOf(diffInDays));
                    txt_view_startDateToCollectWastage.setText(simpleDateFormat.format(dateStartedDate));

                    if (current_wastage_amount == 0) {
                        bg_of_other_details.setVisibility(View.GONE);
                    } else {
                        bg_of_other_details.setVisibility(View.VISIBLE);
                    }
                }
                txt_view_currentInputStatus.setText(String.valueOf(current_wastage_amount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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