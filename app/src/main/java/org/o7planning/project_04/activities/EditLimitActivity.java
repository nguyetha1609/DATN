package org.o7planning.project_04.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import org.o7planning.project_04.R;
import org.o7planning.project_04.databases.CategoryDAO;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.databases.LimitDAO;
import org.o7planning.project_04.model.Limit;
import org.o7planning.project_04.model.category;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditLimitActivity extends AppCompatActivity {
    public static final String EXTRA_LIMIT_ID ="limit_Id";
    private  static final int REQUEST_CATEGORY_PICKER = 1001;
    private final Calendar calendar = Calendar.getInstance();

    private EditText edtAmount, edtName;
    private TextView tvStartDate, tvEndDate,tv_category;
    private  int limiId;
    private LinearLayout ll_category;
    private Button btn_save_limit , btn_delete_limit;
    private CategoryDAO dbcate;
    private LimitDAO dblimit;
    private int idTK;
    private ArrayList<Integer> selectedCategoryIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_limit);
        dblimit = new LimitDAO(this);
        dbcate = new CategoryDAO(this);

        // anh xa cac view
        edtAmount= findViewById(R.id.et_amount);
        edtName =findViewById(R.id.et_limit_name);
        tvStartDate= findViewById(R.id.tv_start_date);
        tvEndDate=findViewById(R.id.tv_end_date);
        ll_category = findViewById(R.id.ll_category);
        tv_category= findViewById(R.id.tv_category);
        btn_delete_limit= findViewById(R.id.btn_delete_limit);
        btn_save_limit= findViewById(R.id.btn_save_limit);


        MaterialToolbar toolbar = findViewById(R.id.toolbar_edit_limit);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //Lấy ID_TK
        SharedPreferences prefs = getSharedPreferences("LOGIN_PREF", MODE_PRIVATE);
                idTK = prefs.getInt("ID_TK", -1);

        //Lay ID tu intent
        limiId = getIntent().getIntExtra(EXTRA_LIMIT_ID, -1);
        if(limiId ==-1){
            Toast.makeText(this,"Khong tim thay han muc",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadSelectedCategories(limiId);

        ll_category.setOnClickListener(v -> {
            Intent intent = new Intent(EditLimitActivity.this,CategoryMultiSelectActivity.class);
            intent.putIntegerArrayListExtra("selected_category_id",selectedCategoryIds);
            startActivityForResult(intent,REQUEST_CATEGORY_PICKER);
        });
        tvStartDate.setOnClickListener(v -> showDatePicker(tvStartDate));
        tvEndDate.setOnClickListener(v -> showDatePicker(tvEndDate));


        btn_save_limit.setOnClickListener(v -> saveLimit());

        btn_delete_limit.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa hạn mức này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        DBHelper db = new DBHelper(this);
                        dblimit.deleteLimit(limiId,idTK);
                        Toast.makeText(this, "Đã xóa hạn mức", Toast.LENGTH_SHORT).show();

                        // Quay lại màn hình SpendingLimitActivity
                        Intent intent = new Intent();
                        intent.putExtra("limit_deleted", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });


        //load du lieu tu db theo id
        loadLimitData(limiId);
        updateCategoryText();
    }
    private void loadLimitData(int id){
        Limit limit = dblimit.getLimitById(id);

        if(limit == null){
            Toast.makeText(this,"Khong tim thay han muc",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //gan dl
        edtAmount.setText(String.valueOf(limit.getSoTien()));
        edtName.setText(limit.getTenHM());

        tvStartDate.setText(limit.getNgayGD());
        tvEndDate.setText(limit.getNgayKetThuc());

    }
    private void showDatePicker(TextView target) {
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    target.setText(formatted);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }


    private void saveLimit(){
        String name = edtName.getText().toString().trim();
        String amountStr = edtAmount.getText().toString().trim();
        String startDate = tvStartDate.getText().toString().trim();
        String endDate = tvEndDate.getText().toString().trim();

        if(name.isEmpty() || amountStr.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || selectedCategoryIds.isEmpty()){
            Toast.makeText(this,"Vui lòng nhập đầy đủ thông tin",Toast.LENGTH_SHORT).show();
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(amountStr);
            if (amount < 0) {
                Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            if (start != null && end != null && end.before(start)) {
                Toast.makeText(this, "Ngày kết thúc phải sau ngày bắt đầu", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Lỗi định dạng ngày", Toast.LENGTH_SHORT).show();
            return;
        }

        Limit limit = new Limit();
        limit.setID_HM(limiId);
        limit.setTenHM(name);
        limit.setSoTien(amount);
        limit.setNgayGD(startDate);
        limit.setNgayKetThuc(endDate);
        limit.setID_TK(idTK);
        limit.setListDanhMuc(selectedCategoryIds);

        boolean success = dblimit.updateLimit(limit,idTK);

        if (success) {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSelectedCategories(int limiId){
        selectedCategoryIds =dblimit.getCategoryIdsByLimitId(limiId);
    }
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == REQUEST_CATEGORY_PICKER && resultCode ==RESULT_OK && data !=null){
            ArrayList<Integer> result = data.getIntegerArrayListExtra("selected_category_id");
            if (result != null) {
                selectedCategoryIds.clear();
                selectedCategoryIds.addAll(result);
                updateCategoryText(); // cập nhật TextView
            }


            if (selectedCategoryIds != null) {
                int size = selectedCategoryIds.size();

                if (size == 0) {
                    tv_category.setText("Không có danh mục nào");
                } else if (size == 1) {
                    category cate = dbcate.getCategoryById(selectedCategoryIds.get(0));
                    tv_category.setText(cate.getTenDM());
                } else {
                    // Lấy tên danh mục đầu tiên
                    category firstCate = dbcate.getCategoryById(selectedCategoryIds.get(0));
                    String firstName = firstCate != null ? firstCate.getTenDM() : "";

                    int othersCount = size - 1;
                    tv_category.setText(firstName + " + " + othersCount + " danh mục khác");
                }
            }
        }
    }

    private String formatDate(String dateStr){
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }

    private void updateCategoryText() {

        int size = selectedCategoryIds.size();

        if (size == 0) {
            tv_category.setText("Không có danh mục nào");
        } else if (size == 1) {
            category cate = dbcate.getCategoryById(selectedCategoryIds.get(0));
            if (cate != null) {
                tv_category.setText(cate.getTenDM());
            } else {
                tv_category.setText("Danh mục không tồn tại");
            }
        } else {
            category firstCate = dbcate.getCategoryById(selectedCategoryIds.get(0));
            String firstName = (firstCate != null) ? firstCate.getTenDM() : "Danh mục";

            int othersCount = size - 1;
            tv_category.setText(firstName + " + " + othersCount + " danh mục khác");
        }
    }


}
