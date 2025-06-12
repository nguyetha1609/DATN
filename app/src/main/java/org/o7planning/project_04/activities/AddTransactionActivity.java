package org.o7planning.project_04.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Import Toolbar

import com.google.android.material.textfield.TextInputEditText;

import org.o7planning.project_04.R;

public class AddTransactionActivity extends AppCompatActivity {

    private TextInputEditText etAmount;
    private LinearLayout itemType, itemDate, itemTime, itemNote;
    private LinearLayout btnGallery, btnCamera;
    private TextView toolbarSave;
    private Toolbar toolbar; // Khai báo Toolbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense); // Đặt layout cho Activity này

        // Khởi tạo các View
        toolbar = findViewById(R.id.toolbar_add_expense); // Khởi tạo Toolbar
        etAmount = findViewById(R.id.etAmount); // Khởi tạo EditText cho số tiền
        //itemType = findViewById(R.id.item_type); // Khởi tạo LinearLayout cho loại
        itemDate = findViewById(R.id.item_date); // Khởi tạo LinearLayout cho ngày
        itemTime = findViewById(R.id.item_time); // Khởi tạo LinearLayout cho giờ
        itemNote = findViewById(R.id.item_note); // Khởi tạo LinearLayout cho ghi chú
        btnGallery = findViewById(R.id.btnGallery); // Khởi tạo LinearLayout cho nút gallery
        btnCamera = findViewById(R.id.btnCamera); // Khởi tạo LinearLayout cho nút camera
        toolbarSave = findViewById(R.id.toolbar_save); // Khởi tạo TextView cho nút lưu trên toolbar

        // Thiết lập Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_add_expense); // Đặt tiêu đề từ tài nguyên chuỗi
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Bật nút quay lại
            getSupportActionBar().setDisplayShowHomeEnabled(true); // Hiển thị nút quay lại
        }

        // Đặt OnClickListener cho mũi tên quay lại trên toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Xử lý sự kiện nhấp vào nút quay lại
            }
        });

        // Đặt OnClickListener cho TextView "Lưu" trên toolbar
        toolbarSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý hành động lưu
                Toast.makeText(AddTransactionActivity.this, "Lưu đã được click", Toast.LENGTH_SHORT).show();
                // Bạn thường sẽ lưu dữ liệu chi tiêu ở đây
            }
        });

        // Đặt OnClickListener cho các mục trong card
        itemType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddTransactionActivity.this, "Loại đã được click", Toast.LENGTH_SHORT).show();
                // Mở activity hoặc dialog để chọn loại chi tiêu
            }
        });

        itemDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddTransactionActivity.this, "Ngày đã được click", Toast.LENGTH_SHORT).show();
                // Mở dialog chọn ngày
            }
        });

        itemTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddTransactionActivity.this, "Giờ đã được click", Toast.LENGTH_SHORT).show();
                // Mở dialog chọn giờ
            }
        });

        itemNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddTransactionActivity.this, "Ghi Chú đã được click", Toast.LENGTH_SHORT).show();
                // Mở activity hoặc dialog để thêm ghi chú
            }
        });

        // Đặt OnClickListener cho các tùy chọn ảnh
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddTransactionActivity.this, "Gallery đã được click", Toast.LENGTH_SHORT).show();
                // Mở thư viện để chọn ảnh
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddTransactionActivity.this, "Camera đã được click", Toast.LENGTH_SHORT).show();
                // Mở camera để chụp ảnh
            }
        });
    }
}