package org.o7planning.project_04.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import org.o7planning.project_04.R;
import org.o7planning.project_04.databases.CategoryDAO;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.model.category;

import java.util.List;


public class AddCategoryActivity extends AppCompatActivity {
    private static final int REQUEST_ICON_PICKER = 1001;
    private int selectedIconResId = R.drawable.ic_default;

    private String iconName = "ic_default";
    private ImageView imgSelectedIcon;
    private EditText edtCatename;
    private String loaiDM ="ChiTieu";
    private int selectedParentId = -1;
    CategoryDAO db;
    LinearLayout tabParentContainer;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_categoryactivity);
        db = new CategoryDAO(this);
        //Lấy ID_TK
        SharedPreferences prefs = getSharedPreferences("LOGIN_PREF", MODE_PRIVATE);
        int idTK = prefs.getInt("ID_TK", -1);

        edtCatename = findViewById(R.id.edt_category_name);
        imgSelectedIcon= findViewById(R.id.selected_icon);
        btnSave = findViewById(R.id.btn_save);

        if (idTK == -1) {
            Toast.makeText(this, "Không lấy được ID_TK!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loaiDM = getIntent().getStringExtra("LoaiDM");
        if(loaiDM == null) loaiDM ="ChiTieu";


        // nút quay lại
        MaterialToolbar toolbar = findViewById(R.id.toolbar_add_category);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // nut chon icon
        imgSelectedIcon.setOnClickListener(v -> openIconPicker());

        //nut them
        btnSave.setOnClickListener(v -> {
            String tenDM = edtCatename.getText().toString().trim();

            if (tenDM.isEmpty()) {
                Toast.makeText(this, "Tên danh mục không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            if (db.isCateNameExists(tenDM, loaiDM, idTK)) {
                Toast.makeText(this, "Tên danh mục đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            category newCate = new category();
            newCate.setTenDM(tenDM);
            newCate.setLoaiDM(loaiDM);
            newCate.setHinhAnh(iconName);
            newCate.setDMMacDinh(2);   // 2 = danh mục người dùng tạo
            newCate.setID_TK(idTK);

            try {
                db.addcate(newCate, idTK);
                Toast.makeText(this, "Đã thêm danh mục", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi khi thêm danh mục", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

    }
    private void openIconPicker() {
        IconPickerBottomSheet picker = new IconPickerBottomSheet();

        picker.setIconSelectedListener((selectedIconName, resId) -> {
            this.iconName = selectedIconName;
            imgSelectedIcon.setImageResource(resId);
        });

        picker.show(getSupportFragmentManager(), picker.getTag());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ICON_PICKER && resultCode == RESULT_OK && data != null) {
            selectedIconResId = data.getIntExtra("iconResId", R.drawable.ic_default);
            iconName = data.getStringExtra("iconName");
            imgSelectedIcon.setImageResource(selectedIconResId);
        }
    }
}
