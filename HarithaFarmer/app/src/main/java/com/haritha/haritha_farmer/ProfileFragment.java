package com.haritha.haritha_farmer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView txt_show_farm_name, txt_show_name, txt_show_email,
            txt_show_phone, txt_show_gender, txt_show_location, txt_show_country;
    private ImageView img_profile;
    private ProgressDialog loadingBar;
    private String farmName, name, email, phone, gender, location, country;
    private FirebaseAuth mAuth;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txt_show_farm_name = view.findViewById(R.id.txt_show_farm_name);
        txt_show_name = view.findViewById(R.id.txt_show_name);
        txt_show_email = view.findViewById(R.id.txt_show_email);
        txt_show_phone = view.findViewById(R.id.txt_show_phone);
        txt_show_gender = view.findViewById(R.id.txt_show_gender);
        txt_show_location = view.findViewById(R.id.txt_show_location);
        txt_show_country = view.findViewById(R.id.txt_show_country);
        loadingBar = new ProgressDialog(getActivity());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getActivity(), "Something went wrong! User's details are not available at the moment.", Toast.LENGTH_LONG).show();
        } else {
            checkIfEmailVerified(currentUser);
            loadingBar.setTitle("Profile");
            loadingBar.setMessage("Loading...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            showUserProfile(currentUser);
        }

        return view;
    }

    private void checkIfEmailVerified(FirebaseUser currentUser) {
        if(!currentUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Email Not Verified.");
        builder.setMessage("Please verify your email now. You can not login without email verification next time.");

        //open email app if user clicks/taps continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUserProfile(FirebaseUser currentUser) {
        String userID = currentUser.getUid();

        //extracting user reference from Database
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Farmer").child("Users");
        referenceUser.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    name = currentUser.getDisplayName();
                    email = currentUser.getEmail();
                    phone = readUserDetails.mobile;
                    gender = readUserDetails.gender;
                    farmName = readUserDetails.farmName;
                    location = readUserDetails.location;
                    country = readUserDetails.country;

                    txt_show_farm_name.setText(farmName);
                    txt_show_name.setText(name);
                    txt_show_email.setText(email);
                    txt_show_phone.setText(phone);
                    txt_show_gender.setText(gender);
                    txt_show_location.setText(location);
                    txt_show_country.setText(country);
                }
                loadingBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
            }
        });
    }

}