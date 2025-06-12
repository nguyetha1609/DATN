package org.o7planning.project_04.activities;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import org.o7planning.project_04.R;
import org.o7planning.project_04.databases.CategoryDAO;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.databases.LimitDAO;
import org.o7planning.project_04.model.Limit;
import org.o7planning.project_04.model.Category;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityAddSpendinglimit extends AppCompatActivity {

    private static final int REQUEST_SELECT_CATEGORY =1001;
    private LinearLayout llCategory;
    private TextView tvCategory, tvStartDate,tvEndDate;
    private DBHelper db;
    private Button btn_save_limit;
    private EditText et_limit_name,et_amount;
private CategoryDAO dbcate;
private LimitDAO dblimit;

    Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


    private ArrayList<Integer> selectedCategoryId = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spendinglimit);
        dbcate = new CategoryDAO(this);
        dblimit = new LimitDAO(this);

        llCategory = findViewById(R.id.ll_category);
        tvCategory = findViewById(R.id.tv_category);
        db = new DBHelper(this);
        tvStartDate = findViewById(R.id.tv_start_date);
        tvEndDate = findViewById(R.id.tv_end_date);
        LinearLayout llStartDate = findViewById(R.id.ll_start_date);
        LinearLayout llEndDate = findViewById(R.id.ll_end_date);
        btn_save_limit= findViewById(R.id.btn_save_limit);
        et_limit_name=findViewById(R.id.et_limit_name);
        et_amount = findViewById(R.id.et_amount);

        llStartDate.setOnClickListener(v -> showDatePicker(tvStartDate));
        llEndDate.setOnClickListener(v -> showDatePicker(tvEndDate));


// nút quay lại
        MaterialToolbar toolbar = findViewById(R.id.toolbar_add_limit);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        llCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this,CategoryMultiSelectActivity.class);
            intent.putIntegerArrayListExtra("selected_category_id",new ArrayList<>(selectedCategoryId));
            startActivityForResult(intent,REQUEST_SELECT_CATEGORY);
        });
// ngày bắt đâu
        String currentDate = sdf.format(calendar.getTime());
        tvStartDate.setText(currentDate);

        // btn luu
        btn_save_limit.setOnClickListener(v -> {
            String tenHM= et_limit_name.getText().toString().trim();
            String sotien = et_amount.getText().toString().trim();
            String ngayBD = tvStartDate.getText().toString().trim();
            String ngayKT = tvEndDate.getText().toString().trim();
            List<Integer> listDM = selectedCategoryId;

            //validate du lieu
            if(tenHM.isEmpty() || sotien.isEmpty() || ngayBD.isEmpty() || ngayKT.isEmpty()){
                Toast.makeText(this,"Vui long dien du thong tin",Toast.LENGTH_SHORT).show();
                return;
            }
            long soTien;
            try {
                soTien = Long.parseLong(sotien);
                if (soTien < 0) {
                    Toast.makeText(this, "Số tiền không được nhỏ hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
           // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try{
                Date dateStart = sdf.parse(ngayBD);
                Date dateEnd = sdf.parse(ngayKT);
                if(dateStart !=null && dateEnd !=null &&dateEnd.before(dateStart)){
                    Toast.makeText(this,"Ngay ket thuc phai sau ngay bat dau",Toast.LENGTH_SHORT).show();
                    return;
                }
            }catch (ParseException e){
                Toast.makeText(this,"Loi dinh dang ngay",Toast.LENGTH_SHORT).show();
                return;
            }
            if(listDM == null || listDM.isEmpty()){
                Toast.makeText(this,"Vui long chon it nhat 1 danh muc",Toast.LENGTH_SHORT).show();
                return;
            }
            Limit limit = new Limit();
            limit.setTenHM(tenHM);
            limit.setSoTien(soTien);
            limit.setNgayGD(ngayBD);
            limit.setNgayKetThuc(ngayKT);
            limit.setListDanhMuc(listDM);

            DBHelper db = new DBHelper(this);
            boolean result = dblimit.insertLimit(limit);
            if(result){
                Toast.makeText(this,"Them han muc thanh cong",Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            }else {
                Toast.makeText(this,"Them han muc that bai",Toast.LENGTH_SHORT).show();
            }


        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_CATEGORY && resultCode == RESULT_OK) {
            ArrayList<Integer> result = data.getIntegerArrayListExtra("selected_category_id");
            if(result !=null){
                selectedCategoryId.clear();
                selectedCategoryId.addAll(result);
            }


            if (selectedCategoryId != null) {
                int size = selectedCategoryId.size();

                if (size == 0) {
                    tvCategory.setText("Không có danh mục nào");
                } else if (size == 1) {
                    Category cate = dbcate.getCategoryById(selectedCategoryId.get(0));
                    tvCategory.setText(cate.getTenDM());
                } else {
                    // Lấy tên danh mục đầu tiên
                    Category firstCate = dbcate.getCategoryById(selectedCategoryId.get(0));
                    String firstName = firstCate != null ? firstCate.getTenDM() : "";

                    int othersCount = size - 1;
                    tvCategory.setText(firstName + " + " + othersCount + " danh mục khác");
                }
            }
        }
    }

    // ham hien thi datepicker
    private void showDatePicker(TextView targetTextView){
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view ,selectedYear,selectedMonth,selectedDay) ->{
                    String formattedDate = String.format("%04d-%02d-%02d",selectedYear,selectedMonth +1,selectedDay);
                    targetTextView.setText(formattedDate);
                },
                year,month,day
        );
            datePickerDialog.show();
    }


}
