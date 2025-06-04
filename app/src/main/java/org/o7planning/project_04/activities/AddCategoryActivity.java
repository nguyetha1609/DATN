package org.o7planning.project_04.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.o7planning.project_04.R;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.model.category;


public class AddCategoryActivity extends AppCompatActivity {
    private static final int REQUEST_ICON_PICKER = 1001;
    private int selectedIconResId = R.drawable.ic_default;
    private String selectedIconName = "ic_default";
    private ImageView imgSelectedIcon;
    private EditText edtCatename;
    private Spinner spinnerLoaiDM;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_categoryactivity);

        edtCatename = findViewById(R.id.edt_category_name);
        spinnerLoaiDM=findViewById(R.id.spinner_loai_dm);
        imgSelectedIcon= findViewById(R.id.selected_icon);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"-- Loại danh mục --", "Chi tiêu", "Thu nhập"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoaiDM.setAdapter(adapter);

        //Nút hủy
        TextView btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(v -> {
            finish();
        });
        // nut chon icon
        findViewById(R.id.btn_choose_icon).setOnClickListener(v -> {
            IconPickerBottomSheet bottomSheet = new IconPickerBottomSheet();
            bottomSheet.setIconSelectedListener((iconName, resId) -> {
                selectedIconName = iconName;
                selectedIconResId = resId;
                imgSelectedIcon.setImageResource(resId);
            });
            bottomSheet.show(getSupportFragmentManager(), "IconPicker");

        });
        //nut them
        findViewById(R.id.btn_save).setOnClickListener(v -> {
            String tenDM=edtCatename.getText().toString().trim();
            String loaiDM= spinnerLoaiDM.getSelectedItem().toString();

            if(tenDM.isEmpty()){
                Toast.makeText(this, "Tên danh mục không được để trống", Toast.LENGTH_SHORT).show();
                 return;
            }
            if(loaiDM.equals("-- Loại danh mục --")){
                Toast.makeText(this,"Vui lòng chọn lọai danh mục",Toast.LENGTH_SHORT).show();
                return;
            }
            // chuyen doi chon danh muc
            String loai = loaiDM.equals("Chi tiêu")? "ChiTieu":"ThuNhap";

            // them vao csdl
            category cate = new category(tenDM,loai,selectedIconName,2);
            DBHelper db = new DBHelper(this);
            db.addcate(cate);

            Toast.makeText(this, "Thêm danh mục thành công", Toast.LENGTH_SHORT).show();

            setResult(RESULT_OK);
            finish();
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ICON_PICKER && resultCode == RESULT_OK && data != null) {
            selectedIconResId = data.getIntExtra("iconResId", R.drawable.ic_default);
            selectedIconName = data.getStringExtra("iconName");
            imgSelectedIcon.setImageResource(selectedIconResId);
        }
    }
}
