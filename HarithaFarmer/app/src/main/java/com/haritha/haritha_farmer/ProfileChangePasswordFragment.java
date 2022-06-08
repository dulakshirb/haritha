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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileChangePasswordFragment extends Fragment {

    private View view;
    private EditText txt_change_password_current_password, txt_change_password_new, txt_change_password_new_confirm;
    private TextView txt_change_password_authenticated;
    private Button btn_change_password_authenticate, btn_change_password;
    private String currentPassword;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_change_password, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        txt_change_password_current_password = view.findViewById(R.id.txt_change_password_current_password);
        txt_change_password_new = view.findViewById(R.id.txt_change_password_new);
        txt_change_password_new_confirm = view.findViewById(R.id.txt_change_password_new_confirm);
        txt_change_password_authenticated = view.findViewById(R.id.txt_change_password_authenticated);
        btn_change_password_authenticate = view.findViewById(R.id.btn_change_password_authenticate);
        btn_change_password = view.findViewById(R.id.btn_change_password);
        loadingBar = new ProgressDialog(getActivity());

        //disable new,confirm pwd and button in the beginning
        txt_change_password_new.setEnabled(false);
        txt_change_password_new_confirm.setEnabled(false);
        btn_change_password.setEnabled(false);

        if (currentUser.equals("")) {
            Toast.makeText(getActivity(), "Something went wrong! User's details not available.", Toast.LENGTH_LONG).show();
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment fragment = new ProfileFragment();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
        } else {
            reAuthenticate(currentUser);
        }

        return view;
    }

    //ReAuthenticate / verify user before updating email
    private void reAuthenticate(FirebaseUser currentUser) {
        btn_change_password_authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPassword = txt_change_password_current_password.getText().toString().trim();

                if (TextUtils.isEmpty(currentPassword)) {
                    txt_change_password_current_password.setError("Please enter your current password to continue.");
                    txt_change_password_current_password.requestFocus();
                } else {
                    loadingBar.setTitle("User");
                    loadingBar.setMessage("Authenticating...");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();

                    AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);
                    currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loadingBar.dismiss();

                                txt_change_password_current_password.setEnabled(false);
                                btn_change_password_authenticate.setEnabled(false);

                                txt_change_password_new.setEnabled(true);
                                txt_change_password_new_confirm.setEnabled(true);
                                btn_change_password.setEnabled(true);

                                txt_change_password_authenticated.setText("You are authenticated. You can change your password now!");

                                Toast.makeText(getActivity(), "Password has been verified. Your can change your password now.", Toast.LENGTH_LONG).show();

                                btn_change_password.setBackgroundResource(R.drawable.button_green);

                                btn_change_password.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changePassword(currentUser);
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

    private void changePassword(FirebaseUser currentUser) {
        String newPassword = txt_change_password_new.getText().toString().trim();
        String confirmPassword = txt_change_password_new_confirm.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            txt_change_password_new.setError("Password is required.");
            txt_change_password_new.requestFocus();
        } else if (newPassword.length() < 6) {
            txt_change_password_new.setError("Password too weak.");
            txt_change_password_new.requestFocus();
        } else if (TextUtils.isEmpty(confirmPassword)) {
            txt_change_password_new_confirm.setError("Password confirmation is required.");
            txt_change_password_new_confirm.requestFocus();
        } else if (!newPassword.equals(confirmPassword)) {
            txt_change_password_new_confirm.setError("Password & Confirm Password must be match.");
            txt_change_password_new_confirm.requestFocus();
            //clear the entered passwords
            txt_change_password_new.clearComposingText();
            txt_change_password_new_confirm.clearComposingText();
        } else if (newPassword.equals(currentPassword)) {
            txt_change_password_new.setError("New password cannot be same as old password.");
            txt_change_password_new.requestFocus();
        } else {
            loadingBar.setTitle("New Password");
            loadingBar.setMessage("Changing...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            currentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Password has been changed.", Toast.LENGTH_LONG).show();

                        //send user to profile fragment
                        Fragment profileFragment = new ProfileFragment();
                        loadFragment(profileFragment);

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