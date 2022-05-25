package com.haritha.haritha_farmer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private EditText loginEmail, loginPassword;
    private Button btnLogin;
    private TextView forgetPasswordLink, newUserRegisterLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeField();

        newUserRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUsertoRegisterActivity();
            }
        });
    }

    private void initializeField() {
        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        forgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);
        newUserRegisterLink = (TextView) findViewById(R.id.new_user_register_link);
        btnLogin = (Button) findViewById(R.id.btn_login);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser != null) {
            sendUsertoMainActivity();
        }
    }

    private void sendUsertoMainActivity() {
        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(loginIntent);
    }

    private void sendUsertoRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}