package com.haritha.haritha_farmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText fpEmail;
    private Button btnResetPassword;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mAuth = FirebaseAuth.getInstance();

        initializeField();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String email = fpEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            fpEmail.setError("Email is required!");
            fpEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            fpEmail.setError("Please provide valid email!");
            fpEmail.requestFocus();
            return;
        }

        loadingBar.setTitle("Sending Reset Email");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgetPasswordActivity.this, "Check your email to reset your password!", Toast.LENGTH_SHORT).show();
                    sendUsertoLoginActivity();
                }else {
                    Toast.makeText(ForgetPasswordActivity.this, "Try again! Something wrong happened!", Toast.LENGTH_SHORT).show();
                }
                loadingBar.dismiss();
            }
        });
    }

    private void initializeField() {
        fpEmail = (EditText) findViewById(R.id.fp_email);
        btnResetPassword = (Button) findViewById(R.id.btn_resetPassword);
        loadingBar = new ProgressDialog(this);
    }

    private void sendUsertoLoginActivity() {
        Intent loginIntent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
}