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
    private Button btnAdd, btnChiTieu, btnThuNhap;
    private TextView tabExpense, tabIncome, filterDay, filterMonth, filterYear, filterAll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this); // Đảm bảo thư viện ThreeTenABP đã được cấu hình đúng
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        //Tao danh muc mac dinh
        DBHelper db = new DBHelper(this);
        db.insertDefaultCategoriesIfNeeded();


        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        // Load fragment VD giao dịch/trang chủ/category lên Mainactivity đầu tiên
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())//Load fragmenthome đầu tiên khi vào app
                .commit();
//  Điều hướng đến các fragment từ bottom_nav
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_cate) {
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

    @Override
    public void onBackPressed() {

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

        // Khởi tạo btnAdd và đặt OnClickListener cho nó
        btnAdd = findViewById(R.id.btnAdd); // Đảm bảo btnAdd là ID chính xác từ activity_main.xml
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                startActivity(intent);
            }
        });

        // Khởi tạo các tab con Chi tiêu và Thu nhập và thêm sự kiện click
        tabExpense = findViewById(R.id.tabExpense);
        tabIncome = findViewById(R.id.tabIncome);

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
        btnChiTieu = findViewById(R.id.btnChiTieu);
        btnThuNhap = findViewById(R.id.btnThuNhap);

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
        filterDay = findViewById(R.id.filter_day);
        filterMonth = findViewById(R.id.filter_month);
        filterYear = findViewById(R.id.filter_year);
        filterAll = findViewById(R.id.filter_all);

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
