package com.haritha.haritha_farmer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail, userPassword;
    private Button btnRegister;
    private TextView alreadyHaveanAccountLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeField();

        alreadyHaveanAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUsertoLoginActivity();
            }
        });
    }

    private void initializeField() {
        userEmail = (EditText) findViewById(R.id.register_email);
        userPassword = (EditText) findViewById(R.id.register_password);
        btnRegister = (Button) findViewById(R.id.btn_register);
        alreadyHaveanAccountLink = (TextView) findViewById(R.id.already_have_an_account_link);
    }

    private void sendUsertoLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

}