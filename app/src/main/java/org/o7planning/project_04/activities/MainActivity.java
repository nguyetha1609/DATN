package org.o7planning.project_04.activities;

import static android.app.PendingIntent.getActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.o7planning.project_04.R;
import org.o7planning.project_04.databases.PrepopulatedDBHelper;
import org.o7planning.project_04.fragments.StatFragment;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.material.navigation.NavigationBarView;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class MainActivity extends AppCompatActivity {

    private Button btnAdd, btnChiTieu, btnThuNhap;
    private TextView tabExpense, tabIncome, filterDay, filterMonth, filterYear, filterAll;
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
//        //Lay ID TK
//       SharedPreferences prefs = getSharedPreferences("LOGIN_PREF", MODE_PRIVATE);
//        int userId = prefs.getInt("ID_TK", -1);
//
//       if (userId != -1) {
//            CategoryDAO db = new CategoryDAO(this);
//            db.insertDefaultCategoriesIfNeeded(userId);
//        }


        // Initialize and copy database (if needed)
        PrepopulatedDBHelper dbHelper = new PrepopulatedDBHelper(this);
        dbHelper.checkAndCopyDatabase(); // Call before opening the database

        btnAdd = findViewById(R.id.btnAdd); // Đảm bảo btnAdd là ID chính xác từ activity_main.xml
        tabExpense = findViewById(R.id.tabExpense);
        tabIncome = findViewById(R.id.tabIncome);
        btnChiTieu = findViewById(R.id.btnChiTieu);
        btnThuNhap = findViewById(R.id.btnThuNhap);
        filterDay = findViewById(R.id.filter_day);
        filterMonth = findViewById(R.id.filter_month);
        filterYear = findViewById(R.id.filter_year);
        filterAll = findViewById(R.id.filter_all);
        // Initialize BottomNavigationView
        bottomNav = findViewById(R.id.bottom_nav);

        SharedPreferences prefs = getSharedPreferences(LOGIN_PREF, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_REMEMBER, false);
        if (!isLoggedIn) {
            // chưa login → qua NotLoginActivity
            startActivity(new Intent(this, NotLoginActivity.class));
            finish();
            return;
        }
        // Handle navigation item selection
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    return true;
                } else if (itemId == R.id.nav_stats) {
                    selectedFragment = new StatFragment();

                } else if (itemId == R.id.nav_category) { // gia dinh de test thoi, xóa khi gọi từ trang them giao dịch
                    Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
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
                return false;
            }
        });
        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddTransactionActivity.class));
        });

            // Khởi tạo các tab con Chi tiêu và Thu nhập và thêm sự kiện click


            tabExpense.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý khi tab "Chi tiêu" được click
                    // Ví dụ: thay đổi trạng thái UI, load dữ liệu chi tiêu
                    // Ở đây bạn có thể thay đổi màu nền hoặc màu chữ để biểu thị tab đang được chọn
                    // Giả sử có colorPrimary và textPrimary trong colors.xml
                    tabExpense.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tabIncome.setTextColor(getResources().getColor(R.color.textPrimary));
                    // Thêm logic để hiển thị danh sách chi tiêu
                }
            });
            tabIncome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý khi tab "Thu nhập" được click
                    // Ví dụ: thay đổi trạng thái UI, load dữ liệu thu nhập
                    tabIncome.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tabExpense.setTextColor(getResources().getColor(R.color.textPrimary));
                    // Thêm logic để hiển thị danh sách thu nhập
                }
            });

            // Khởi tạo các Button "Chi tiêu" và "Thu nhập"


            btnChiTieu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý khi nút "Chi tiêu" được click
                    // Ví dụ: hiển thị chi tiết chi tiêu hoặc làm nổi bật nút này
                }
            });

            btnThuNhap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý khi nút "Thu nhập" được click
                    // Ví dụ: hiển thị chi tiết thu nhập hoặc làm nổi bật nút này
                }
            });

            // Khởi tạo các TextView trong phần "Tabs: Ngày, Tháng, Năm, Tất cả" và thêm sự kiện click


            filterDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý khi tab "Ngày" được click
                    // Ví dụ: thay đổi trạng thái UI, load dữ liệu theo ngày
                }
            });

            filterMonth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý khi tab "Tháng" được click
                    // Ví dụ: thay đổi trạng thái UI, load dữ liệu theo tháng
                }
            });

            filterYear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý khi tab "Năm" được click
                    // Ví dụ: thay đổi trạng thái UI, load dữ liệu theo năm
                }
            });

            filterAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý khi tab "Tất cả" được click
                    // Ví dụ: thay đổi trạng thái UI, load tất cả dữ liệu
                }
            });
            return;
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