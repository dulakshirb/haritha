package com.haritha.haritha_farmer;

import static android.app.Activity.RESULT_FIRST_USER;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.transform.Result;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private View view;
    private CircleImageView profileImage;
    private EditText farmName, userName;
    private Button btnUpdateProfile;

    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private StorageReference userProfileImagesRef;

    ActivityResultLauncher<String> activityResultLauncher;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                profileImage.setImageURI(result);

                StorageReference filePath = userProfileImagesRef.child(currentUserId + ".jpg");
                filePath.putFile(result).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(), "Profile Image Uploaded Successfully..", Toast.LENGTH_SHORT).show();
                        }else {
                            String message = task.getException().toString();
                            Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        initializeFields();
        retrieveProfileInfo();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityResultLauncher.launch("image/*");
            }
        });

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
                            if (task.isSuccessful()) {
                                sendUsertoMainActivity();
                                Toast.makeText(getActivity(), "Profile updated successfully...", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = task.getException().toString();
                                System.out.println(message);
                                Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void retrieveProfileInfo() {
        rootRef.child("Farmer").child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if ((snapshot.exists()) && (snapshot.hasChild("farmName") && snapshot.hasChild("profileImage"))) {
                            String retFarmName = snapshot.child("farmName").getValue().toString();
                            String retUserName = snapshot.child("userName").getValue().toString();
                            String retProfileImage = snapshot.child("profileImage").getValue().toString();
                            farmName.setText(retFarmName);
                            userName.setText(retUserName);
                        } else if ((snapshot.exists()) && (snapshot.hasChild("farmName"))) {
                            String retFarmName = snapshot.child("farmName").getValue().toString();
                            String retUserName = snapshot.child("userName").getValue().toString();
                            farmName.setText(retFarmName);
                            userName.setText(retUserName);
                        } else {
                            Toast.makeText(getActivity(), "Please set & update your profile information.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void sendUsertoMainActivity() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
    }
}