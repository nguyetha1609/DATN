package org.o7planning.project_04.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.project_04.databases.PrepopulatedDBHelper;
import org.o7planning.project_04.R;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText etNew, etConfirm;
    private Button btnExit, btnSave;
    private PrepopulatedDBHelper dbHelper;
    private SQLiteDatabase database;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etNew     = findViewById(R.id.etNewPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);
        btnExit   = findViewById(R.id.btnexit);
        btnSave   = findViewById(R.id.btnsave);

        // Lấy email từ Intent
        email = getIntent().getStringExtra("email");

        // Mở DB
        dbHelper = new PrepopulatedDBHelper(this);
        database = dbHelper.openDatabase();

        btnExit.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> {
            String p1 = etNew.getText().toString();
            String p2 = etConfirm.getText().toString();
            if (p1.isEmpty() || p2.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!p1.equals(p2)) {
                etConfirm.setError("Xác nhận mật khẩu không khớp");
                return;
            }

            // CẬP NHẬT MẬT KHẨU TRỰC TIẾP
            ContentValues cv = new ContentValues();
            cv.put("PassWord", p1);
            database.update(
                    "TAIKHOAN",
                    cv,
                    "Email = ?",
                    new String[]{ email }
            );
            Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

            // Quay về Login
            Intent it = new Intent(this, LoginActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(it);
            finish();
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
