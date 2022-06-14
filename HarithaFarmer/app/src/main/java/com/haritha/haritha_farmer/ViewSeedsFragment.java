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

public class ViewSeedsFragment extends Fragment {

    private String seed_name, seed_description, seed_variety, seed_id, seed_type;
    private Float seed_amount, seed_restock_amount;
    private Button btnEdit, btnUpdateSeeds;
    private AppCompatButton btnRemove, btnCancel, btnDelete;
    private Dialog deleteDialog;
    private TextView txt_viewSName, txt_viewSDescription, txt_viewSVariety, txt_viewSType, txt_viewSAmount, txt_viewSRestockAmount;

    private View view;
    private DatabaseReference dbSeeds;
    private FirebaseAuth mAuth;

    public ViewSeedsFragment(String seed_id, String seed_name, String seed_description, String seed_variety, String seed_type, Float seed_amount, Float seed_restockAmount) {
        this.seed_id = seed_id;
        this.seed_name = seed_name;
        this.seed_description = seed_description;
        this.seed_variety = seed_variety;
        this.seed_type = seed_type;
        this.seed_amount = seed_amount;
        this.seed_restock_amount = seed_restockAmount;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_seeds, container, false);

        // Database reference and get current user Id
        mAuth = FirebaseAuth.getInstance();
        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        dbSeeds = FirebaseDatabase.getInstance().getReference("Farmer").child("Seeds").child(currentUserID).child(seed_id);

       viewSeedsFragmentFieldsInitializing();
        viewSeedsFragmentSetValues();


        deleteDialogInitializing();
        btnRemove.setOnClickListener(remove -> {
            deleteDialog.show();
        });

        btnCancel.setOnClickListener(cancelBtn -> {
            deleteDialog.dismiss();
        });

        btnDelete.setOnClickListener(deleteBtn -> {
            deleteSeeds();
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
    private void viewSeedsFragmentFieldsInitializing() {
        txt_viewSName = (TextView) view.findViewById(R.id.txt_viewSName);
        txt_viewSDescription = (TextView) view.findViewById(R.id.txt_viewSDescription);
        txt_viewSVariety = (TextView) view.findViewById(R.id.txt_viewSVariety);
        txt_viewSType = (TextView) view.findViewById(R.id.txt_viewSType);
        txt_viewSAmount = (TextView) view.findViewById(R.id.txt_viewSAmount);
        txt_viewSRestockAmount = (TextView) view.findViewById(R.id.txt_viewSRestockAmount);


        btnEdit = (Button) view.findViewById(R.id.btn_edit_seeds);
        btnRemove = (AppCompatButton) view.findViewById(R.id.btn_deleteSeeds);
    }

    private void viewSeedsFragmentSetValues() {
        txt_viewSName.setText(seed_name);
        txt_viewSDescription.setText(seed_description);
        txt_viewSVariety.setText(seed_variety);
        txt_viewSType.setText(seed_type);
        txt_viewSAmount.setText(seed_amount.toString());
                txt_viewSRestockAmount.setText(seed_restock_amount.toString());
    }

    private void deleteSeeds() {
        if (dbSeeds != null)
            dbSeeds.removeValue();
        sendUsertoSeedsFragment();
    }

    public void onEditPressed(){

        //open fragment_update_fertilizer dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_update_seeds, null);
        dialogBuilder.setView(dialogView);

        final EditText txt_updateSeedName = dialogView.findViewById(R.id.txt_update_seedName);
        final EditText txt_update_seedDescription = dialogView.findViewById(R.id.txt_update_seedDescription);
        final EditText txt_update_seedType = dialogView.findViewById(R.id.txt_update_seedType);
        final EditText txt_update_seedVariety = dialogView.findViewById(R.id.txt_update_seedVariety);
        final EditText txt_update_seedAmount = dialogView.findViewById(R.id.txt_update_seedAmount);
        final EditText txt_update_seedRestockAmount = dialogView.findViewById(R.id.txt_update_seedRestockAmount);

        Button btnUpdateSeeds = dialogView.findViewById(R.id.btn_seed_Update);
        dialogBuilder.setTitle("Updating " + seed_name);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        txt_updateSeedName.setText(seed_name);
        txt_update_seedDescription.setText(seed_description);
        txt_update_seedType.setText(seed_type);
        txt_update_seedVariety.setText(seed_variety);
        txt_update_seedAmount.setText(String.valueOf(seed_amount));
        txt_update_seedRestockAmount.setText(String.valueOf(seed_restock_amount));

        btnUpdateSeeds.setOnClickListener(view1 -> {

            if (txt_updateSeedName.getText().toString().trim().isEmpty()) {
                txt_updateSeedName.setError("This field is required!");
                txt_updateSeedName.requestFocus();
                return;
            }
            if (txt_update_seedDescription.getText().toString().trim().isEmpty()) {
                txt_update_seedDescription.setError("This field is required!");
                txt_update_seedDescription.requestFocus();
                return;
            }
            seed_type = txt_update_seedType.getText().toString().trim();
            seed_variety = txt_update_seedVariety.getText().toString().trim();
            seed_amount = Float.parseFloat(txt_update_seedAmount.getText().toString().trim());
            seed_restock_amount = Float.parseFloat(txt_update_seedRestockAmount.getText().toString().trim());

            if (updateSeeds(seed_id, seed_name, seed_description, seed_variety, seed_type, seed_amount, seed_restock_amount)) {
                alertDialog.dismiss();
                sendUsertoSeedsFragment();
            } else {
                Toast.makeText(getActivity(), "Failed to update, please try again!", Toast.LENGTH_SHORT).show();
            }


        });
    }

    private void sendUsertoSeedsFragment() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment seedsFragment = new SeedsFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, seedsFragment).addToBackStack(null).commit();
    }
    private boolean updateSeeds(String seed_id, String seed_name, String seed_description, String seed_type, String seed_variety, Float seed_amount,
                                     Float seed_restock_amount) {

        Seeds seeds = new Seeds(seed_id, seed_name, seed_description, seed_type, seed_variety, seed_amount, seed_restock_amount);


        dbSeeds.setValue(seeds);
        return true;
    }
}