package org.o7planning.project_04.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.project_04.PrepopulatedDBHelper;
import org.o7planning.project_04.R;

public class ActivityAccountInfor extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 2000;

    private ImageView ivLogoForgot;
    private EditText edtForgotEmail;
    private EditText edtOldPassword;
    private EditText edtNewPassword;
    private Button btnExit;
    private Button btnSave;

    private PrepopulatedDBHelper dbHelper;
    private SQLiteDatabase database;

    // Lưu tạm URI ảnh mới
    private String newImageUriString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Khởi tạo DBHelper
        dbHelper = new PrepopulatedDBHelper(this);
        database = dbHelper.openDatabase();

        // Ánh xạ View (bây giờ là EditText thay cho TextInputEditText)
        ivLogoForgot    = findViewById(R.id.ivLogoForgot);
        edtForgotEmail  = findViewById(R.id.edtForgotEmail);
        edtOldPassword  = findViewById(R.id.editTextText);
        edtNewPassword  = findViewById(R.id.editTextText2);
        btnExit         = findViewById(R.id.btnexit);
        btnSave         = findViewById(R.id.btnsave);

        // Xử lý đổi ảnh
        ivLogoForgot.setOnClickListener(view -> confirmChangeLogo());

        // Nút thoát → finish()
        btnExit.setOnClickListener(view -> finish());

        // Nút lưu → kiểm tra và update DB
        btnSave.setOnClickListener(view -> attemptSaveChanges());
    }

    private void confirmChangeLogo() {
        new AlertDialog.Builder(ActivityAccountInfor.this)
                .setTitle("Thay đổi ảnh đại diện")
                .setMessage("Bạn có muốn thay đổi ảnh không?")
                .setPositiveButton("Có", (dialog, which) -> openGalleryToPickImage())
                .setNegativeButton("Không", null)
                .show();
    }

    private void openGalleryToPickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE
                && resultCode == RESULT_OK
                && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                // Hiển thị ảnh lên ivLogoForgot
                ivLogoForgot.setImageURI(uri);
                // Lưu tạm URI
                newImageUriString = uri.toString();
            }
        }
    }

    private void attemptSaveChanges() {
        String email   = edtForgotEmail.getText().toString().trim();
        String oldPass = edtOldPassword.getText().toString();
        String newPass = edtNewPassword.getText().toString();

        if (email.isEmpty()) {
            edtForgotEmail.setError("Email không được để trống");
            edtForgotEmail.requestFocus();
            return;
        }
        if (oldPass.isEmpty()) {
            edtOldPassword.setError("Mật khẩu cũ không được để trống");
            edtOldPassword.requestFocus();
            return;
        }
        if (newPass.isEmpty()) {
            edtNewPassword.setError("Mật khẩu mới không được để trống");
            edtNewPassword.requestFocus();
            return;
        }

        Cursor cursor = database.query(
                "TAIKHOAN",
                new String[]{ "ID_TK", "PassWord" },
                "Email = ?",
                new String[]{ email },
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String currentPassInDB = cursor.getString(cursor.getColumnIndexOrThrow("PassWord"));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("ID_TK"));
            cursor.close();

            if (!currentPassInDB.equals(oldPass)) {
                edtOldPassword.setError("Mật khẩu cũ không đúng");
                edtOldPassword.requestFocus();
                return;
            }

            ContentValues cv = new ContentValues();
            cv.put("PassWord", newPass);
            if (newImageUriString != null) {
                cv.put("HinhAnh", newImageUriString);
            }

            int rowsAffected = database.update(
                    "TAIKHOAN",
                    cv,
                    "ID_TK = ?",
                    new String[]{ String.valueOf(userId) }
            );
            if (rowsAffected > 0) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (cursor != null) cursor.close();
            edtForgotEmail.setError("Email không tồn tại");
            edtForgotEmail.requestFocus();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
