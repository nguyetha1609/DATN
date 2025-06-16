package org.o7planning.project_04.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import org.o7planning.project_04.Adapter.OnboardingAdapter;
import org.o7planning.project_04.R;

public class OnboardingActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Kiểm tra SharedPreferences TRƯỚC: nếu đã xem onboarding rồi thì bỏ qua
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun",true);
        boolean isLoggedIn = prefs.getBoolean("REMEMBER",false);

        if(!isFirstRun){
            //Đã từng mở app -> kiểm tra đã đăng nhập chưa
            if(isLoggedIn){
                //nếu đã lưu trạng thái đăng nhập -> mở MainActivity điều hướng tới trang giao dịch
                startActivity(new Intent(this,MainActivity.class));
            }else {
                //Nếu chưa đăng nhập lần nào chuyển tới trang login
                startActivity(new Intent(this,LoginActivity.class));
            }
            finish();
            return;
        }

//        if (!prefs.getBoolean("isFirstRun", true)) {
//            // Chuyển thẳng vào MainActivity và finish để không quay lại
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
//            return;
//        }

        // 2. Chỉ khi là lần đầu mới setContentView và khởi tạo ViewPager
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        adapter = new OnboardingAdapter(viewPager, prefs);
        viewPager.setAdapter(adapter);
    }
}
