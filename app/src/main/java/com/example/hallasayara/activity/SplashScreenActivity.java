package com.example.hallasayara.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


import com.example.hallasayara.R;
import com.example.hallasayara.global.Database;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // ImageView view = findViewById(R.id.titleImageView);

        Database.initialize(getApplicationContext());
        Database.loadUniversityList();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 2s = 5000ms
                Intent intent = new Intent(getApplicationContext(), SelectionActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

}
