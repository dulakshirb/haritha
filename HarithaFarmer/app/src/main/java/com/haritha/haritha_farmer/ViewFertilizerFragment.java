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
        this.fertilizer_description = fertilizer_description;
        this.fertilizer_crops_touse = fertilizer_crops_touse;
        this.fertilizer_amount = fertilizer_amount;
        this.fertilizer_restock_amount = fertilizer_restock_amount;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_view_fertilizer, container, false);

        //Database reference and get current user Id
        mAuth = FirebaseAuth.getInstance();
       // currentUserID = mAuth.getCurrentUser().getUid();
       // dbFertilizer = FirebaseDatabase.getInstance().getReference("Farmer").child("Fertilizer").child(currentUserId).child(fertilizerId);

        viewFertilizerFragmentFieldsInitializing();
        viewFertilizerFragmentSetValues();

        //Remove button on cropViewFragment
        deleteDialogInitializing();
        btnRemove.setOnClickListener(remove -> {
            deleteDialog.show();
        });

        btnCancel.setOnClickListener(cancelBtn -> {
            deleteDialog.dismiss();
        });

        btnDelete.setOnClickListener(deleteBtn -> {
           // deleteFertilizer(fertilizerId);
            deleteDialog.dismiss();
        });

        btnEdit.setOnClickListener(edit -> {

            //onEditPressed();
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

    private void deleteFertilizer(String cropId) {
        dbFertilizer.removeValue();
        sendUsertoFertilizerFragment();
    }
    private void sendUsertoFertilizerFragment() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment fertilizerFragment = new FertilizerFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, fertilizerFragment).addToBackStack(null).commit();
    }


}