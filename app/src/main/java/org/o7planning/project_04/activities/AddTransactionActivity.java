package org.o7planning.project_04.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.o7planning.project_04.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private TextView tvAmount;
    private LinearLayout itemDate, itemTime, itemNote;
    private TextView toolbarSave;
    private Toolbar toolbar;
    private ImageView imgCategory; // Thêm ImageView cho category
    private TextView tvDateLabel; // TextView để hiển thị ngày đã chọn
    private TextView tvTimeLabel; // TextView để hiển thị giờ đã chọn
    private TextView tvNoteHint; // TextView để hiển thị ghi chú

    // Các biến để lưu trữ dữ liệu
    private double amount = 0.0;
    private Calendar selectedDate = Calendar.getInstance();
    private String note = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Khởi tạo các View
        toolbar = findViewById(R.id.toolbar_add_expense);
        tvAmount = findViewById(R.id.tvAmount);
        itemDate = findViewById(R.id.item_date);
        itemTime = findViewById(R.id.item_time);
        itemNote = findViewById(R.id.item_note);
        toolbarSave = findViewById(R.id.toolbar_save);
        imgCategory = findViewById(R.id.imgCategory); // Ánh xạ ImageView category
        tvDateLabel = findViewById(R.id.tvDateLabel); // Ánh xạ TextView ngày
        tvTimeLabel = findViewById(R.id.tvTimeLabel); // Ánh xạ TextView giờ
        tvNoteHint = findViewById(R.id.tvNoteHint); // Ánh xạ TextView ghi chú

        // Thiết lập Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(""); // Xóa tiêu đề mặc định của Toolbar
        }

        // Đặt tiêu đề tùy chỉnh cho Toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.title_add_expense);

        // Đặt OnClickListener cho nút back trên toolbar
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTransactionActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Đặt OnClickListener cho TextView "Lưu" trên toolbar
        toolbarSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý hành động lưu dữ liệu vào database
                saveTransactionToDatabase();
            }
        });

        // Đặt OnClickListener cho ImageView category
        imgCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTransactionActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        // Đặt OnClickListener cho TextView "tvAmount"
        tvAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAmountInputDialog();
            }
        });

        // Đặt OnClickListener cho item_date
        itemDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Đặt OnClickListener cho item_time
        itemTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // Đặt OnClickListener cho item_note
        itemNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoteInputDialog();
            }
        });

        // Cập nhật ngày và giờ ban đầu
        updateDateLabel();
        updateTimeLabel();
    }
    //nhập số tiền
    private void showAmountInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập số tiền");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    amount = Double.parseDouble(input.getText().toString());
                    tvAmount.setText(String.format(Locale.getDefault(), "%.0f", amount));
                } catch (NumberFormatException e) {
                    Toast.makeText(AddTransactionActivity.this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    //Nhập ngày
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, monthOfYear);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateLabel();
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateDateLabel() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        tvDateLabel.setText(sdf.format(selectedDate.getTime()));
    }

    //Nhập thời gian
    private void showTimePickerDialog() {
        int hour = selectedDate.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDate.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDate.set(Calendar.MINUTE, minute);
                        updateTimeLabel();
                    }
                }, hour, minute, true); // true cho định dạng 24 giờ
        timePickerDialog.show();
    }

    private void updateTimeLabel() {
        String timeFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
        tvTimeLabel.setText(sdf.format(selectedDate.getTime()));
    }

    //Viết ghi chú
    private void showNoteInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm ghi chú");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(note); // Hiển thị ghi chú hiện tại nếu có
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                note = input.getText().toString();
                tvNoteHint.setText(note.isEmpty() ? getString(R.string.hint_note) : note);
                if (note.isEmpty()) {
                    tvNoteHint.setTextColor(getResources().getColor(R.color.textHint));
                } else {
                    tvNoteHint.setTextColor(getResources().getColor(R.color.textPrimary));
                }
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    //Lưu vào database
    private void saveTransactionToDatabase() {
        // Đây là nơi bạn sẽ thêm logic để lưu dữ liệu vào database.
        // Ví dụ: Bạn có thể tạo một đối tượng Transaction và lưu nó.
        // Trong ví dụ này, chúng ta chỉ hiển thị một Toast.
        String message = String.format(Locale.getDefault(),
                "Lưu dữ liệu:\nSố tiền: %.0f\nNgày: %s\nGiờ: %s\nGhi chú: %s",
                amount,
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.getTime()),
                new SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedDate.getTime()),
                note);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Sau khi lưu, bạn có thể quay lại Activity chính hoặc đóng Activity này
        // Intent intent = new Intent(AddTransactionActivity.this, MainActivity.class);
        // startActivity(intent);
        // finish();
    }
}