package com.haritha.harithaagriofficer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private TextView txt_show_name, txt_show_email,
            txt_show_phone, txt_show_gender, txt_show_district;
    private ImageView img_profile;
    private ProgressDialog loadingBar;
    private String name, email, phone, gender, district;
    private FirebaseAuth mAuth;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        txt_show_name = view.findViewById(R.id.txt_show_name);
        txt_show_email = view.findViewById(R.id.txt_show_email);
        txt_show_phone = view.findViewById(R.id.txt_show_phone);
        txt_show_gender = view.findViewById(R.id.txt_show_gender);
        txt_show_district = view.findViewById(R.id.txt_show_district);
        loadingBar = new ProgressDialog(getActivity());

        //set onClickListener on ImageView to Open ProfilePictureUploadFragment
        img_profile = view.findViewById(R.id.img_profile);
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment profilePictureUploadFragment = new ProfilePictureUploadFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, profilePictureUploadFragment).addToBackStack(null).commit();
            }
        });

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
        if (!currentUser.isEmailVerified()) {
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
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Officer").child("Users");
        referenceUser.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    name = currentUser.getDisplayName();
                    email = currentUser.getEmail();
                    phone = readUserDetails.mobile;
                    gender = readUserDetails.gender;
                    district = readUserDetails.district;

                    txt_show_name.setText(name);
                    txt_show_email.setText(email);
                    txt_show_phone.setText(phone);
                    txt_show_gender.setText(gender);
                    txt_show_district.setText(district);

                    //Set user DP
                    if (currentUser.getPhotoUrl() != null) {
                        Uri uri = currentUser.getPhotoUrl();
                        Picasso.get().load(uri).into(img_profile);
                    }

                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
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

    //create action bar menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //when any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.menu_update_profile) {
            fragment = new ProfileUpdateFragment();
            loadFragment(fragment);
        } else if (id == R.id.menu_update_email) {
            fragment = new ProfileUpdateEmailFragment();
            loadFragment(fragment);
        } else if (id == R.id.menu_change_password) {
            fragment = new ProfileChangePasswordFragment();
            loadFragment(fragment);
        } else if (id == R.id.menu_delete_profile) {
            fragment = new ProfileDeleteFragment();
            loadFragment(fragment);
        } else if (id == R.id.menu_logout) {
            mAuth.signOut();
            Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();
            sendUserToMainActivity();
        } else {
            Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        getActivity().finish();
    }

}