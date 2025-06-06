package org.o7planning.project_04.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.project_04.R;
import org.o7planning.project_04.databases.DBHelper;

public class ActivityAccountInfor extends AppCompatActivity {

    private EditText edtForgotEmail;
    private EditText edtOldPassword;
    private EditText edtNewPassword;
    private Button btnExit, btnSave;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_infor);

        // Ánh xạ view
        edtForgotEmail = findViewById(R.id.edtForgotEmail);
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        btnExit = findViewById(R.id.btnExit);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new DBHelper(this);

        // Lấy ID người dùng từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        long currentUserId = sharedPreferences.getLong("userId", -1);

        // Hiển thị email hiện tại
        String currentEmail = dbHelper.getEmailById(currentUserId);
        edtForgotEmail.setText(currentEmail);

        // Xử lý nút Thoát
        btnExit.setOnClickListener(v -> {
            finish(); // Quay lại màn trước
        });

        // Xử lý nút Lưu
        btnSave.setOnClickListener(v -> {
            String newEmail = edtForgotEmail.getText().toString().trim();
            String oldPassword = edtOldPassword.getText().toString().trim();
            String newPassword = edtNewPassword.getText().toString().trim();

            if (newEmail.isEmpty() || oldPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.updateUserInformation(currentUserId, newEmail, oldPassword, newPassword);
            if (success) {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Mật khẩu cũ không đúng hoặc có lỗi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
