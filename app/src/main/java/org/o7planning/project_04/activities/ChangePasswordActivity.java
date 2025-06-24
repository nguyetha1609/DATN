package org.o7planning.project_04.activities;

import android.content.ContentValues;
import android.content.Intent;
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
        etNew     = findViewById(R.id.etNewPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);
        btnExit   = findViewById(R.id.btnexit);
        btnSave   = findViewById(R.id.btnsave);

        // Lấy email từ Intent
        email = getIntent().getStringExtra("Email");

        // Mở DB
        dbHelper = new PrepopulatedDBHelper(this);
        database = dbHelper.openDatabase();

        btnExit.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> {
            String oldPass = etdOldPassword.getText().toString();
            String p1 = etNew.getText().toString();
            String p2 = etConfirm.getText().toString();

            Log.d("ChangePassword", "Email: " + email); // Log the email
            Log.d("ChangePassword", "Old Pass Entered: " + oldPass); // Log old password
            Log.d("ChangePassword", "New Pass: " + p1); // Log new password

            if (oldPass.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!p1.equals(p2)) {
                etConfirm.setError("Xác nhận mật khẩu không khớp");
                return;
            }

            Cursor cursor = null; // Declare cursor outside try-finally for scope
            try {
                cursor = database.query(
                        "TAIKHOAN",
                        new String[]{"PassWord"},
                        "Email = ?",
                        new String[]{email},
                        null, null, null
                );

                if (cursor != null && cursor.moveToFirst()) {
                    String currentPassInDB = cursor.getString(cursor.getColumnIndexOrThrow("PassWord"));
                    Log.d("ChangePassword", "Current Pass in DB: " + currentPassInDB); // Log DB password

                    if (!currentPassInDB.equals(oldPass)) {
                        etdOldPassword.setError("Mật khẩu cũ không đúng");
                        etdOldPassword.requestFocus();
                        return;
                    }

                    ContentValues cv = new ContentValues();
                    cv.put("PassWord", p1);
                    int rowsAffected = database.update(
                            "TAIKHOAN",
                            cv,
                            "Email = ?",
                            new String[]{email}
                    );
                    Log.d("ChangePassword", "Rows Affected: " + rowsAffected); // Log rows affected

                    if (rowsAffected > 0) {
                        Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        // ... (rest of your success code)
                    } else {
                        Toast.makeText(this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Email không tồn tại", Toast.LENGTH_SHORT).show();
                    Log.e("ChangePassword", "Email not found in database."); // Log error
                }
            } catch (Exception e) {
                Log.e("ChangePassword", "Database error: " + e.getMessage(), e); // Log any exceptions
                Toast.makeText(this, "Đã xảy ra lỗi cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            } finally {
                if (cursor != null) {
                    cursor.close(); // Ensure cursor is closed
                }
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
