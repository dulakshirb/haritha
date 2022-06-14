package com.haritha.haritha_farmer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgetPasswordActivity";
    private EditText fp_email;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        fp_email = findViewById(R.id.fp_email);
        Button btn_reset_Password = findViewById(R.id.btn_reset_Password);
        loadingBar = new ProgressDialog(this);

        btn_reset_Password.setOnClickListener(view -> resetPassword());
    }

    private void resetPassword() {
        String email = fp_email.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            fp_email.setError("Email is required!");
            fp_email.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            fp_email.setError("Please provide valid email!");
            fp_email.requestFocus();
        } else {

            loadingBar.setTitle("Sending Reset Email");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth = FirebaseAuth.getInstance();
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgetPasswordActivity.this, "Check your email to reset your password!", Toast.LENGTH_SHORT).show();
                    sendUserToLoginActivity();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        fp_email.setError("User does not exists or is no longer valid. Please register again.");
                        fp_email.requestFocus();
                    }catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ForgetPasswordActivity.this, "Try again! Something wrong happened!", Toast.LENGTH_SHORT).show();
                    }
                }
                loadingBar.dismiss();
            });
        }
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }
}