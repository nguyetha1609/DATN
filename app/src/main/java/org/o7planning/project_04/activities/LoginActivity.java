package org.o7planning.project_04.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.project_04.databases.PrepopulatedDBHelper;
import org.o7planning.project_04.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private CheckBox cbRemember;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;

    private PrepopulatedDBHelper dbHelper;
    private SharedPreferences sharedPrefs;
    private static final String PREF_NAME = "LOGIN_PREF";
    private static final String KEY_USER = "USERNAME";
    private static final String KEY_PASS = "PASSWORD";
    private static final String KEY_REMEMBER = "REMEMBER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ view
        etUsername       = findViewById(R.id.etUsername);
        etPassword       = findViewById(R.id.etPassword);
        cbRemember       = findViewById(R.id.cbRemember);
        btnLogin         = findViewById(R.id.btnLogin);
        tvRegister       = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Khởi tạo helper và SharedPreferences
        dbHelper    = new PrepopulatedDBHelper(getApplicationContext());
            sharedPrefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Nếu đã lưu trước đó thì điền vào ô
        checkRememberedAccount();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = etUsername.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this,
                            "Vui lòng nhập tài khoản và mật khẩu",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checkLogin(user, pass)) {
                    // Lấy ID_TK
                    int idTK = getID_TK(user);
                    // Lưu nhớ hay không
                    saveAccount(
                            cbRemember.isChecked() ? user : "",
                            cbRemember.isChecked() ? pass : "",
                            cbRemember.isChecked()
                    );
                    // Lưu luôn ID_TK để dùng ở chỗ khác (luôn lưu, không phụ thuộc cbRemember để thêm danh mục, giao dịch
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putInt("ID_TK", idTK);
                    editor.apply();

                    // Đăng nhập thành công → MainActivity
                    Intent intent = new Intent(
                            LoginActivity.this,
                            MainActivity.class
                    );
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Sai tài khoản hoặc mật khẩu!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(
                        LoginActivity.this,
                        RegisterActivity.class))
        );

        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(
                        LoginActivity.this,
                        ForgotPasswordActivity.class))
        );
    }

    /**
     * Kiểm tra trong bảng TAIKHOAN xem có bản ghi Username/PassWord khớp không
     */
    private boolean checkLogin(String username, String password) {
        SQLiteDatabase db = dbHelper.openDatabase();
        // Chú ý: cột Password trong DB bạn đặt là PassWord
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM TAIKHOAN WHERE Username = ? AND PassWord = ? LIMIT 1",
                new String[]{ username, password }
        );
        boolean exist = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exist;
    }

    private void saveAccount(String user, String pass, boolean remember) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(KEY_USER, user);
        editor.putString(KEY_PASS, pass);
        editor.putBoolean(KEY_REMEMBER, remember);
        editor.apply();
    }

    private void checkRememberedAccount() {
        boolean isRemember = sharedPrefs.getBoolean(KEY_REMEMBER, false);
        if (isRemember) {
            etUsername.setText(sharedPrefs.getString(KEY_USER, ""));
            etPassword.setText(sharedPrefs.getString(KEY_PASS, ""));
            cbRemember.setChecked(true);
        }
    }

    // Hàm này để lấy ID_TK
    private int getID_TK(String username) {
        SQLiteDatabase db = dbHelper.openDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT ID_TK FROM TAIKHOAN WHERE Username = ? LIMIT 1",
                new String[]{ username }
        );
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return id;
    }

}
