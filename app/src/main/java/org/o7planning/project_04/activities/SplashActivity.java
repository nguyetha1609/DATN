package org.o7planning.project_04.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        SharedPreferences prefs= getSharedPreferences("AppPrefs",MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
        boolean isLoggedIn = prefs.getBoolean("REMEMBER", false);
        Intent intent;
        if(isFirstRun){
            intent = new Intent(this, OnboardingActivity.class);
        } else if (isLoggedIn) {
            intent = new Intent(this,MainActivity.class);
        }else {
            intent = new Intent(this,LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
