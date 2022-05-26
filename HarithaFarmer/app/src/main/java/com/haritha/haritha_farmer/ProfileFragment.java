package com.haritha.haritha_farmer;

import static android.app.Activity.RESULT_FIRST_USER;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.transform.Result;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private View view;
    private CircleImageView profileImage;
    private EditText farmName, userName;
    private Button btnUpdateProfile;
    private ProgressDialog loadingBar;

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

                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait, your profile image is uploading...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                StorageReference filePath = userProfileImagesRef.child(currentUserId + ".jpg");
/*                filePath.putFile(result).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Profile Image Uploaded Successfully..", Toast.LENGTH_SHORT).show();
                            String downloadedUrl = task.getResult().getDownloadUrl.toString();
                            Log.d("url", downloadedUrl);
                            rootRef.child("Farmer").child("Users").child(currentUserId).child("image").setValue(downloadedUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Image save in database successfully.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    } else {
                                        String message = task.getException().toString();
                                        Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });*/
                filePath.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getActivity(), "Profile Image Uploaded Successfully...", Toast.LENGTH_SHORT).show();
                        final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                rootRef.child("Farmer").child("Users").child(currentUserId).child("image").setValue(downloadUrl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getActivity(), "Image save in database successfully.", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                } else {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });
                            }
                        });
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

        loadingBar = new ProgressDialog(getActivity());
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
                        if ((snapshot.exists()) && (snapshot.hasChild("farmName") && snapshot.hasChild("image"))) {
                            String retFarmName = snapshot.child("farmName").getValue().toString();
                            String retUserName = snapshot.child("userName").getValue().toString();
                            String retProfileImage = snapshot.child("image").getValue().toString();
                            farmName.setText(retFarmName);
                            userName.setText(retUserName);
                            System.out.println(retProfileImage);
                            Picasso.get().load(retProfileImage).into(profileImage);
                            //Glide.with(ProfileFragment.this).load(retProfileImage).into(profileImage);
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