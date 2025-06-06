package org.o7planning.project_04.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import org.o7planning.project_04.R; // Đảm bảo đã import R

public class ActivityChangePassword extends AppCompatActivity { // Hoặc chỉ Activity nếu không sử dụng AppCompat
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password); // Liên kết với bố cục XML của bạn
        // Mã khởi tạo cho activity này sẽ nằm ở đây
    }
}
