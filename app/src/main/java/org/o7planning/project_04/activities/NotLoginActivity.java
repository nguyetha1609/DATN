package org.o7planning.project_04.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.project_04.R;

public class NotLoginActivity extends AppCompatActivity {

    private LinearLayout layoutAccount;
    private LinearLayout layoutChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_login);

        layoutAccount        = findViewById(R.id.layoutAccount);
        layoutChangePassword = findViewById(R.id.layoutChangePassword);

        layoutAccount.setOnClickListener(v -> {
            startActivity(new Intent(NotLoginActivity.this, LoginActivity.class));
        });

        layoutChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(NotLoginActivity.this, RegisterActivity.class));
        });
    }
}
