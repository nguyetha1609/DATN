package org.o7planning.project_04.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.o7planning.project_04.activities.EditCategoryActivity;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.model.category;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import org.o7planning.project_04.activities.AddCategoryActivity;

import org.o7planning.project_04.Adapter.categoryAdapter;
import org.o7planning.project_04.R;

public class categoryfragment extends Fragment {
public categoryfragment (){}
    private String loaiDM= "ChiTieu";
private DBHelper dbHelper;
private RecyclerView recyclerView;
private categoryAdapter adapter;
    private static final int REQUEST_EDIT_CATEGORY = 2001;
    private static final int REQUEST_ADD_CATEGORY = 1001;


    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.categoryfragment, container, false);

         recyclerView = view.findViewById(R.id.recycler_categories);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

        Button btn_expense = view.findViewById(R.id.btn_expense);
        Button btn_income = view.findViewById(R.id.btn_income);

         dbHelper = new DBHelper(getContext());
        dbHelper.createDatabaseIfNone();

        if (getArguments() != null) {
            loaiDM = getArguments().getString("LoaiDM", "ChiTieu");
            btn_expense.setAlpha(1.0f);
            btn_income.setAlpha(0.4f);
        }
        Log.d("categoryfragment", "LoaiDM hiện tại = '" + loaiDM + "'");

       List<category> categoryList = dbHelper.getAllCategories(loaiDM);
        Log.d("categoryfragment", "Số danh mục: " + categoryList.size());

         adapter = new categoryAdapter(getContext(), categoryList);
        recyclerView.setAdapter(adapter);

//nút + thêm danh mục
        ImageButton fabAdd = view.findViewById(R.id.btn_add_category);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddCategoryActivity.class);
            startActivityForResult(intent, REQUEST_ADD_CATEGORY);
        });




//        List<category> cate= dbHelper.getAllCategories(loaiDM);
//
//        adapter = new categoryAdapter(getContext(), cate);
//        recyclerView.setAdapter(adapter);

        // Xử lý khi ấn Chi tiêu
        btn_expense.setOnClickListener(v -> {
            loaiDM = "ChiTieu";
            btn_expense.setAlpha(1.0f);
            btn_income.setAlpha(0.4f);
            fadeRecyclerView(recyclerView);
            loadCategoryList();
        });

        // Xử lý khi ấn Thu nhập
        btn_income.setOnClickListener(v -> {
            loaiDM = "ThuNhap";
            btn_expense.setAlpha(0.4f);
            btn_income.setAlpha(1.0f);
            fadeRecyclerView(recyclerView);
            loadCategoryList();
        });

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.cate_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        return view;

    }
    private void openEditCategory(int categoryId) {
        Intent intent = new Intent(getContext(), EditCategoryActivity.class);
        intent.putExtra("ID_DM", categoryId);
        startActivityForResult(intent, REQUEST_EDIT_CATEGORY);
    }

    private void loadCategories(DBHelper dbHelper, RecyclerView recyclerView, String loaiDM) {
        List<category> categoryList = dbHelper.getAllCategories(loaiDM);
        categoryAdapter adapter = new categoryAdapter(getContext(), categoryList);
        recyclerView.setAdapter(adapter);
    }

    private void fadeRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAlpha(0f);
        recyclerView.animate().alpha(1f).setDuration(300).start();
    }
    @Override
    public void onResume() {
        super.onResume();
        loadCategoryList();
    }
    private void loadCategoryList() {
        List<category> updatedList = dbHelper.getAllCategories(loaiDM);
        adapter.setData(updatedList);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_ADD_CATEGORY || requestCode == REQUEST_EDIT_CATEGORY) && resultCode == getActivity().RESULT_OK) {
            loadCategoryList();  // tải lại danh sách để cập nhật UI
        }
    }
}
