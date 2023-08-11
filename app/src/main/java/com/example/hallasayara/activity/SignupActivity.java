package com.example.hallasayara.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hallasayara.R;
import com.example.hallasayara.core.University;
import com.example.hallasayara.global.Database;
import com.example.hallasayara.global.Validation;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {
    private Spinner universitiesSpinner;
    private Button signupButton;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText domainEditText;
    private EditText phoneEditText;
    private EditText regnoEditText;
    private TextView haveAccountEditText;

    private List<University> universityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        phoneEditText = (EditText) findViewById(R.id.phone_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        domainEditText = (EditText) findViewById(R.id.domain_edit_text);
//        regnoEditText = (EditText) findViewById(R.id.regno_edit_text);
        signupButton = (Button) findViewById(R.id.signup_button);
        universitiesSpinner = (Spinner) findViewById(R.id.universities_spinner);
        haveAccountEditText = (TextView) findViewById(R.id.have_account_text_view);

        haveAccountEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String name = nameEditText.getText().toString().trim();
                if (Validation.isEmpty(name))
                    nameEditText.setError("Name cannot be empty field.");
                else if (Validation.isTooLong(name, 40))
                    nameEditText.setError("Name is too long.");
                else if (!Validation.matchesPattern(name, Validation.PATTERN_NAME))
                    nameEditText.setError("Name is not valid");
                else
                    nameEditText.setError(null);
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
                String domain = domainEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim() + "@" + domain;
                if (Validation.isEmpty(email))
                    emailEditText.setError("Email cannot be empty field.");
                else if (Validation.isTooLong(email, 40))
                    emailEditText.setError("Email is too long.");
                else if (!Validation.matchesPattern(email, Validation.PATTERN_EMAIL))
                    emailEditText.setError("Email is not valid");
                else
                    emailEditText.setError(null);
            }
        });

        domainEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String domain = domainEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim() + "@" + domain;
                if (Validation.isEmpty(email))
                    emailEditText.setError("Email cannot be empty or incomplete field.");
                else if (Validation.isTooLong(email, 40))
                    emailEditText.setError("Email is too long.");
                else if (!Validation.matchesPattern(email, Validation.PATTERN_EMAIL)) {
                    emailEditText.setError("Email is not valid");
                } else
                    emailEditText.setError(null);
            }
        });

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
                if (Validation.isEmpty(phone))
                    phoneEditText.setError("Phone cannot be empty field.");
                else if (!Validation.matchesPattern(phone, Validation.PATTERN_PHONE))
                    phoneEditText.setError("Phone is not valid");
                else
                    phoneEditText.setError(null);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String domain = domainEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim() + "@" + domain;
                int regNo = 0; //Integer.parseInt(regnoEditText.getText().toString().trim());
                int uniIndex = universitiesSpinner.getSelectedItemPosition();
                University university = universityList.get(uniIndex);

                // validations
                if (nameEditText.getError() == null && phoneEditText.getError() == null && emailEditText.getError() == null) {
                    Database.createUser(name, email, phone, university, regNo);
                }
            }
        });

        universityList = Database.getUniversityList();

        if (universityList != null && universityList.size() > 0) {

            List<String> list = new ArrayList<String>();
            for (University university : universityList)
                list.add(university.getName());

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            universitiesSpinner.setAdapter(arrayAdapter);

            universitiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    University university = universityList.get(position);

                    if (university != null && university.getDomain() != null) {
                        domainEditText.setText(university.getDomain());
                        domainEditText.setEnabled(false);
                    } else {
                        domainEditText.setEnabled(true);
                        domainEditText.setText("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (!universityList.isEmpty())
                universitiesSpinner.setSelection(0);
            nameEditText.setText("");
            phoneEditText.setText("");
        }


    }
}

