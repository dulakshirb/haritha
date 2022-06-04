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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail, userPassword;
    private Button btnRegister;
    private TextView alreadyHaveAnAccountLink;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        initializeField();

        alreadyHaveAnAccountLink.setOnClickListener(view -> sendUserToLoginActivity());
        btnRegister.setOnClickListener(view -> createNewUserAccount());
    }

    private void createNewUserAccount() {
        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            userEmail.setError("Email is required!");
            userEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError("Please provide a valid email.");
            userEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            userPassword.setError("Password is required");
            userPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            {
                userPassword.setError("Sorry, that password isn't right. Password must have at lease 8 characters.");
            }
        } else {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
                boolean isNewUser = Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();

                if (isNewUser) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            rootRef.child("Officer").child("Users").child(currentUserId).setValue("");
                            sendUserToMainActivity();
                            Toast.makeText(RegisterActivity.this, "Account Create Successfully.", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        } else {
                            String message = Objects.requireNonNull(task1.getException()).toString();
                            Toast.makeText(RegisterActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "The email address you have entered is already registered.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            });


        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void initializeField() {
        userEmail = (EditText) findViewById(R.id.register_email);
        userPassword = (EditText) findViewById(R.id.register_password);
        btnRegister = (Button) findViewById(R.id.btn_register);
        alreadyHaveAnAccountLink = (TextView) findViewById(R.id.already_have_an_account_link);

        loadingBar = new ProgressDialog(this);
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

}