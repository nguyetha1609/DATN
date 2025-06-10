package org.o7planning.project_04.activities;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import org.o7planning.project_04.R;
import org.o7planning.project_04.databases.CategoryDAO;
import org.o7planning.project_04.model.category;

import org.o7planning.project_04.databases.DBHelper;


public class EditCategoryActivity extends AppCompatActivity {
    private static final int REQUEST_ICON_PICKER = 1001;
   private EditText etName;
   private ImageView selectedIcon;
   private Button btnSave;
    private int selectedIconResId = R.drawable.ic_default;
    private String selectedIconName = "ic_default";
   private int ID;
   private String loaiDM ;
   private CategoryDAO dbHelper;
   private category currentCategory;

   @Override
    protected  void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_edit_category);

       MaterialToolbar toolbar = findViewById(R.id.toolbar_edit_category);
       setSupportActionBar(toolbar);
       etName = findViewById(R.id.edt_category_name);
       selectedIcon = findViewById(R.id.selected_icon);
       btnSave= findViewById(R.id.btn_save);
       dbHelper = new CategoryDAO(this);

       toolbar.setNavigationOnClickListener(v -> finish());




       //Lay id tu intent
       ID= getIntent().getIntExtra("ID_DM",-1);
       if(ID ==-1){
           Toast.makeText(this,"Khong tim thay danh muc",Toast.LENGTH_SHORT).show();
           finish();
           return;
       }
       // lay danh muc tu db
       currentCategory = dbHelper.getCategoryById(ID);
       if(currentCategory == null){
           Toast.makeText(this,"Danh muc khong ton tai",Toast.LENGTH_SHORT).show();
           finish();
           return;
       }
       //lay loaiDM tu intent
       loaiDM = getIntent().getStringExtra("LoaiDM");
       if(loaiDM == null) loaiDM ="ChiTieu";

       //gan thong tin
       etName.setText(currentCategory.getTenDM());
       selectedIconName = currentCategory.getHinhAnh();
       int resId = getResources().getIdentifier(selectedIconName,"drawable",getPackageName());
       if(resId !=0) selectedIcon.setImageResource(resId);


       // Nút chọn icon
      selectedIcon.setOnClickListener(v ->openIconPicker());

       //Luu thong tin
       btnSave.setOnClickListener(v -> {
           String newName = etName.getText().toString().trim();

           if(newName.isEmpty()){
               Toast.makeText(this,"Vui lòng nhập tên danh mục",Toast.LENGTH_SHORT).show();
               return;
           }
           currentCategory.setTenDM(newName);
           currentCategory.setLoaiDM(loaiDM);
           currentCategory.setHinhAnh(selectedIconName);  // cập nhật icon mới

           dbHelper.updateCategory(currentCategory);
           Toast.makeText(this,"Đã lưu danh mục",Toast.LENGTH_SHORT).show();
           setResult(RESULT_OK);
           finish();
       });

   }
    private void openIconPicker() {
        IconPickerBottomSheet picker = new IconPickerBottomSheet();

        picker.setIconSelectedListener((selectedIconName, resId) -> {
            this.selectedIconName = selectedIconName;
            selectedIcon.setImageResource(resId);
        });

        picker.show(getSupportFragmentManager(), picker.getTag());
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_ICON_PICKER && resultCode == RESULT_OK && data != null) {
//            selectedIconResId = data.getIntExtra("iconResId", R.drawable.ic_default);
//            selectedIconName= data.getStringExtra("iconName");
//            selectedIcon.setImageResource(selectedIconResId);
//        }
//    }


}
