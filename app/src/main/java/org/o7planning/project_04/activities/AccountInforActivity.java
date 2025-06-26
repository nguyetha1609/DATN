package org.o7planning.project_04.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.project_04.databases.PrepopulatedDBHelper;
import org.o7planning.project_04.R;

public class AccountInforActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 2000;
    private static final String TAG = "AccountInforActivity"; // Tag cho Logcat

    private ImageView ivLogo;
    private EditText edtEmail, etdUserName;
    private Button btnExit, btnSave;

    private PrepopulatedDBHelper dbHelper;
    private SQLiteDatabase database;

    // Lưu tạm URI ảnh mới
    private String newImageUriString = null;
    private int currentUserId = -1; // Để lưu ID của người dùng hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_infor);

        // Khởi tạo DBHelper
        dbHelper = new PrepopulatedDBHelper(this);
        try {
            dbHelper.checkAndCopyDatabase(); // Đảm bảo database tồn tại và được copy
            database = dbHelper.openDatabase();
            if (database == null || !database.isOpen()) {
                Toast.makeText(this, "Không thể mở cơ sở dữ liệu. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Database is null or not open after openDatabase()");
                finish();
                return;
            }
        } catch (SQLiteException e) {
            Toast.makeText(this, "Lỗi cơ sở dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "SQLiteException when opening database: " + e.getMessage(), e);
            finish();
            return;
        } catch (Exception e) { // Bắt các lỗi khác có thể xảy ra trong quá trình mở DB
            Toast.makeText(this, "Lỗi không xác định khi mở DB: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "General Exception when opening database: " + e.getMessage(), e);
            finish();
            return;
        }


        // Ánh xạ View
        ivLogo      = findViewById(R.id.ivLogo);
        edtEmail    = findViewById(R.id.edtEmail);
        etdUserName = findViewById(R.id.edtUserName);
        btnExit     = findViewById(R.id.btnexit);
        btnSave     = findViewById(R.id.btnsave);

        // Xử lý đổi ảnh
        ivLogo.setOnClickListener(view -> confirmChangeLogo());

        // Nút thoát → finish()
        btnExit.setOnClickListener(view -> finish());

        // Nút lưu → kiểm tra và update DB
        btnSave.setOnClickListener(view -> attemptSaveChanges());

        // Lấy ID_TK từ Intent và load dữ liệu
        currentUserId = getIntent().getIntExtra("ID_TK", -1);
        if (currentUserId != -1) {
            loadAccountInformation(currentUserId);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin tài khoản", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "ID_TK not found in Intent.");
            finish(); // Thoát nếu không có ID người dùng
        }
    }

    private void loadAccountInformation(int idTk) {
        Cursor cursor = null;
        try {
            cursor = database.query("TAIKHOAN", null, "ID_TK = ?",
                    new String[]{String.valueOf(idTk)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String email = cursor.getString(cursor.getColumnIndexOrThrow("Email"));
                String hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));

                edtEmail.setText(email);
                etdUserName.setText(username);
                if (hinhAnh != null && !hinhAnh.isEmpty()) {
                    ivLogo.setImageURI(Uri.parse(hinhAnh));
                    newImageUriString = hinhAnh; // Set URI ban đầu nếu ảnh tồn tại
                }
                Log.d(TAG, "Account info loaded for ID_TK: " + idTk);
            } else {
                Log.e(TAG, "No account found in DB for ID_TK: " + idTk);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "SQLiteException loading account info: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khi tải thông tin tài khoản: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "General Exception loading account info: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi không xác định khi tải thông tin tài khoản.", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void confirmChangeLogo() {
        new AlertDialog.Builder(AccountInforActivity.this)
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
                ivLogo.setImageURI(uri);
                newImageUriString = uri.toString(); // Lưu tạm URI
                Log.d(TAG, "New image URI selected: " + newImageUriString);
            }
        }
    }

    private void attemptSaveChanges() {
        String email    = edtEmail.getText().toString().trim();
        String userName = etdUserName.getText().toString().trim(); // Lấy username

        if (email.isEmpty()) {
            edtEmail.setError("Email không được để trống");
            edtEmail.requestFocus();
            return;
        }

        if (userName.isEmpty()) {
            etdUserName.setError("Tên người dùng không được để trống");
            etdUserName.requestFocus();
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("Email", email);
        cv.put("Username", userName); // Cập nhật username
        if (newImageUriString != null) {
            cv.put("HinhAnh", newImageUriString);
        }
        Log.d(TAG, "Attempting to save changes for ID_TK: " + currentUserId + ", Email: " + email + ", Username: " + userName);

        try {
            int rowsAffected = database.update(
                    "TAIKHOAN",
                    cv,
                    "ID_TK = ?",
                    new String[]{ String.valueOf(currentUserId) }
            );
            Log.d(TAG, "Rows affected by update: " + rowsAffected);

            if (rowsAffected > 0) {
                Toast.makeText(this, "Cập nhật thông tin tài khoản thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thông tin tài khoản thất bại", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Update account info failed, 0 rows affected.");
            }
        } catch (SQLiteException e) {
            Toast.makeText(this, "Lỗi cơ sở dữ liệu khi cập nhật: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "SQLiteException during account info update: " + e.getMessage(), e);
        } catch (Exception e) {
            Toast.makeText(this, "Đã xảy ra lỗi khi cập nhật thông tin: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "General Exception during account info update: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
            Log.d(TAG, "Database closed.");
        }
    }
}