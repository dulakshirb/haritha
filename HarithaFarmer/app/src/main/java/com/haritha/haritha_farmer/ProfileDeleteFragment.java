package com.haritha.haritha_farmer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileDeleteFragment extends Fragment {

    private View view;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText txt_delete_user_password;
    private TextView txt_delete_user_authenticated;
    private ProgressDialog loadingBar;
    private String userPassword;
    private Button btn_delete_user_authenticate, btn_delete_user;
    private static final String TAG = "ProfileDeleteFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_delete, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        txt_delete_user_password = view.findViewById(R.id.txt_delete_user_password);
        txt_delete_user_authenticated = view.findViewById(R.id.txt_delete_user_authenticated);
        btn_delete_user_authenticate = view.findViewById(R.id.btn_delete_user_authenticate);
        btn_delete_user = view.findViewById(R.id.btn_delete_user);
        loadingBar = new ProgressDialog(getActivity());

        btn_delete_user.setEnabled(false);

        if (currentUser.equals("")) {
          //  Toast.makeText(getActivity(), "Something went wrong! User's details not available.", Toast.LENGTH_LONG).show();
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment fragment = new ProfileFragment();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
        } else {
            reAuthenticate(currentUser);
        }

        return view;
    }

    private void reAuthenticate(FirebaseUser currentUser) {
        btn_delete_user_authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPassword = txt_delete_user_password.getText().toString().trim();

                if (TextUtils.isEmpty(userPassword)) {
                    txt_delete_user_password.setError("Please enter your current password to continue.");
                    txt_delete_user_password.requestFocus();
                } else {
                    loadingBar.setTitle("User");
                    loadingBar.setMessage("Authenticating...");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();

                    AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), userPassword);
                    currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loadingBar.dismiss();

                                txt_delete_user_password.setEnabled(false);
                                btn_delete_user_authenticate.setEnabled(false);
                                btn_delete_user.setEnabled(true);

                                txt_delete_user_authenticated.setText("You are authenticated. You can delete your profile now!");

                                Toast.makeText(getActivity(), "Password has been verified. You can delete your profile now.", Toast.LENGTH_LONG).show();

                                btn_delete_user.setBackgroundResource(R.drawable.button_red);

                                btn_delete_user.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showAlertDialog();
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), "Incorrect Password. Please try again!", Toast.LENGTH_LONG).show();
                                }
                                loadingBar.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Profile and Related Data?");
        builder.setMessage("Do your really want to delete your profile and related data? this action is irreversible.");

        //Continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteUser(currentUser);
            }
        });
        //cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment fragment = new ProfileFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
            }
        });
        AlertDialog alertDialog = builder.create();

        //continue button color
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.h_warning_red));
            }
        });

        alertDialog.show();
    }

    private void deleteUser(FirebaseUser currentUser) {
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    deleteUserData();
                    mAuth.signOut();
                    Toast.makeText(getActivity(), "User profile has been deleted!", Toast.LENGTH_SHORT).show();
                    sendUserToMainActivity();
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

    private void deleteUserData() {
        //Delete display pic
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(currentUser.getPhotoUrl().toString());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: Photo Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //delete data from realtime db
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Farmer").child("Users");
        databaseReference.child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: User Data Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
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