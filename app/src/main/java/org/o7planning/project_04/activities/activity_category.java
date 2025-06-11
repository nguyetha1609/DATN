package org.o7planning.project_04.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import org.o7planning.project_04.Adapter.categoryAdapter;
import org.o7planning.project_04.R;
import org.o7planning.project_04.databases.CategoryDAO;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.model.category;

import java.util.List;

public class activity_category extends AppCompatActivity {
    private String loaiDM = "ChiTieu";
    private CategoryDAO dbHelper;
    private DBHelper db;
    private RecyclerView recyclerView;
    private categoryAdapter adapter;
    private static final int REQUEST_EDIT_CATEGORY = 2001;
    private static final int REQUEST_ADD_CATEGORY = 1001;
    private boolean isEditMode = false;
    private MaterialToolbar topAppbar;
    private Button btnAddCategory;
    private LinearLayout tabContainer;
    private MaterialButton btnExpense, btnIncome;
    private int selectedParentId =-1;
    private int currentParentId = -1;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category); // dùng layout mới


        recyclerView = findViewById(R.id.recyclerViewCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

         btnExpense = findViewById(R.id.btn_expense);
         btnIncome = findViewById(R.id.btn_income);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        topAppbar = findViewById(R.id.topAppBar);
        db = new DBHelper(this);
        db.createDatabaseIfNone();

        dbHelper = new CategoryDAO(this);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("ID_TK", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getIntent() != null) {
            loaiDM = getIntent().getStringExtra("LoaiDM");
            if (loaiDM == null) loaiDM = "ChiTieu";
        }

        updateButtonStates(btnExpense, btnIncome);
        loadCategoryList();


        recyclerView.setAdapter(adapter);
//Them danh muc
        Button btn = findViewById(R.id.btnAddCategory);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCategoryActivity.class);
            startActivityForResult(intent, REQUEST_ADD_CATEGORY);
        });




        btnExpense.setOnClickListener(v -> {
            loaiDM = "ChiTieu";
            updateButtonStates(btnExpense, btnIncome);
            fadeRecyclerView(recyclerView);
            loadCategoryList();
        });

        btnIncome.setOnClickListener(v -> {
            loaiDM = "ThuNhap";
            updateButtonStates(btnExpense, btnIncome);
            fadeRecyclerView(recyclerView);
            loadCategoryList();
        });

        topAppbar.setNavigationOnClickListener(v -> finish());

        topAppbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() ==R.id.action_edit){
                toggleEditMode();
                return true;
            }
            return false;
        });



    }
    // trang thai btn chi tieu va thu nhap
    private void updateButtonStates(MaterialButton btnExpense, MaterialButton btnIncome) {
       if(loaiDM.equals("ChiTieu")){
           btnExpense.setTypeface(null, Typeface.BOLD);
           btnExpense.setTextColor(Color.BLACK);
           btnExpense.setChecked(true);

           btnIncome.setTypeface(null,Typeface.NORMAL);
           btnIncome.setTextColor(Color.parseColor("#808080"));
           btnIncome.setChecked(false);
       }else {
           btnIncome.setTypeface(null,Typeface.BOLD);
           btnIncome.setTextColor(Color.BLACK);
           btnIncome.setChecked(true);

           btnExpense.setTypeface(null,Typeface.NORMAL);
           btnExpense.setTextColor(Color.parseColor("#808080"));
           btnIncome.setChecked(false);
       }

    }

    private void toggleEditMode(){
        isEditMode = !isEditMode;
        adapter.setEditMode(isEditMode);
        adapter.notifyDataSetChanged();

        MenuItem editItem = topAppbar.getMenu().findItem(R.id.action_edit);
        if (isEditMode) {
            editItem.setIcon(R.drawable.ic_check);
            editItem.setTitle("Xong");
            btnAddCategory.setEnabled(false);
        } else {
            editItem.setIcon(R.drawable.ic_edit);
            editItem.setTitle("Sửa");
            btnAddCategory.setEnabled(true);
        }
    }
    private void fadeRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAlpha(0f);
        recyclerView.animate().alpha(1f).setDuration(300).start();
    }

    private void loadCategoryList() {
        List<category> list = dbHelper.getAllCategories(loaiDM,userId);
        if (adapter == null) {
            adapter = new categoryAdapter(this, list);
            adapter.setEditMode(isEditMode);

            // Gán listener xử lý xóa và sửa
            adapter.setOnItemActionListener(new categoryAdapter.OnItemActionListener() {
                @Override
                public void onDelete(category cat) {
                    showConfirmDeleteDialog(cat);
                }

                @Override
                public void onEdit(category cat) {
                    Intent intent = new Intent(activity_category.this, EditCategoryActivity.class);
                    intent.putExtra("ID_DM", cat.getID());
                    startActivityForResult(intent, REQUEST_EDIT_CATEGORY);
                }
            });

            recyclerView.setAdapter(adapter);
        } else {
            adapter.setData(list);
            adapter.notifyDataSetChanged();
        }
    }
    private void showConfirmDeleteDialog(category cat) {
        if (dbHelper.isCategoryUsedAnywhere(cat.getID())) {
            new AlertDialog.Builder(this)
                    .setTitle("Không thể xóa")
                    .setMessage("Danh mục \"" + cat.getTenDM() + "\" đang được sử dụng trong giao dịch hoặc hạn mức và không thể xóa.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa danh mục \"" + cat.getTenDM() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean success = dbHelper.deleteCategory(cat.getID(), userId);
                    if (success) {
                        loadCategoryList();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadCategoryList();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_ADD_CATEGORY || requestCode == REQUEST_EDIT_CATEGORY) && resultCode == RESULT_OK) {
            loadCategoryList();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_limit_detail, menu);
        return true;
    }

}
