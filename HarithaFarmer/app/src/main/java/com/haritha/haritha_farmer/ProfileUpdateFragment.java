package com.haritha.haritha_farmer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileUpdateFragment extends Fragment {

    private View view;
    private EditText txt_update_name, txt_update_phone, txt_update_farm_name,
            txt_update_location;
    private RadioGroup rg_update_gender_group;
    private RadioButton rb_update_gender_selected;
    private Spinner sp_update_district;
    private ProgressDialog loadingBar;
    private String name, phone, gender, farmName, location, district;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_update, container, false);

        txt_update_name = view.findViewById(R.id.txt_update_name);
        txt_update_phone = view.findViewById(R.id.txt_update_phone);
        txt_update_farm_name = view.findViewById(R.id.txt_update_farm_name);
        txt_update_location = view.findViewById(R.id.txt_update_location);
        sp_update_district = view.findViewById(R.id.sp_update_district);
        rg_update_gender_group = view.findViewById(R.id.rg_update_gender_group);

        loadingBar = new ProgressDialog(getActivity());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //show profile data
        showProfile(currentUser);

        //update profile pic
        Button btn_update_profile_pic = view.findViewById(R.id.btn_update_profile_pic);
        btn_update_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment profilePictureUploadFragment = new ProfilePictureUploadFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, profilePictureUploadFragment).addToBackStack(null).commit();
            }
        });

        //update email
        Button btn_update_email = view.findViewById(R.id.btn_update_email);
        btn_update_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment ProfileUpdateEmailFragment = new ProfileUpdateEmailFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, ProfileUpdateEmailFragment).addToBackStack(null).commit();
            }
        });

        //update profile
        Button btn_update_profile = view.findViewById(R.id.btn_update_profile);
        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(currentUser);
            }
        });

        return view;
    }

    //update data
    private void updateProfile(FirebaseUser currentUser) {
        //selected gender id
        int selectedGenderId = rg_update_gender_group.getCheckedRadioButtonId();
        rb_update_gender_selected = view.findViewById(selectedGenderId);

        //validation
        if (TextUtils.isEmpty(name)) {
            txt_update_name.setError("Your Name is required.");
            txt_update_name.requestFocus();
        } else if (TextUtils.isEmpty(phone)) {
            txt_update_phone.setError("Mobile No. is required.");
            txt_update_phone.requestFocus();
        } else if (phone.length() != 10) {
            txt_update_phone.setError("Mobile no. should be 10 digits.");
            txt_update_phone.requestFocus();
        } else if (TextUtils.isEmpty(rb_update_gender_selected.getText())) {
            Toast.makeText(getActivity(), "Gender is required.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(farmName)) {
            txt_update_farm_name.setError("Farm Name is required.");
            txt_update_farm_name.requestFocus();
        } else if (TextUtils.isEmpty(location)) {
            txt_update_location.setError("Location is required.");
            txt_update_location.requestFocus();
        } else {
            //obtain the data entered by user
            name = txt_update_name.getText().toString().trim();
            phone = txt_update_phone.getText().toString().trim();
            farmName = txt_update_farm_name.getText().toString().trim();
            location = txt_update_location.getText().toString().trim();
            district = sp_update_district.getSelectedItem().toString();
            gender = rb_update_gender_selected.getText().toString();

            //update user data in firebase
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(currentUser.getUid(), name, currentUser.getEmail(), phone, gender, farmName, location, district);
            String userId = currentUser.getUid();
            DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Farmer").child("Users");

            loadingBar.setTitle("Update Account");
            loadingBar.setMessage("Please wait, while we are updating your account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            referenceUsers.child(userId).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        currentUser.updateProfile(userProfileChangeRequest);
                        Toast.makeText(getActivity(), "Update Successful!", Toast.LENGTH_LONG).show();

                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        Fragment ProfileFragment = new ProfileFragment();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, ProfileFragment).commit();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    loadingBar.dismiss();
                }
            });
        }

    }

    //fetch data from db and display
    private void showProfile(FirebaseUser currentUser) {
        String userId = currentUser.getUid();
        DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Farmer").child("Users");

        loadingBar.setTitle("Profile Data");
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        referenceUsers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    name = currentUser.getDisplayName();
                    phone = readUserDetails.mobile;
                    gender = readUserDetails.gender;
                    farmName = readUserDetails.farmName;
                    location = readUserDetails.location;
                    district = readUserDetails.district;

                    txt_update_name.setText(name);
                    txt_update_phone.setText(phone);
                    txt_update_farm_name.setText(farmName);
                    txt_update_location.setText(location);

                    //show gender through radio button
                    if (gender.equals("Male")) {
                        rb_update_gender_selected = view.findViewById(R.id.rb_male);
                    } else {
                        rb_update_gender_selected = view.findViewById(R.id.rb_female);
                    }
                    rb_update_gender_selected.setChecked(true);

                    //show country through spinner
                    if (district.equals("Ampara")) {
                        sp_update_district.setSelection(0);
                    } else if (district.equals("Anuradhapura")) {
                        sp_update_district.setSelection(1);
                    } else if (district.equals("Badulla")) {
                        sp_update_district.setSelection(2);
                    } else if (district.equals("Batticaloa")) {
                        sp_update_district.setSelection(3);
                    } else if (district.equals("Colombo")) {
                        sp_update_district.setSelection(4);
                    } else if (district.equals("Galle")) {
                        sp_update_district.setSelection(5);
                    } else if (district.equals("Gampaha")) {
                        sp_update_district.setSelection(6);
                    } else if (district.equals("Hambantota")) {
                        sp_update_district.setSelection(7);
                    }else if (district.equals("Jaffna")) {
                        sp_update_district.setSelection(8);
                    }else if (district.equals("Kalutara")) {
                        sp_update_district.setSelection(9);
                    }else if (district.equals("Kandy")) {
                        sp_update_district.setSelection(10);
                    }else if (district.equals("Kegalle")) {
                        sp_update_district.setSelection(11);
                    }else if (district.equals("Kilinochchi")) {
                        sp_update_district.setSelection(12);
                    }else if (district.equals("Kurunegala")) {
                        sp_update_district.setSelection(13);
                    }else if (district.equals("Mannar")) {
                        sp_update_district.setSelection(14);
                    }else if (district.equals("Matale")) {
                        sp_update_district.setSelection(15);
                    }else if (district.equals("Matara")) {
                        sp_update_district.setSelection(16);
                    }else if (district.equals("Moneragala")) {
                        sp_update_district.setSelection(17);
                    }else if (district.equals("Mullaitivu")) {
                        sp_update_district.setSelection(18);
                    }else if (district.equals("Nuwara Eliya")) {
                        sp_update_district.setSelection(19);
                    }else if (district.equals("Polonnaruwa")) {
                        sp_update_district.setSelection(20);
                    }else if (district.equals("Puttalam")) {
                        sp_update_district.setSelection(21);
                    }else if (district.equals("Ratnapura")) {
                        sp_update_district.setSelection(22);
                    }else if (district.equals("Trincomalee")) {
                        sp_update_district.setSelection(23);
                    }else if (district.equals("Vavuniya")) {
                        sp_update_district.setSelection(24);
                    }

                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
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