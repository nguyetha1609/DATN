package org.o7planning.project_04.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.project_04.R;
import org.o7planning.project_04.model.category;

import org.o7planning.project_04.databases.DBHelper;

import java.util.ArrayList;

public class EditCategoryActivity extends AppCompatActivity {
   private EditText etName;
   private ImageView imgIcon;
   private Button btnSave,btnchoseIcon;
   private TextView btnCancel,btndelete;

    private int selectedIconResId = R.drawable.ic_default;
    private String selectedIconName = "ic_default";
    private ImageView imgSelectedIcon;
private Spinner spinner;
private ArrayAdapter<String > spinnerAdapter;
   private int ID;
private String iconname ;
   private DBHelper dbHelper;
   private category currentCategory;

   @Override
    protected  void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_edit_category);

       btnCancel = findViewById(R.id.btn_cancel);
       btndelete= findViewById(R.id.btn_delete);
       etName = findViewById(R.id.edt_category_name);
       imgIcon = findViewById(R.id.selected_icon);
       btnchoseIcon = findViewById(R.id.btn_choose_icon);
       btnSave= findViewById(R.id.btn_save);
        spinner = findViewById(R.id.spinner_loai_dm);
       dbHelper = new DBHelper(this);

       // Cài đặt Spinner
       ArrayAdapter<String> adapter = new ArrayAdapter<>(
               this,
               android.R.layout.simple_spinner_item,
               new String[]{"-- Loại danh mục --", "Chi tiêu", "Thu nhập"}
       );
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       spinner.setAdapter(adapter);


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
       //gan thong tin
       etName.setText(currentCategory.getTenDM());
       iconname = currentCategory.getHinhAnh();
       int resId = getResources().getIdentifier(iconname,"drawable",getPackageName());
       if(resId !=0) imgIcon.setImageResource(resId);

       // Gán loại danh mục vào spinner
       String loai = currentCategory.getLoaiDM();
       if (loai.equals("ChiTieu")) {
           spinner.setSelection(1);
       } else if (loai.equals("ThuNhap")) {
           spinner.setSelection(2);
       }

       //huy
       btnCancel.setOnClickListener(v -> finish());

       // xoa
       btndelete.setOnClickListener(v -> {
           new AlertDialog.Builder(this).setTitle("Xac nhan xoa")
                   .setMessage("Ban co chac chan muon xoa danh muc nay?")
                   .setPositiveButton("Xoa",(dialog, which) -> {
                       boolean deleted = dbHelper.deleteCategory(ID, this);
                       if (deleted) {
                           Toast.makeText(this, "Đã xóa danh mục", Toast.LENGTH_SHORT).show();
                           setResult(RESULT_OK);
                           finish();
                       }
                   })
                   .setNegativeButton("Huy",null)
                   .show();
       });
       // Nút chọn icon
       btnchoseIcon.setOnClickListener(v -> {
           IconPickerBottomSheet bottomSheet = new IconPickerBottomSheet();
           bottomSheet.setIconSelectedListener((iconName,  iconResId) -> {
               selectedIconName = iconName;
               selectedIconResId = iconResId;
               imgIcon.setImageResource( iconResId);
           });
           bottomSheet.show(getSupportFragmentManager(), "IconPicker");
       });

       //Luu thong tin
       btnSave.setOnClickListener(v -> {
           String newName = etName.getText().toString().trim();
           String selectedLoai = spinner.getSelectedItem().toString();

           if(newName.isEmpty()){
               Toast.makeText(this,"Vui lòng nhập tên danh mục",Toast.LENGTH_SHORT).show();
               return;
           }
           if(selectedLoai.equals("-- Loại danh mục --")){
               Toast.makeText(this,"Vui lòng chọn loại danh mục",Toast.LENGTH_SHORT).show();
               return;
           }

           String loaiDb;
           if (selectedLoai.equals("Chi tiêu")) {
               loaiDb = "ChiTieu";
           } else if (selectedLoai.equals("Thu nhập")) {
               loaiDb = "ThuNhap";
           } else {
               Toast.makeText(this, "Loại danh mục không hợp lệ", Toast.LENGTH_SHORT).show();
               return;
           }

           currentCategory.setTenDM(newName);
           currentCategory.setLoaiDM(loaiDb);
           currentCategory.setHinhAnh(selectedIconName);  // cập nhật icon mới

           dbHelper.updateCategory(currentCategory);
           Toast.makeText(this,"Đã lưu danh mục",Toast.LENGTH_SHORT).show();
           setResult(RESULT_OK);
           finish();
       });

   }


}
