package com.example.hallasayara.helper;

import android.text.Editable;
import android.text.TextWatcher;

public class FieldValidator implements TextWatcher {

    private String field;

    public FieldValidator(String field){
        this.field = field;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
