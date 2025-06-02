package org.o7planning.project_04;

import android.app.AlertDialog;
import android.content.DialogInterface;
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


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        new AlertDialog.Builder(this)
                .setTitle("Thoát ứng dụng")
                .setMessage("Bạn có chắc muốn thoát ứng dụng?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
