package org.o7planning.project_04.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.project_04.PrepopulatedDBHelper;
import org.o7planning.project_04.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegUsername, etEmail, etRegPassword, etConfirmPassword;
    private CheckBox cbAgree;
    private Button btnRegister;
    private TextView tvLogin, tvForgotPassword;

    private PrepopulatedDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        etRegUsername     = findViewById(R.id.etRegUsername);
        etEmail           = findViewById(R.id.etEmail);
        etRegPassword     = findViewById(R.id.etRegPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbAgree           = findViewById(R.id.cbAgree);
        btnRegister       = findViewById(R.id.btnRegister);
        tvLogin           = findViewById(R.id.tvLogin);
        tvForgotPassword  = findViewById(R.id.tvForgotPassword);

        // Khởi tạo DB helper
        dbHelper = new PrepopulatedDBHelper(getApplicationContext());

        btnRegister.setOnClickListener(v -> {
            String user  = etRegUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass  = etRegPassword.getText().toString().trim();
            String pass2 = etConfirmPassword.getText().toString().trim();

            // 1. Kiểm tra rỗng
            if (user.isEmpty() || email.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Check điều khoản
            if (!cbAgree.isChecked()) {
                Toast.makeText(this, "Bạn phải đồng ý điều khoản", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Check mật khẩu khớp
            if (!pass.equals(pass2)) {
                Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. Kiểm tra username đã tồn tại?
            if (checkUserExists(user)) {
                Toast.makeText(this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 5. Thêm user mới vào TAIKHOAN, để HinhAnh tạm là chuỗi rỗng
            if (insertUser(user, pass, email, "")) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        tvLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );

        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ActivityForgotPassword.class))
        );
    }

    /**
     * Trả về true nếu đã có bản ghi với Username này
     */
    private boolean checkUserExists(String username) {
        SQLiteDatabase db = dbHelper.openDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM TAIKHOAN WHERE Username = ? LIMIT 1",
                new String[]{ username }
        );
        boolean exist = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exist;
    }

    /**
     * Chèn bản ghi mới vào TAIKHOAN
     * @param username
     * @param password
     * @param email
     * @param hinhAnh: đường dẫn hoặc URL ảnh đại diện, ở đây có thể để "" tạm
     */
    private boolean insertUser(String username, String password, String email, String hinhAnh) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.openDatabase();
            ContentValues cv = new ContentValues();
            cv.put("Username", username);
            cv.put("PassWord", password);
            cv.put("Email", email);
            cv.put("HinhAnh", hinhAnh);
            long rowId = db.insert("TAIKHOAN", null, cv);
            return rowId != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null && db.isOpen()) db.close();
        }
    }
}
