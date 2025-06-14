package org.o7planning.project_04.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.project_04.databases.PrepopulatedDBHelper;
import org.o7planning.project_04.R;
import org.o7planning.project_04.utils.EmailSender;

import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText edtEmail;
    private Button btnExit, btnSend;
    private PrepopulatedDBHelper dbHelper;
    private SQLiteDatabase database;
    private String currentEmail, currentCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtEmail = findViewById(R.id.editTextText);
        btnExit  = findViewById(R.id.btnexit);
        btnSend  = findViewById(R.id.btnsave);

        dbHelper = new PrepopulatedDBHelper(this);
        // mở DB (copy từ assets nếu cần) rồi lấy đối tượng
        database = dbHelper.openDatabase();

        btnExit.setOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Email không hợp lệ");
                return;
            }

            // KIỂM TRA EMAIL TỒN TẠI
            Cursor c = database.rawQuery(
                    "SELECT 1 FROM TAIKHOAN WHERE Email = ? LIMIT 1",
                    new String[]{ email }
            );
            boolean exists = c.moveToFirst();
            c.close();
            if (!exists) {
                edtEmail.setError("Email chưa đăng ký");
                return;
            }

            // TẠO VÀ GỬI MÃ XÁC NHẬN
            currentCode  = String.format("%05d", new Random().nextInt(100000));
            currentEmail = email;
            EmailSender.sendCode(currentEmail, currentCode);

            // CHUYỂN SANG MÀN HÌNH NHẬP MÃ
            Intent it = new Intent(this, VerificationCodeActivity.class);
            it.putExtra("email", currentEmail);
            it.putExtra("code", currentCode);
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
