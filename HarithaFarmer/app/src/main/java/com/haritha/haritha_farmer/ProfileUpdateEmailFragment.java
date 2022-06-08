package com.haritha.haritha_farmer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileUpdateEmailFragment extends Fragment {

    private View view;
    private EditText txt_update_email_new, txt_update_email_verify_password;
    private TextView txt_update_email_authenticated;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String oldEmail, newEmail, password;
    private Button btn_update_email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_update_email, container, false);

        txt_update_email_new = view.findViewById(R.id.txt_update_email_new);
        txt_update_email_verify_password = view.findViewById(R.id.txt_update_email_verify_password);
        txt_update_email_authenticated = view.findViewById(R.id.txt_update_email_authenticated);
        btn_update_email = view.findViewById(R.id.btn_update_email);
        loadingBar = new ProgressDialog(getActivity());

        btn_update_email.setEnabled(false); //disabled in the beginning until authenticate by user
        txt_update_email_new.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //set old email on TextView
        oldEmail = currentUser.getEmail();
        TextView txt_update_email = view.findViewById(R.id.txt_update_email);
        txt_update_email.setText(oldEmail);

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
        Button btn_authenticate_user = view.findViewById(R.id.btn_authenticate_user);
        btn_authenticate_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = txt_update_email_verify_password.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    txt_update_email_verify_password.setError("Please enter your password to continue.");
                    txt_update_email_verify_password.requestFocus();
                } else {
                    loadingBar.setTitle("User");
                    loadingBar.setMessage("Authenticating...");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();

                    AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, password);
                    currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
                                Toast.makeText(getActivity(), "Password has been verified. Your can update email now.", Toast.LENGTH_LONG).show();
                                txt_update_email_authenticated.setText("You are authenticated. You can update your email now!");
                                //disable password field and authenticate button
                                txt_update_email_verify_password.setEnabled(false);
                                btn_authenticate_user.setEnabled(false);
                                btn_update_email.setEnabled(true);
                                txt_update_email_new.setEnabled(true);

                                btn_update_email.setBackgroundResource(R.drawable.button_green);

                                //update email
                                btn_update_email.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        newEmail = txt_update_email_new.getText().toString().trim();

                                        if (TextUtils.isEmpty(newEmail)) {
                                            txt_update_email_new.setError("E-mail is required.");
                                            txt_update_email_new.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                                            txt_update_email_new.setError("Valid e-mail is required.");
                                            txt_update_email_new.requestFocus();
                                        } else if (oldEmail.matches(newEmail)) {
                                            txt_update_email_new.setError("New email cannot be same as old email.");
                                            txt_update_email_new.requestFocus();
                                        } else {
                                            loadingBar.setTitle("New E-mail");
                                            loadingBar.setMessage("Updating...");
                                            loadingBar.setCanceledOnTouchOutside(true);
                                            loadingBar.show();
                                            updateEmail(currentUser);
                                        }

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

    private void updateEmail(FirebaseUser currentUser) {
        currentUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    //verify email
                    currentUser.sendEmailVerification();
                    Toast.makeText(getActivity(), "E-mail has been updated. Please verify your new email.", Toast.LENGTH_LONG).show();

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