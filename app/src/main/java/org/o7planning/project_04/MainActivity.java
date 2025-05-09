package org.o7planning.project_04;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private PrepopulatedDBHelper database;
    private BottomNavigationView bottom_Nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Khởi tạo và sao chép database (nếu cần)
        PrepopulatedDBHelper dbHelper = new PrepopulatedDBHelper(this);
        dbHelper.checkAndCopyDatabase(); // Gọi trước khi mở database


        // Thêm HomeFragment vào container
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, new HomeFragment())
                    .commit();
        }

        // Xử lý BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, new HomeFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.nav_category) {
                // Xử lý các fragment khác
            }
            // ... các case khác
            return false;
        });
    }

//        bottomNav = findViewById(R.id.bottomNav);
//        // bottomNav
//        bottom_Nav.setSelectedItemId(R.id.nav_home);
//        bottom_Nav.setOnItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//            if (itemId == R.id.nav_home) {
//                return true;
//            } else if (itemId == R.id.nav_category) {
//                startActivity(new Intent(MainActivity.this, DeviceManagementActivity.class));
//                overridePendingTransition(0,0);
//                return true;
//            } else if (itemId == R.id.nav_stats) {
//                startActivity(new Intent(MainActivity.this, InventoryActivity.class));
//                overridePendingTransition(0,0);
//                return true;
//            } else if (itemId == R.id.nav_profile) {
//                startActivity(new Intent(MainActivity.this, MaintenanceActivity.class));
//                overridePendingTransition(0,0);
//                return true;
//            } else if (itemId == R.id.nav_more) {
//                startActivity(new Intent(MainActivity.this, ReportActivity.class));
//                overridePendingTransition(0, 0);
//                return true;
//            }
//            return false;
//        });
    public void onBackPressed(){
        //Tạo Dialog
        new AlertDialog.Builder(this)
                .setTitle("Thoát ứng dụng")
                .setMessage("Bạn có chắc muốn thoát ứng dụng?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Khi user xác nhận, gọi finish() hoặc super.onBackPressed() để đóng Activity
                        finish();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}