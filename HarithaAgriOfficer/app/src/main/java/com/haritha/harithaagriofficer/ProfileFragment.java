package com.haritha.harithaagriofficer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private View view;
    private CircleImageView profileImage;
    private EditText userName;
    private Button btnUpdateProfile;
    private ProgressDialog loadingBar;

    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private StorageReference userProfileImagesRef;
    String downloadUrl;

    ActivityResultLauncher<String> activityResultLauncher;

    public ProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Officer Profile Images");

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            profileImage.setImageURI(result);

            loadingBar.setTitle("Set Profile Image");
            loadingBar.setMessage("Please wait, your profile image is uploading...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            StorageReference filePath = userProfileImagesRef.child(currentUserId + ".jpg");
            filePath.putFile(result).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(getActivity(), "Profile Image Uploaded Successfully...", Toast.LENGTH_SHORT).show();
                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                firebaseUri.addOnSuccessListener(uri -> {
                    downloadUrl = uri.toString();
                    rootRef.child("Officer").child("Users").child(currentUserId).child("image").setValue(downloadUrl)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Image save in database successfully.", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                    retrieveProfileInfo();
                                } else {
                                    String message = Objects.requireNonNull(task.getException()).toString();
                                    Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            });
                });
            });
        });

        initializeFields();
        retrieveProfileInfo();

        profileImage.setOnClickListener(view -> activityResultLauncher.launch("image/*"));

        btnUpdateProfile.setOnClickListener(view -> updateProfile());

        return view;
    }
    private void initializeFields() {
        profileImage = (CircleImageView) view.findViewById(R.id.set_profile_image);
        userName = (EditText) view.findViewById(R.id.set_user_name);
        btnUpdateProfile = (Button) view.findViewById(R.id.update_profile_button);

        loadingBar = new ProgressDialog(getActivity());
    }
    private void updateProfile() {
        String setUserName = userName.getText().toString().trim();

        if (TextUtils.isEmpty(setUserName)) {
            userName.setError("Please enter your name");
            userName.requestFocus();
        } else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("userID", currentUserId);
            profileMap.put("userName", setUserName);
            profileMap.put("image", downloadUrl);
            rootRef.child("Officer").child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            sendUserToMainActivity();
                            Toast.makeText(getActivity(), "Profile updated successfully...", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = Objects.requireNonNull(task.getException()).toString();
                            System.out.println(message);
                            Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void retrieveProfileInfo() {
        rootRef.child("Officer").child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if ((snapshot.exists()) && (snapshot.hasChild("userName") && snapshot.hasChild("image"))) {
                            String retUserName = Objects.requireNonNull(snapshot.child("userName").getValue()).toString();
                            String retProfileImage = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                            userName.setText(retUserName);
                            System.out.println(retProfileImage);
                            Picasso.get().load(retProfileImage).into(profileImage);
                        } else if ((snapshot.exists()) && (snapshot.hasChild("userName"))) {
                            String retUserName = Objects.requireNonNull(snapshot.child("userName").getValue()).toString();
                            userName.setText(retUserName);
                        } else if ((snapshot.exists()) && snapshot.hasChild("image")){
                            String retProfileImage = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                            Picasso.get().load(retProfileImage).into(profileImage);
                        } else{
                            Toast.makeText(getActivity(), "Please set & update your profile information.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
    }
}