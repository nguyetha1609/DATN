package org.o7planning.project_04.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private TextView tvAmount, toolbarSave,tvDateLabel,tvTimeLabel, txt_name;
    private LinearLayout itemDate, itemTime, itemNote;
    private ImageView imgCategory,btnBack;
    private Calendar calendar;
    // field để lưu tạm category được chọn
    private int selectedCategoryId = -1;
    private String selectedCategoryName;
    private static final int REQUEST_SELECT_CATEGORY = 3001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Khởi tạo các View
        btnBack = findViewById(R.id.btnBack);
        toolbarSave = findViewById(R.id.toolbar_save);
        imgCategory = findViewById(R.id.imgCategory);
        tvAmount = findViewById(R.id.tvAmount);
        itemTime = findViewById(R.id.item_time);
        itemNote = findViewById(R.id.item_note);
        tvDateLabel = findViewById(R.id.tvDateLabel);
        tvTimeLabel = findViewById(R.id.tvTimeLabel);
        calendar = Calendar.getInstance();

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
                int hour   = calendar.get(Calendar.HOUR_OF_DAY);
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
                Intent intent = new Intent(AddTransactionActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 2. Click chọn danh mục → mở CategoryActivity chờ kết quả
        imgCategory.setOnClickListener(v -> {
            Intent intent = new Intent(AddTransactionActivity.this, CategoryActivity.class);
            intent.putExtra(CategoryActivity.EXTRA_FOR_SELECTION, true);
            // nếu có lọc ChiTieu / ThuNhap, vẫn truyền như bình thường
            intent.putExtra("LoaiDM", "ChiTieu");
            startActivityForResult(intent, REQUEST_SELECT_CATEGORY);
        });

        // 3. Click lưu → gọi hàm save()
        toolbarSave.setOnClickListener(v -> saveTransaction());
    }

    // Nhận kết quả từ CategoryActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_CATEGORY && resultCode == RESULT_OK && data != null) {
            selectedCategoryId = data.getIntExtra("selectedCategoryId", -1);
            String name = data.getStringExtra("selectedCategoryName");
            txt_name.setText(name);
        }
    }

    // Save vào DB
    private void saveTransaction() {
        // 1. Validate
        if (selectedCategoryId < 0) {
            Toast.makeText(this, "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
            return;
        }
        String amountStr = tvAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        String datetime = tvDateLabel.getText() + " " + tvTimeLabel.getText();
        String note     = itemNote.toString().trim();

        // 2. Insert vào DB
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_id", selectedCategoryId);
        values.put("amount", amount);
        values.put("datetime", datetime);
        values.put("note", note);

        long newId = db.insert("Transactions", null, values);
        db.close();

        // 3. Thông báo & đóng
        if (newId > 0) {
            Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi lưu", Toast.LENGTH_SHORT).show();
        }

        // Đặt OnClickListener cho ImageView category
        imgCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTransactionActivity.this, CategoryActivity.class);
                // truyền thêm loại nếu bạn có filter chi tiêu / thu nhập
                intent.putExtra("LoaiDM", "ChiTieu");
                startActivityForResult(intent, REQUEST_SELECT_CATEGORY);
            }
        });
        //toolbarSave.setOnClickListener(v -> onSaveClicked());
        // Đặt OnClickListener cho TextView "tvAmount"
        tvAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Đặt OnClickListener cho item_note
        itemNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}