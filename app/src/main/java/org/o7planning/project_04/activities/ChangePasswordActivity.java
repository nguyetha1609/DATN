package org.o7planning.project_04.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.project_04.databases.PrepopulatedDBHelper;
import org.o7planning.project_04.R;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText etdOldPassword, etNew, etConfirm;
    private Button btnExit, btnSave;
    private PrepopulatedDBHelper dbHelper;
    private SQLiteDatabase database;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etdOldPassword = findViewById(R.id.etdOldPassword);
        etNew = findViewById(R.id.etNewPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);
        btnExit = findViewById(R.id.btnexit);
        btnSave = findViewById(R.id.btnsave);

        email = getIntent().getStringExtra("Email");
        dbHelper = new PrepopulatedDBHelper(this);
        database = dbHelper.openDatabase();

        btnExit.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String oldPass = etdOldPassword.getText().toString().trim();
            String newPass = etNew.getText().toString().trim();
            String confirmPass = etConfirm.getText().toString().trim();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                etConfirm.setError("Xác nhận mật khẩu không khớp");
                return;
            }

            if (newPass.length() < 8) {
                etNew.setError("Mật khẩu phải tối thiểu 8 ký tự");
                return;
            }

            Cursor cursor = null;
            try {
                cursor = database.query(
                        "TAIKHOAN",
                        new String[]{"PassWord"},
                        "Email = ?",
                        new String[]{email},
                        null, null, null
                );

                if (cursor != null && cursor.moveToFirst()) {
                    String currentPassword = cursor.getString(cursor.getColumnIndexOrThrow("PassWord"));

                    if (!currentPassword.equals(oldPass)) {
                        etdOldPassword.setError("Mật khẩu cũ không đúng");
                        etdOldPassword.requestFocus();
                        return;
                    }

                    ContentValues values = new ContentValues();
                    values.put("PassWord", newPass);
                    int rows = database.update("TAIKHOAN", values, "Email = ?", new String[]{email});

                    if (rows > 0) {
                        Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Email không tồn tại", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("ChangePassword", "Database error: " + e.getMessage(), e);
                Toast.makeText(this, "Đã xảy ra lỗi cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            } finally {
                if (cursor != null) cursor.close();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
