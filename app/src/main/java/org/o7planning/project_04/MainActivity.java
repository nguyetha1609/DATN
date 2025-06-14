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

import org.o7planning.project_04.activities.ActivityCategory;
import org.o7planning.project_04.databases.CategoryDAO;
import org.o7planning.project_04.fragments.TransactionFragment;
import org.o7planning.project_04.fragments.StatFragment;


import com.jakewharton.threetenabp.AndroidThreeTen; // Đảm bảo đã thêm thư viện này vào build.gradle




import org.o7planning.project_04.activities.AccountActivity;
import org.o7planning.project_04.activities.NotLoginActivity;

public class MainActivity extends AppCompatActivity {
    private PrepopulatedDBHelper database;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this); // Initialize ThreeTenABP
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //Lay ID TK
        SharedPreferences prefs = getSharedPreferences("LOGIN_PREF", MODE_PRIVATE);
        int userId = prefs.getInt("ID_TK", -1);

       if (userId != -1) {
            CategoryDAO db = new CategoryDAO(this);
            db.insertDefaultCategoriesIfNeeded(userId);
        }

        // Khởi tạo và sao chép database (nếu cần)


        // Initialize and copy database (if needed)
        PrepopulatedDBHelper dbHelper = new PrepopulatedDBHelper(this);
        dbHelper.checkAndCopyDatabase(); // Call before opening the database

        // Initialize BottomNavigationView
        bottomNav = findViewById(R.id.bottom_nav);

        // Load the Transaction_Fragment as the default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new TransactionFragment())
                .commit();

        // Handle navigation item selection
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    selectedFragment = new TransactionFragment();

                } else if (itemId == R.id.nav_stats) {
                    selectedFragment = new StatFragment();

                } else if (itemId == R.id.nav_category) { // gia dinh de test thoi, xóa khi gọi từ trang them giao dịch
                    Intent intent = new Intent(MainActivity.this, ActivityCategory.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_more) {

                   // selectedFragment = new spending_limit_fragment(); trang tai khoan hien tai

                    // Check login state using SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("LOGIN_PREF", MODE_PRIVATE);
                    boolean isLoggedIn = prefs.getBoolean("REMEMBER", false);
                    String username = prefs.getString("USERNAME", "");

                    if (isLoggedIn && !username.isEmpty()) {
                        // User is logged in, navigate to AccountActivity
                        Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                        startActivity(intent);
                    } else {
                        // User is not logged in, navigate to NotLoginActivity
                        Intent intent = new Intent(MainActivity.this, NotLoginActivity.class);
                        startActivity(intent);
                    }
                    return true; // Return true to indicate the event is handled
                }

                // Load the selected fragment if not null
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
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