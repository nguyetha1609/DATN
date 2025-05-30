package org.o7planning.project_04;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent; // Import Intent
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.jakewharton.threetenabp.AndroidThreeTen; // Đảm bảo đã thêm thư viện này vào build.gradle

import com.google.android.material.bottomnavigation.BottomNavigationView; // Import BottomNavigationView
import android.view.MenuItem; // Import MenuItem
import androidx.annotation.NonNull; // Import NonNull

public class MainActivity extends AppCompatActivity {
    private PrepopulatedDBHelper database;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this); // Đảm bảo thư viện ThreeTenABP đã được cấu hình đúng
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Khởi tạo và sao chép database (nếu cần)
        PrepopulatedDBHelper dbHelper = new PrepopulatedDBHelper(this);
        dbHelper.checkAndCopyDatabase(); // Gọi trước khi mở database

        // === Logic Xử lý BottomNavigationView ===
        // Tìm và gán cho biến thành viên bottomNav
        bottomNav = findViewById(R.id.bottomNav);

        // Đảm bảo item Home được chọn mặc định khi Activity khởi tạo
        bottomNav.setSelectedItemId(R.id.nav_home);

        // Thêm HomeFragment vào container ngay khi khởi tạo (nếu chưa có savedInstanceState)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, new HomeFragment())
                    .commit();
        }

        // Thiết lập OnItemSelectedListener cho BottomNavigationView
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId(); // Lấy ID của item được chọn

                if (itemId == R.id.nav_home) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, new HomeFragment())
                            .commit();
                    return true;
                } else if (itemId == R.id.nav_category) {
                    // Thay thế bằng Activity hoặc Fragment tương ứng
                    startActivity(new Intent(MainActivity.this, DeviceManagementActivity.class));
                    overridePendingTransition(0, 0); // Giữ nguyên transition nếu muốn
                    return true;
                } else if (itemId == R.id.nav_stats) {
                    startActivity(new Intent(MainActivity.this, InventoryActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(MainActivity.this, MaintenanceActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_more) { // item dấu 3 chấm
                    startActivity(new Intent(MainActivity.this, ReportActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false; // Trả về false nếu item không được xử lý
            }
        });
    } // Đóng phương thức onCreate()

    // Phương thức onBackPressed() đã được đặt đúng vị trí
    @Override // Sử dụng @Override để đảm bảo ghi đè đúng phương thức
    public void onBackPressed(){
        super.onBackPressed();
        // Tạo Dialog xác nhận thoát
        new AlertDialog.Builder(this)
                .setTitle("Thoát ứng dụng")
                .setMessage("Bạn có chắc muốn thoát ứng dụng?")
                .setIcon(R.drawable.ic_warning) // Đảm bảo icon này tồn tại trong drawable
                .setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Khi user xác nhận, gọi finish() để đóng Activity
                        finish();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Đóng dialog và ở lại Activity
                    }
                })
                .show();
        // Không gọi super.onBackPressed() ở đây nữa nếu muốn dialog là duy nhất để thoát
        // Nếu không có dialog, thì super.onBackPressed() sẽ là hành vi mặc định
    }
}