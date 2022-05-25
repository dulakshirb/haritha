package com.haritha.haritha_farmer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private View view;
    private CircleImageView profileImage;
    private EditText farmName, userName;
    private Button btnUpdateProfile;
    private String currentUserId;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        initializeFields();

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        return view;
    }

    private void initializeFields() {
        profileImage = (CircleImageView) view.findViewById(R.id.set_profile_image);
        farmName = (EditText) view.findViewById(R.id.set_farm_name);
        userName = (EditText) view.findViewById(R.id.set_user_name);
        btnUpdateProfile = (Button) view.findViewById(R.id.update_profile_button);
    }

    private void updateProfile() {
        String setFarmName = farmName.getText().toString().trim();
        String setUserName = userName.getText().toString().trim();

        if (TextUtils.isEmpty(setFarmName)) {
            farmName.setError("Please write your farm name");
            farmName.requestFocus();
        }
        if (TextUtils.isEmpty(setUserName)) {
            userName.setError("Please enter your name");
            userName.requestFocus();
        } else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("userID", currentUserId);
            profileMap.put("farmName", setFarmName);
            profileMap.put("userName", setUserName);
            rootRef.child("Farmer").child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendUsertoMainActivity();
                                Toast.makeText(getActivity(), "Profile updated successfully...", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String message = task.getException().toString();
                                System.out.println(message);
                                Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void sendUsertoMainActivity() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
    }
}