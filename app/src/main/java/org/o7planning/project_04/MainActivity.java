package org.o7planning.project_04;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.o7planning.project_04.activities.AccountActivity;
import org.o7planning.project_04.activities.NotLoginActivity;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.fragments.Transaction_Fragment;
import org.o7planning.project_04.fragments.categoryfragment;
import org.o7planning.project_04.fragments.statfragment;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class MainActivity extends AppCompatActivity {

    public static final String LOGIN_PREF = "LOGIN_PREF";
    public static final String KEY_REMEMBER = "REMEMBER";
    private PrepopulatedDBHelper database;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this); // Initialize ThreeTenABP
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Create default categories
        DBHelper db = new DBHelper(this);
        db.insertDefaultCategoriesIfNeeded();

        // Initialize and copy database (if needed)
        PrepopulatedDBHelper dbHelper = new PrepopulatedDBHelper(this);
        dbHelper.checkAndCopyDatabase(); // Call before opening the database

        // Initialize BottomNavigationView
        bottomNav = findViewById(R.id.bottom_nav);

        // Load the Transaction_Fragment as the default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new Transaction_Fragment())
                .commit();

        SharedPreferences prefs = getSharedPreferences(LOGIN_PREF, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_REMEMBER, false);
        if (!isLoggedIn) {
            // chưa login → qua NotLoginActivity
            startActivity(new Intent(this, NotLoginActivity.class));
            finish();
            return;
        }

        // 2. Thiết lập BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        // đang ở đây
                        return true;
                    case R.id.nav_category:
                        startActivity(new Intent(MainActivity.this, categoryfragment.class));
                        return true;
                    case R.id.nav_stats:
                        startActivity(new Intent(MainActivity.this, statfragment.class));
                        return true;
                    case R.id.nav_more:
                        startActivity(new Intent(MainActivity.this, AccountActivity.class));
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Show a confirmation dialog before exiting
        new AlertDialog.Builder(this)
                .setTitle("Thoát ứng dụng")
                .setMessage("Bạn có chắc muốn thoát ứng dụng?")
                .setIcon(R.drawable.ic_warning) // Ensure this icon exists in drawable
                .setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish(); // Close the activity
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Dismiss the dialog
                    }
                })
                .show();
    }
}