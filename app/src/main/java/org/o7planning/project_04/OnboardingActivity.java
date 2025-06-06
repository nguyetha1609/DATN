package org.o7planning.project_04;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import org.o7planning.project_04.Adapter.OnboardingAdapter;

public class OnboardingActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Kiểm tra SharedPreferences TRƯỚC: nếu đã xem onboarding rồi thì bỏ qua
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        if (!prefs.getBoolean("isFirstRun", true)) {
            // Chuyển thẳng vào MainActivity và finish để không quay lại
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // 2. Chỉ khi là lần đầu mới setContentView và khởi tạo ViewPager
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        adapter = new OnboardingAdapter(viewPager, prefs);
        viewPager.setAdapter(adapter);
    }
}
