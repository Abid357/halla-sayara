package com.example.hallasayara.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hallasayara.R;
import com.example.hallasayara.global.Database;
import com.example.hallasayara.global.Validation;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText emailEditText;
    private EditText phoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Database.initialize(this);

        loginButton = (Button) findViewById(R.id.login_button);
        phoneEditText = (EditText) findViewById(R.id.login_phone_edit_text);
        emailEditText = (EditText) findViewById(R.id.login_email_edit_text);


        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String phone = phoneEditText.getText().toString().trim();
                if (!Validation.matchesPattern(phone, Validation.PATTERN_PHONE))
                    phoneEditText.setError("Phone is not valid");
                else
                    phoneEditText.setError(null);
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email = emailEditText.getText().toString().trim();
                if (Validation.isTooLong(email, 40))
                    emailEditText.setError("Email is too long.");
                else if (!Validation.matchesPattern(email, Validation.PATTERN_EMAIL))
                    emailEditText.setError("Email is not valid");
                else
                    emailEditText.setError(null);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();

                if ((emailEditText.getError() == null && !email.isEmpty()) || phoneEditText.getError() == null && !phone.isEmpty()){
                    Database.loginUser(email, phone);
                }
            }
        });
    }
}
