package org.o7planning.project_04;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent; // Import Intent
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;


import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.fragments.Transaction_Fragment;
import org.o7planning.project_04.fragments.categoryfragment;
import org.o7planning.project_04.fragments.spending_limit_fragment;
import org.o7planning.project_04.fragments.statfragment;


import com.jakewharton.threetenabp.AndroidThreeTen; // Đảm bảo đã thêm thư viện này vào build.gradle

import com.google.android.material.bottomnavigation.BottomNavigationView; // Import BottomNavigationView
import android.view.MenuItem; // Import MenuItem
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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


        //Tao danh muc mac dinh
        DBHelper db = new DBHelper(this);
        db.insertDefaultCategoriesIfNeeded();




        // Khởi tạo và sao chép database (nếu cần)
        PrepopulatedDBHelper dbHelper = new PrepopulatedDBHelper(this);
        dbHelper.checkAndCopyDatabase(); // Gọi trước khi mở database

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        // === Logic Xử lý BottomNavigationView ===
        // Tìm và gán cho biến thành viên bottomNav
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new Transaction_Fragment())//Load trang giao dich đầu tiên khi vào app
                .commit();
//  Điều hướng đến các fragment từ bottom_nav
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    selectedFragment = new Transaction_Fragment();
                } else if (itemId == R.id.nav_category) {
                    categoryfragment fragment = new categoryfragment();
                    Bundle args = new Bundle();
                    args.putString("loaiDM", "chi tiêu");
                    fragment.setArguments(args);
                    selectedFragment = fragment;
                } else if (itemId == R.id.nav_stats) {
                    selectedFragment = new statfragment();
                } else if (itemId == R.id.nav_more) {
                    selectedFragment = new spending_limit_fragment();
                }

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
