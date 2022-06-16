package com.haritha.haritha_farmer;

import android.os.Bundle;


import android.app.AlertDialog;

import android.app.Dialog;
import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class ViewFertilizerFragment extends Fragment {

    private String fertilizer_name, fertilizer_description, fertilizer_crops_touse, fertilizer_id;
    private Float fertilizer_amount, fertilizer_restock_amount;
    private Button btnEdit, btnUpdateFertilizer;
    private AppCompatButton btnRemove, btnCancel, btnDelete;
    private Dialog deleteDialog;
    private TextView txt_viewFName, txt_viewFDescription, txt_viewFCrops, txt_viewFAmount, txt_viewFRestockAmount;

    private View view;
    private DatabaseReference dbFertilizer;
    private FirebaseAuth mAuth;

    public ViewFertilizerFragment(String fertilizer_id, String fertilizer_name, String fertilizer_description, String fertilizer_crops_touse, Float fertilizer_amount, Float fertilizer_restock_amount) {
        this.fertilizer_id = fertilizer_id;
        this.fertilizer_name = fertilizer_name;
        this.fertilizer_description = fertilizer_description;
        this.fertilizer_crops_touse = fertilizer_crops_touse;
        this.fertilizer_amount = fertilizer_amount;
        this.fertilizer_restock_amount = fertilizer_restock_amount;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_view_fertilizer, container, false);

       // Database reference and get current user Id
        mAuth = FirebaseAuth.getInstance();
        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
       dbFertilizer = FirebaseDatabase.getInstance().getReference("Farmer").child("Fertilizer").child(currentUserID).child(fertilizer_id);

        viewFertilizerFragmentFieldsInitializing();
        viewFertilizerFragmentSetValues();


        deleteDialogInitializing();
        btnRemove.setOnClickListener(remove -> {
            deleteDialog.show();
        });

        btnCancel.setOnClickListener(cancelBtn -> {
            deleteDialog.dismiss();
        });

        btnDelete.setOnClickListener(deleteBtn -> {
            deleteFertilizer();
            deleteDialog.dismiss();
        });

        btnEdit.setOnClickListener(edit -> {

            onEditPressed();
        });


        return view;
    }

    private void deleteDialogInitializing() {
        deleteDialog = new Dialog(getActivity());
        deleteDialog.setContentView(R.layout.delete_dialog);
        deleteDialog.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_background);
        deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteDialog.setCancelable(false);
        deleteDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        btnCancel = deleteDialog.findViewById(R.id.btn_dialog_cancel);
        btnDelete = deleteDialog.findViewById(R.id.btn_dialog_delete);
    }
    private void viewFertilizerFragmentFieldsInitializing() {
        txt_viewFName = (TextView) view.findViewById(R.id.txt_viewFName);
        txt_viewFDescription = (TextView) view.findViewById(R.id.txt_viewFDescription);
        txt_viewFCrops = (TextView) view.findViewById(R.id.txt_viewFCrops);
        txt_viewFAmount = (TextView) view.findViewById(R.id.txt_viewFAmount);
        txt_viewFRestockAmount = (TextView) view.findViewById(R.id.txt_viewFRestockAmount);


        btnEdit = (Button) view.findViewById(R.id.btn_edit_fertilizer);
        btnRemove = (AppCompatButton) view.findViewById(R.id.btn_deleteFertilizer);
    }

    private void viewFertilizerFragmentSetValues() {
        txt_viewFName.setText(fertilizer_name);
        txt_viewFDescription.setText(fertilizer_description);
        txt_viewFCrops.setText(fertilizer_crops_touse);
        txt_viewFAmount.setText(fertilizer_amount.toString());
        txt_viewFRestockAmount.setText(fertilizer_restock_amount.toString());
    }

    private void deleteFertilizer() {
        if (dbFertilizer != null)
            dbFertilizer.removeValue();
        sendUsertoFertilizerFragment();
    }

    public void onEditPressed(){

        //open fragment_update_fertilizer dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_update_fertilizer, null);
        dialogBuilder.setView(dialogView);

        final EditText txt_fertilizerName = dialogView.findViewById(R.id.txt_fertilizerName);
        final Spinner spinner_update_fertilizerName = dialogView.findViewById(R.id.spinner_update_fertilizerName);
        final EditText txt_update_fertilizerDescription = dialogView.findViewById(R.id.txt_update_fertilizerDescription);
        final EditText txt_update_fertilizerCrops = dialogView.findViewById(R.id.txt_update_fertilizerCrops);
        final EditText txt_update_fertilizerAmount = dialogView.findViewById(R.id.txt_update_fertilizerAmount);
        final EditText txt_update_fertilizerRestockAmount = dialogView.findViewById(R.id.txt_update_fertilizerRestockAmount);

        Button btnUpdateFertilizer = dialogView.findViewById(R.id.btn_fertilizer_update);
        dialogBuilder.setTitle("Updating " + fertilizer_name);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        txt_fertilizerName.setText(fertilizer_name);
        txt_update_fertilizerDescription.setText(fertilizer_description);
        txt_update_fertilizerCrops.setText(fertilizer_crops_touse);
        txt_update_fertilizerAmount.setText(String.valueOf(fertilizer_amount));
        txt_update_fertilizerRestockAmount.setText(String.valueOf(fertilizer_restock_amount));

        btnUpdateFertilizer.setOnClickListener(view1 -> {
            if (!Objects.equals(fertilizer_name, spinner_update_fertilizerName.getSelectedItem().toString()))
                fertilizer_name = spinner_update_fertilizerName.getSelectedItem().toString();
            if (txt_update_fertilizerDescription.getText().toString().trim().isEmpty()) {
                txt_update_fertilizerDescription.setError("This field is required!");
                txt_update_fertilizerDescription.requestFocus();
                return;
            }
            fertilizer_crops_touse = txt_update_fertilizerCrops.getText().toString().trim();
            fertilizer_amount = Float.parseFloat(txt_update_fertilizerAmount.getText().toString().trim());
            fertilizer_restock_amount = Float.parseFloat(txt_update_fertilizerRestockAmount.getText().toString().trim());

            if (updateFertilizer(fertilizer_id, fertilizer_name, fertilizer_description, fertilizer_crops_touse, fertilizer_amount, fertilizer_restock_amount)) {
                alertDialog.dismiss();
                sendUsertoFertilizerFragment();
            } else {
                Toast.makeText(getActivity(), "Failed to update, please try again!", Toast.LENGTH_SHORT).show();
            }


        });
    }
    private void sendUsertoFertilizerFragment() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment fertilizerFragment = new FertilizerFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, fertilizerFragment).addToBackStack(null).commit();
    }

    private boolean updateFertilizer(String fertilizer_id, String fertilizer_name, String fertilizer_description, String fertilizer_crops_touse, Float fertilizer_amount,
                               Float fertilizer_restock_amount) {

        Fertilizer fertilizer = new Fertilizer(fertilizer_id, fertilizer_name, fertilizer_description, fertilizer_crops_touse, fertilizer_amount, fertilizer_restock_amount);


        dbFertilizer.setValue(fertilizer);
        return true;
    }


}