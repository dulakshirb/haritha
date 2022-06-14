package com.haritha.haritha_farmer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText txt_login_email, txt_login_password;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txt_login_email = findViewById(R.id.txt_login_email);
        txt_login_password = findViewById(R.id.txt_login_password);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        //show hide password using eye icon
        ImageView img_show_hide_password = findViewById(R.id.img_show_hide_password);
        img_show_hide_password.setImageResource(R.drawable.ic_hide_pwd);
        img_show_hide_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_login_password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    //if pw is visible then hide it and change icon
                    txt_login_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    img_show_hide_password.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    txt_login_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    img_show_hide_password.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        //user login
        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(view -> allowUserToLogin());

        //forget password
        TextView forget_password_link = findViewById(R.id.forget_password_link);
        forget_password_link.setOnClickListener(view -> sendUserToForgetPasswordActivity());

        //register new account
        TextView new_user_register_link = findViewById(R.id.new_user_register_link);
        new_user_register_link.setOnClickListener(view -> sendUserToRegisterActivity());
    }

    private void allowUserToLogin() {
        String txtEmail = txt_login_email.getText().toString().trim();
        String txtPassword = txt_login_password.getText().toString().trim();

        if (TextUtils.isEmpty(txtEmail)) {
            txt_login_email.setError("This field is required");
            txt_login_email.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()) {
            txt_login_email.setError("Please provide valid email!");
            txt_login_email.requestFocus();
        }
        if (TextUtils.isEmpty(txtPassword)) {
            txt_login_password.setError("This field is required");
            txt_login_password.requestFocus();
        } else {
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    //get instance of the current user
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    //check if email is verified before user can access their profile
                    if(firebaseUser.isEmailVerified()){
                        sendUserToMainActivity();
                        Toast.makeText(LoginActivity.this, "Logged in Successful..", Toast.LENGTH_SHORT).show();
                    }else {
                        firebaseUser.sendEmailVerification();
                        mAuth.signOut();
                        showAlertDialog();
                    }

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        txt_login_email.setError("User does not exists or is no longer valid. Please register again.");
                        txt_login_email.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        txt_login_email.setError("Invalid credentials. Kindly, check and re-enter.");
                        txt_login_email.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
                loadingBar.dismiss();
            });
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Not Verified.");
        builder.setMessage("Please verify your email now. You can not login without email verification.");

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

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registerIntent);
        finish();
    }

    private void sendUserToForgetPasswordActivity() {
        Intent forgetPasswordIntent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
       // forgetPasswordIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(forgetPasswordIntent);
        //finish();
    }

    //check if user is already logged in.

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            Toast.makeText(this, "Already Logged In!", Toast.LENGTH_SHORT).show();
            sendUserToMainActivity();
        }else {
            Toast.makeText(this, "You can login now!", Toast.LENGTH_SHORT).show();
        }
    }
}