package com.example.hallasayara.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hallasayara.R;
import com.example.hallasayara.global.Database;

import java.util.Random;

public class VerificationActivity extends AppCompatActivity {

    private EditText phoneEditText;
    private EditText emailEditText;
    private Button verifyButton;

    private int phoneCode;
    private int emailCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        Database.initialize(this);
        SharedPreferences sp = getSharedPreferences("com.example.hallasayara", Context.MODE_PRIVATE);
        final int id = sp.getInt("id", -1);
        String email = sp.getString("verify_email", null);
        String phone = sp.getString("verify_phone", null);
        String name = sp.getString("name", null);

        phoneEditText = (EditText) findViewById(R.id.phone_verification_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_verification_edit_text);
        verifyButton = (Button) findViewById(R.id.verify_button);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int inputPhoneCode = -1, inputEmailCode = -1;
                if (!phoneEditText.getText().toString().isEmpty())
                    inputPhoneCode = Integer.parseInt(phoneEditText.getText().toString());
                if (!emailEditText.getText().toString().isEmpty())
                    inputEmailCode = Integer.parseInt(emailEditText.getText().toString());
                if (inputEmailCode == emailCode || inputPhoneCode == phoneCode) {
                    Toast.makeText(getApplicationContext(), "Verification successful!", Toast.LENGTH_SHORT).show();
                    Database.verifyUser(id, inputEmailCode == emailCode, inputPhoneCode == phoneCode);
                    Intent intent =new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        Random random = new Random();
        phoneCode = random.nextInt(9999 - 1) + 1;
        emailCode = random.nextInt(9999 - 1) + 1;

        // TEST
//        email = "syed.irti@hotmail.com";
//        phone = "03122354177";

        if (email != null && !email.isEmpty())
            Database.sendEmailVerification(email, emailCode, name);
        if (phone != null && !phone.isEmpty()) {
            phone = phone.replaceFirst("^0+(?!$)", "+92");
            name = name.split(" ")[0];
            Database.sendPhoneVerification(phone, phoneCode, name);
        }
        Log.d("CODE", "Email: " + emailCode + " Phone: " + phoneCode);
    }
}
