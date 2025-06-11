package org.o7planning.project_04.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.project_04.MainActivity;
import org.o7planning.project_04.PrepopulatedDBHelper;
import org.o7planning.project_04.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1000;

    // Tạm giả sử ID tài khoản hiện tại = 1 (cột ID_TK của bảng TAIKHOAN)
    private final int currentUserId = 1;

    private PrepopulatedDBHelper dbHelper;
    private SQLiteDatabase database;

    private CircleImageView imgProfile;
    private TextView tvUserName;
    private Spinner spinnerIncomeType;
    private TextView tvAmount;
    private LinearLayout layoutAccount;
    private LinearLayout layoutChangePassword;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // ── 1. Khởi tạo PrepopulatedDBHelper và mở database ──
        dbHelper = new PrepopulatedDBHelper(this);
        database = dbHelper.openDatabase(); // database KHÔNG còn null

        // ── 2. Ánh xạ các View từ XML ──
        imgProfile = findViewById(R.id.imgProfile);
        tvUserName = findViewById(R.id.tvUserName);
        spinnerIncomeType = findViewById(R.id.spinnerIncomeType);
        tvAmount = findViewById(R.id.tvAmount);
        layoutAccount = findViewById(R.id.layoutAccount);
        layoutChangePassword = findViewById(R.id.layoutChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        // ── 3. Load dữ liệu từ DB (sau khi đã mở database) ──
        loadProfileImageFromDB();
        loadUserNameFromDB();
        setupIncomeTypeSpinner();
        loadAmountFromDB();  // CHỈ SUM(SoTien) trên toàn bộ GIAODICH

        // ── 4. Sự kiện click vào ảnh: hỏi có đổi ảnh không? ──
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmChangeProfileImage();
            }
        });

        // ── 5. Click vào layoutAccount → ForgotPasswordActivity ──
        layoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, ActivityAccountInfor.class);
                startActivity(intent);
            }
        });

        // ── 6. Click vào layoutChangePassword → ChangePasswordActivity ──
        layoutChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, ActivityChangePassword.class);
                startActivity(intent);
            }
        });

        // ── 7. Click vào nút Logout → show AlertDialog xác nhận ──
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmLogout();
            }
        });
    }

    // =====================================================================================
    // 1. Load và hiển thị Ảnh ĐẠI DIỆN (lấy từ cột HinhAnh của TAIKHOAN)
    //    - Cột HinhAnh là TEXT chứa URI.toString()
    // =====================================================================================
    private void loadProfileImageFromDB() {
        Cursor cursor = database.query(
                "TAIKHOAN",                     // tên bảng
                new String[]{ "HinhAnh" },      // chỉ lấy cột HinhAnh
                "ID_TK = ?",                    // WHERE ID_TK = ?
                new String[]{ String.valueOf(currentUserId) },
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String imageUriStr = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"));
            if (imageUriStr != null && !imageUriStr.isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(imageUriStr);
                    imgProfile.setImageURI(imageUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        // Nếu không có bản ghi, imgProfile giữ mặc định trong XML (ví dụ @drawable/ic_person).
    }

    private void confirmChangeProfileImage() {
        new AlertDialog.Builder(AccountActivity.this)
                .setTitle("Thay đổi ảnh đại diện")
                .setMessage("Bạn có muốn thay đổi ảnh không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openGalleryToPickImage();
                    }
                })
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
                try {
                    // Hiển thị ảnh ngay lập tức lên CircleImageView
                    imgProfile.setImageURI(uri);

                    // Lưu URI.toString() vào cột HinhAnh trong bảng TAIKHOAN
                    ContentValues cv = new ContentValues();
                    cv.put("HinhAnh", uri.toString());
                    database.update(
                            "TAIKHOAN",
                            cv,
                            "ID_TK = ?",
                            new String[]{ String.valueOf(currentUserId) }
                    );
                    Toast.makeText(this, "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Lỗi khi lưu ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // =====================================================================================
    // 2. Load và hiển thị Tên NGƯỜI DÙNG (lấy từ cột Username của TAIKHOAN)
    // =====================================================================================
    private void loadUserNameFromDB() {
        Cursor cursor = database.query(
                "TAIKHOAN",                      // tên bảng
                new String[]{ "Username" },      // cột Username
                "ID_TK = ?",                     // WHERE ID_TK = ?
                new String[]{ String.valueOf(currentUserId) },
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
            tvUserName.setText(username);
            cursor.close();
        } else {
            tvUserName.setText("Chưa có tên");
        }
    }

    // =====================================================================================
    // 3. Spinner: chọn “Tiền hàng ngày, Tiền hàng tháng, Tiền hàng năm”
    // =====================================================================================
    private void setupIncomeTypeSpinner() {
        // Vì trong XML đã khai báo android:entries="@array/income_type_array"
        spinnerIncomeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selected = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(AccountActivity.this,
                        "Bạn chọn: " + selected,
                        Toast.LENGTH_SHORT).show();
                // Ở đây bạn có thể xử lý logic theo loại tiền được chọn
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    // =====================================================================================
    // 4. Load và hiển thị SỐ TIỀN: SUM(SoTien) của toàn bộ bảng GIAODICH
    //    (bởi GIAODICH chưa có cột ID_TK, nên không lọc theo user)
    // =====================================================================================
    private void loadAmountFromDB() {
        // Câu SQL không có WHERE ID_TK (vì bảng GIAODICH chưa có cột đó)
        String sql = "SELECT SUM(SoTien) AS Total FROM GIAODICH";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor != null && cursor.moveToFirst()) {
            long total = cursor.getLong(cursor.getColumnIndexOrThrow("Total"));
            if (total > 0) {
                tvAmount.setText(String.format("%,d VND", total));
            } else {
                tvAmount.setText("0 VND");
            }
            cursor.close();
        } else {
            tvAmount.setText("0 VND");
        }
    }

    // =====================================================================================
    // 5. ĐĂNG XUẤT với AlertDialog xác nhận
    // =====================================================================================
    private void confirmLogout() {
        new AlertDialog.Builder(AccountActivity.this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performLogout();
                    }
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void performLogout() {
        Intent intent = new Intent(AccountActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đóng database để tránh leak
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
