package com.haritha.haritha_farmer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText txt_register_user_name, txt_register_email, txt_register_mobile_number, txt_register_farm_name, txt_register_location, txt_register_password,
            txt_register_confirmed_password;
    private RadioGroup rg_gender_group;
    private RadioButton rb_register_gender_selected;
    private Spinner sp_register_country;
    private static final String TAG = "RegisterActivity";
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txt_register_user_name = findViewById(R.id.txt_register_user_name);
        txt_register_email = findViewById(R.id.txt_register_email);
        txt_register_mobile_number = findViewById(R.id.txt_register_mobile_number);
        txt_register_farm_name = findViewById(R.id.txt_register_farm_name);
        txt_register_location = findViewById(R.id.txt_register_location);
        txt_register_password = findViewById(R.id.txt_register_password);
        txt_register_confirmed_password = findViewById(R.id.txt_register_confirmed_password);
        sp_register_country = findViewById(R.id.sp_register_country);
        rg_gender_group = findViewById(R.id.rg_gender_group);
        rg_gender_group.clearCheck();
        loadingBar = new ProgressDialog(this);

        //register button
        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(view -> {

            //selected gender id
            int selectedGenderId = rg_gender_group.getCheckedRadioButtonId();
            rb_register_gender_selected = findViewById(selectedGenderId);

            //obtain the entered data
            String txtUserName = txt_register_user_name.getText().toString().trim();
            String txtEmail = txt_register_email.getText().toString().trim();
            String txtMobile = txt_register_mobile_number.getText().toString().trim();
            String txtFarmName = txt_register_farm_name.getText().toString().trim();
            String txtLocation = txt_register_location.getText().toString().trim();
            String txtPassword = txt_register_password.getText().toString().trim();
            String txtConfirmedPassword = txt_register_confirmed_password.getText().toString().trim();
            String txtCountry;
            String txtGender;

            if (TextUtils.isEmpty(txtUserName)) {
                txt_register_user_name.setError("Your Name is required.");
                txt_register_user_name.requestFocus();
            } else if (TextUtils.isEmpty(txtEmail)) {
                txt_register_email.setError("E-mail is required.");
                txt_register_email.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()) {
                txt_register_email.setError("Valid e-mail is required.");
                txt_register_email.requestFocus();
            } else if (TextUtils.isEmpty(txtMobile)) {
                txt_register_mobile_number.setError("Mobile No. is required.");
                txt_register_mobile_number.requestFocus();
            } else if (txtMobile.length() != 10) {
                txt_register_mobile_number.setError("Mobile no. should be 10 digits.");
                txt_register_mobile_number.requestFocus();
            } else if (rg_gender_group.getCheckedRadioButtonId() == -1) {
                Toast.makeText(RegisterActivity.this, "Gender is required.", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(txtFarmName)) {
                txt_register_farm_name.setError("Farm Name is required.");
                txt_register_farm_name.requestFocus();
            } else if (TextUtils.isEmpty(txtLocation)) {
                txt_register_location.setError("Location is required.");
                txt_register_location.requestFocus();
            } else if (TextUtils.isEmpty(txtPassword)) {
                txt_register_password.setError("Password is required.");
                txt_register_password.requestFocus();
            } else if (txtPassword.length() < 6) {
                txt_register_password.setError("Password too weak.");
                txt_register_password.requestFocus();
            } else if (TextUtils.isEmpty(txtConfirmedPassword)) {
                txt_register_confirmed_password.setError("Password confirmation is required.");
                txt_register_confirmed_password.requestFocus();
            } else if (!txtPassword.equals(txtConfirmedPassword)) {
                txt_register_confirmed_password.setError("Password & Confirm Password must be match.");
                txt_register_confirmed_password.requestFocus();
                //clear the entered passwords
                txt_register_password.clearComposingText();
                txt_register_confirmed_password.clearComposingText();
            } else {
                txtCountry = sp_register_country.getSelectedItem().toString();
                txtGender = rb_register_gender_selected.getText().toString();
                loadingBar.setTitle("Creating New Account");
                loadingBar.setMessage("Please wait, while we are creating new account for you...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();
                registerUser(txtUserName, txtEmail, txtMobile, txtGender, txtFarmName, txtLocation, txtCountry, txtPassword);
            }
        });

        //back to login
        TextView already_have_an_account_link = findViewById(R.id.already_have_an_account_link);
        already_have_an_account_link.setOnClickListener(view -> sendUserToLoginActivity());
    }

    //register user using the credentials given
    private void registerUser(String txtUserName, String txtEmail, String txtMobile, String txtGender, String txtFarmName, String txtLocation, String txtCountry, String txtPassword) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    //Update display name of user
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(txtUserName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //Enter user data into the Firebase Realtime Database
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(txtMobile, txtGender, txtFarmName, txtLocation, txtCountry);
                    //Extracting user reference from database for registered users
                    DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Farmer").child("Users");
                    referenceUsers.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //send verification email
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(RegisterActivity.this, "User registered successfully. Please verify your email.", Toast.LENGTH_LONG).show();
                                sendUserToMainActivity();
                            } else {
                                Toast.makeText(RegisterActivity.this, "User registered failed. Please try again.", Toast.LENGTH_LONG).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        txt_register_password.setError("Your password is too weak. Kindly use a mix of alphabets, numbers and special characters.");
                        txt_register_password.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        txt_register_email.setError("Your email is invalid or already in use. Kindly re-enter.");
                        txt_register_password.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        txt_register_email.setError("User is already registered with this email. User another email.");
                        txt_register_password.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendUserToLoginActivity() {
        Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registerIntent);
        finish();
    }
}