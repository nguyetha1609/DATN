package org.o7planning.project_04.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.o7planning.project_04.R;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.model.category; // Import model category

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private TextView toolbarSave, tvDateLabel, tvTimeLabel, toolbarTitle; // Thêm toolbarTitle
    private EditText edtAmount, edtNoteHint;
    private LinearLayout itemDate, itemTime;
    private ImageView imgCategory, btnBack;
    private Calendar calendar;
    // field để lưu tạm category được chọn
    private int selectedCategoryId = -1;
    private static final int REQUEST_SELECT_CATEGORY = 3001;

    private boolean isEditMode = false;
    private int transactionId = -1; // ID giao dịch nếu đang ở chế độ chỉnh sửa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Khởi tạo các View
        btnBack = findViewById(R.id.btnBack);
        toolbarSave = findViewById(R.id.toolbar_save);
        toolbarTitle = findViewById(R.id.toolbar_title); // Ánh xạ toolbarTitle
        imgCategory = findViewById(R.id.imgCategory);
        edtAmount = findViewById(R.id.edtAmount);
        itemTime = findViewById(R.id.item_time);
        edtNoteHint = findViewById(R.id.edtNoteHint);
        tvDateLabel = findViewById(R.id.tvDateLabel);
        tvTimeLabel = findViewById(R.id.tvTimeLabel);
        calendar = Calendar.getInstance();

        // Kiểm tra nếu là chế độ chỉnh sửa
        if (getIntent().hasExtra("isEditMode") && getIntent().getBooleanExtra("isEditMode", false)) {
            isEditMode = true;
            transactionId = getIntent().getIntExtra("transactionId", -1);
            toolbarTitle.setText("Sửa Giao Dịch"); // Thay đổi tiêu đề
            toolbarSave.setText("Cập Nhật"); // Thay đổi văn bản nút lưu
            loadTransactionData(); // Tải dữ liệu giao dịch đã có
        } else {
            // Đặt giá trị mặc định cho ngày và giờ khi tạo giao dịch mới
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            tvDateLabel.setText(sdfDate.format(calendar.getTime()));

            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
            tvTimeLabel.setText(sdfTime.format(calendar.getTime()));
        }

        tvDateLabel.setOnClickListener(v -> {
            // Lấy ngày hiện tại
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Tạo DatePickerDialog
            DatePickerDialog datePicker = new DatePickerDialog(AddTransactionActivity.this,
                    (view, d, m, y) -> {
                        // Lưu lại trong
                        calendar.set(Calendar.DAY_OF_MONTH, d);
                        calendar.set(Calendar.MONTH, m);
                        calendar.set(Calendar.YEAR, y);

                        // Format thành dd-MM-yyyy
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        String selectedDate = sdf.format(calendar.getTime());

                        // Set text cho etPurchaseDate
                        tvDateLabel.setText(selectedDate);
                    },
                    year, month, day
            );
            datePicker.show();
        });

        // 2. Thiết lập sự kiện click
        itemTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy giờ phút hiện tại làm mặc định
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Tạo TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddTransactionActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                                // Khi người dùng chọn giờ xong, cập nhật TextView
                                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                                tvTimeLabel.setText(formattedTime);
                            }
                        },
                        hour,    // giờ mặc định
                        minute,  // phút mặc định
                        true     // true = 24h format, false = AM/PM
                );
                timePickerDialog.show();
            }
        });

        // Đặt OnClickListener cho nút back trên toolbar
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Quay lại Fragment trước đó
            }
        });

        // 2. Click chọn danh mục → mở CategoryActivity chờ kết quả
        imgCategory.setOnClickListener(v -> {
            Intent intent = new Intent(AddTransactionActivity.this, CategoryActivity.class);
            //   intent.putExtra("LoaiDM", "ChiTieu");
            intent.putExtra("isSelectMode", true);
            startActivityForResult(intent, REQUEST_SELECT_CATEGORY);
        });

        // --- Sự kiện nhập số cho tvAmount ---
        edtAmount.setFocusableInTouchMode(true);
        edtAmount.setOnClickListener(v -> {
            edtAmount.requestFocus();
            edtAmount.postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(edtAmount, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
        });

        // --- Sự kiện nhập ghi chú cho itemNote ---
        edtNoteHint.setOnClickListener(v -> {
            edtNoteHint.requestFocus();
            edtNoteHint.postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(edtNoteHint, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
        });

        // 3. Click lưu → gọi hàm save() hoặc update()
        toolbarSave.setOnClickListener(v -> {
            if (isEditMode) {
                updateTransaction();
            } else {
                saveTransaction();
            }
        });
    }

    private void loadTransactionData() {
        // Lấy dữ liệu từ Intent và điền vào các trường
        selectedCategoryId = getIntent().getIntExtra("categoryId", -1);
        long amount = getIntent().getLongExtra("amount", 0L);
        String time = getIntent().getStringExtra("time");
        String note = getIntent().getStringExtra("note");
        String categoryName = getIntent().getStringExtra("selectedCategoryName");
        String categoryIcon = getIntent().getStringExtra("selectedCategoryIcon");

        // Điền dữ liệu
        edtAmount.setText(String.valueOf(amount));
        edtNoteHint.setText(note);

        if (time != null && time.contains(" ")) {
            String[] dateTimeParts = time.split(" ");
            if (dateTimeParts.length == 2) {
                tvDateLabel.setText(dateTimeParts[0]);
                tvTimeLabel.setText(dateTimeParts[1]);

                // Cập nhật Calendar object
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                    Date date = sdf.parse(time);
                    calendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }


        // Cập nhật icon danh mục
        if (categoryIcon != null) {
            int resId = getResources().getIdentifier(categoryIcon, "drawable", getPackageName());
            if (resId != 0) {
                imgCategory.setImageResource(resId);
            } else {
                imgCategory.setImageResource(R.drawable.ic_default); // Icon mặc định nếu không tìm thấy
            }
        }
    }

    // Nhận kết quả từ CategoryActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_CATEGORY && resultCode == RESULT_OK && data != null) {
            selectedCategoryId = data.getIntExtra("selectedCategoryId", -1);
            String name = data.getStringExtra("selectedCategoryName");

            // txt_name.setText(name); chưa có textView hiện tên danh mục
            String iconName = data.getStringExtra("selectedCategoryIcon");
            if (iconName != null) {
                int resId = getResources().getIdentifier(iconName, "drawable", getPackageName());
                if (resId != 0) {
                    imgCategory.setImageResource(resId);
                }
            }
        }
    }

    // Save vào DB (thêm mới)
    private void saveTransaction() {
        // 1. Validate
        if (selectedCategoryId < 0) {
            Toast.makeText(this, "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
            return;
        }
        String amountStr = edtAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        String datetime = tvDateLabel.getText() + " " + tvTimeLabel.getText();
        String note = edtNoteHint.getText().toString().trim();

        // 2. Insert vào DB
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ID_DM", selectedCategoryId);
        values.put("SoTien", amount);
        values.put("ThoiGian", datetime);
        values.put("GhiChu", note);

        long newId = db.insert("GIAODICH", null, values);
        db.close();

        // 3. Thông báo & đóng
        if (newId > 0) {
            Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi lưu", Toast.LENGTH_SHORT).show();
        }
    }

    // Update vào DB (chỉnh sửa)
    private void updateTransaction() {
        // 1. Validate (Tương tự như save, đảm bảo các trường không rỗng)
        if (selectedCategoryId < 0) {
            Toast.makeText(this, "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
            return;
        }
        String amountStr = edtAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        String datetime = tvDateLabel.getText() + " " + tvTimeLabel.getText();
        String note = edtNoteHint.getText().toString().trim();

        // 2. Update vào DB
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ID_DM", selectedCategoryId);
        values.put("SoTien", amount);
        values.put("ThoiGian", datetime);
        values.put("GhiChu", note);

        int rowsAffected = db.update("GIAODICH", values, "ID_GD = ?", new String[]{String.valueOf(transactionId)});
        db.close();

        // 3. Thông báo & đóng
        if (rowsAffected > 0) {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Báo hiệu đã có thay đổi
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
        }
    }
}