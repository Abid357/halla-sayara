package com.example.hallasayara.global;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class Validation {

    public static final Pattern PATTERN_EMAIL = Patterns.EMAIL_ADDRESS;
    public static final Pattern PATTERN_PHONE = Pattern.compile("^([03]\\d{10}$|)");
    public static final Pattern PATTERN_NAME = Pattern.compile("^([a-zA-Z ]*$|)");

    public static boolean isEmpty(@NonNull String text){
        return text.isEmpty();
    }

    public static boolean isTooLong(@NonNull String text,  int length){
       return text.length() > length;
    }

    public static boolean matchesPattern(@NonNull String text, @NonNull Pattern pattern){
       return pattern.matcher(text).matches();
    }

    public static boolean matchesExact(@NonNull String text, @NonNull String exact){
        return text.equals(exact);
    }
}
