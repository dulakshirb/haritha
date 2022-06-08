package com.haritha.harithaagriofficer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private Button btnLogin;
    private TextView forgetPasswordLink, newUserRegisterLink;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        initializeField();

        newUserRegisterLink.setOnClickListener(view -> sendUserToRegisterActivity());

        btnLogin.setOnClickListener(view -> allowUserToLogin());

        forgetPasswordLink.setOnClickListener(view -> sendUserToForgetPasswordActivity());


    }

    private void initializeField() {
        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        forgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);
        newUserRegisterLink = (TextView) findViewById(R.id.new_user_register_link);
        btnLogin = (Button) findViewById(R.id.btn_login);

        loadingBar = new ProgressDialog(this);
    }

    private void allowUserToLogin() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            loginEmail.setError("This field is required");
            loginEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError("Please provide valid email!");
            loginEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            loginPassword.setError("This field is required");
            loginPassword.requestFocus();
        } else {
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    sendUserToMainActivity();
                    Toast.makeText(LoginActivity.this, "Logged in Successful..", Toast.LENGTH_SHORT).show();
                } else {
                    String message = Objects.requireNonNull(task.getException()).toString();
                    System.out.println("Error : " + message);
                    Toast.makeText(LoginActivity.this, "Failed to login..", Toast.LENGTH_SHORT).show();
                }
                loadingBar.dismiss();
            });
        }
    }

    private void sendUserToForgetPasswordActivity() {
        Intent forgetPasswordIntent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        startActivity(forgetPasswordIntent);
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

}