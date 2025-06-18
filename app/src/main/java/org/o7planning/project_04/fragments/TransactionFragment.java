package org.o7planning.project_04.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.o7planning.project_04.Adapter.TransactionAdapter;
import org.o7planning.project_04.R;
import org.o7planning.project_04.activities.AddTransactionActivity;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.model.GIAODICH;
import org.o7planning.project_04.model.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionFragment extends Fragment {
    private Button btnAdd, btnChiTieu, btnThuNhap;
    private TextView tabExpense, tabIncome, filterDay, filterMonth, filterYear, filterAll;
    private static final int REQUEST_ADD_TRANSACTION = 1001;
    private static final int REQUEST_EDIT_TRANSACTION = 1002;
    private RecyclerView recyclerView;
    private List<GIAODICH> listGiaoDich;
    private Map<Integer, category> mapDanhMuc; // Map chứa danh mục
    private TransactionAdapter transactionAdapter;
    private String currentTransactionType = "all"; // Biến để lưu loại giao dịch hiện tại: "all", "expense", "income"


    private void loadTransactions() {
        listGiaoDich = new ArrayList<>();
        mapDanhMuc = new HashMap<>();

        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursorCategory = null;
        Cursor cursorGiaoDich = null;

        try {
            // 1. Tải danh mục vào mapDanhMuc trước
            cursorCategory = db.rawQuery("SELECT ID_DM, TenDM, HinhAnh, LoaiDM FROM DANHMUC", null);
            while (cursorCategory.moveToNext()) {
                int idDm = cursorCategory.getInt(0);
                String tenDm = cursorCategory.getString(1);
                String hinhAnh = cursorCategory.getString(2);
                String loaiDm = cursorCategory.getString(3);
                category cat = new category(idDm, tenDm, loaiDm, hinhAnh, 0);
                mapDanhMuc.put(idDm, cat);
            }

            // 2. Tải giao dịch và lọc theo loại
            String query = "SELECT ID_GD, ID_DM, SoTien, ThoiGian, GhiChu FROM GIAODICH";
            String orderBy = " ORDER BY ID_GD DESC";
            String whereClause = "";

            // Lọc theo loại giao dịch (chi tiêu/thu nhập)
            if ("expense".equals(currentTransactionType)) {
                List<Integer> expenseCategoryIds = new ArrayList<>();
                for (Map.Entry<Integer, category> entry : mapDanhMuc.entrySet()) {
                    if ("ChiTieu".equals(entry.getValue().getLoaiDM())) {
                        expenseCategoryIds.add(entry.getKey());
                    }
                }
                if (!expenseCategoryIds.isEmpty()) {
                    whereClause = " WHERE ID_DM IN (" + TextUtils.join(",", expenseCategoryIds) + ")";
                } else {
                    // Không có danh mục chi tiêu, không hiển thị gì
                    // Cập nhật adapter ngay và thoát
                    if (transactionAdapter != null) {
                        transactionAdapter.updateData(listGiaoDich); // listGiaoDich rỗng
                    }
                    return; // Thoát khỏi hàm để không thực hiện truy vấn tiếp
                }
            } else if ("income".equals(currentTransactionType)) {
                List<Integer> incomeCategoryIds = new ArrayList<>();
                for (Map.Entry<Integer, category> entry : mapDanhMuc.entrySet()) {
                    if ("ThuNhap".equals(entry.getValue().getLoaiDM())) {
                        incomeCategoryIds.add(entry.getKey());
                    }
                }
                if (!incomeCategoryIds.isEmpty()) {
                    whereClause = " WHERE ID_DM IN (" + TextUtils.join(",", incomeCategoryIds) + ")";
                } else {
                    // Không có danh mục thu nhập, không hiển thị gì
                    // Cập nhật adapter ngay và thoát
                    if (transactionAdapter != null) {
                        transactionAdapter.updateData(listGiaoDich); // listGiaoDich rỗng
                    }
                    return; // Thoát khỏi hàm để không thực hiện truy vấn tiếp
                }
            }

            cursorGiaoDich = db.rawQuery(query + whereClause + orderBy, null);
            while (cursorGiaoDich.moveToNext()) {
                int idGd = cursorGiaoDich.getInt(0);
                int idDm = cursorGiaoDich.getInt(1);
                long soTien = cursorGiaoDich.getLong(2);
                String thoiGian = cursorGiaoDich.getString(3);
                String ghiChu = cursorGiaoDich.getString(4);

                GIAODICH gd = new GIAODICH(idGd, idDm, soTien, thoiGian, ghiChu);
                listGiaoDich.add(gd);
            }

            if (transactionAdapter == null) {
                transactionAdapter = new TransactionAdapter(getContext(), listGiaoDich, mapDanhMuc);
                recyclerView.setAdapter(transactionAdapter);

                //Click vào item để hiện trang chỉnh sửa giao dịch
                recyclerView.setAdapter(transactionAdapter);
                transactionAdapter.setOnItemClickListener(new TransactionAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(GIAODICH giaoDich) {
                        openEditTransaction(giaoDich);
                    }
                });
            } else {
                transactionAdapter.updateData(listGiaoDich); // Gọi phương thức updateData mới
            }
        } finally {
            if (cursorCategory != null) {
                cursorCategory.close();
            }
            if (cursorGiaoDich != null) {
                cursorGiaoDich.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_TRANSACTION && resultCode == Activity.RESULT_OK) {
            loadTransactions(); // gọi hàm load lại dữ liệu vào ListView
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.transaction_fragment, container, false);

        btnAdd = view.findViewById(R.id.btnAdd); // Đảm bảo btnAdd là ID chính xác từ activity_main.xml
        tabExpense = view.findViewById(R.id.tabExpense);
        tabIncome = view.findViewById(R.id.tabIncome);
        btnChiTieu = view.findViewById(R.id.btnChiTieu);
        btnThuNhap = view.findViewById(R.id.btnThuNhap);
        filterDay = view.findViewById(R.id.filter_day);
        filterMonth = view.findViewById(R.id.filter_month);
        filterYear = view.findViewById(R.id.filter_year);
        filterAll = view.findViewById(R.id.filter_all);

        recyclerView = view.findViewById(R.id.rvTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Thiết lập LayoutManager
        loadTransactions(); // Tải giao dịch ban đầu (mặc định là "all")

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getContext(), AddTransactionActivity.class);
                startActivityForResult(intent, REQUEST_ADD_TRANSACTION);
            }
        });

        //Hiển thị giao dịch theo chi tiêu
        tabExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTransactionType = "expense"; // Đặt loại giao dịch là "chi tiêu"
                loadTransactions(); // Tải lại dữ liệu
                tabExpense.setTextColor(getResources().getColor(R.color.colorPrimary));
                tabIncome.setTextColor(getResources().getColor(R.color.textPrimary));
            }
        });

        //Hiển thị giao dịch theo thu nhập
        tabIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTransactionType = "income"; // Đặt loại giao dịch là "thu nhập"
                loadTransactions(); // Tải lại dữ liệu
                tabIncome.setTextColor(getResources().getColor(R.color.colorPrimary));
                tabExpense.setTextColor(getResources().getColor(R.color.textPrimary));
            }
        });

        btnChiTieu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi nút "Chi tiêu" được click
                // Ví dụ: hiển thị chi tiết chi tiêu hoặc làm nổi bật nút này
            }
        });


        btnThuNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi nút "Thu nhập" được click
                // Ví dụ: hiển thị chi tiết thu nhập hoặc làm nổi bật nút này
            }
        });

        //Lọc giao dịch theo ngày
        filterDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi tab "Ngày" được click
                // Ví dụ: thay đổi trạng thái UI, load dữ liệu theo ngày
            }
        });

        //Lọc giao dịch theo tháng
        filterMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi tab "Tháng" được click
                // Ví dụ: thay đổi trạng thái UI, load dữ liệu theo tháng
            }
        });

        //Lọc giao dịch theo năm
        filterYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi tab "Năm" được click
                // Ví dụ: thay đổi trạng thái UI, load dữ liệu theo năm
            }
        });

        //Hiển thị tất cả  giao dịch
        filterAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi tab "Tất cả" được click
                // Ví dụ: thay đổi trạng thái UI, load tất cả dữ liệu
            }
        });
//        recyclerView     = view.findViewById(R.id.rvTransactions);
        loadTransactions();
        return view;
    }
    public void openEditTransaction(GIAODICH giaoDich) {
        // Khi một item giao dịch được click
        Intent intent = new Intent(getContext(), AddTransactionActivity.class);
        intent.putExtra("isEditMode", true); // Báo hiệu là chế độ chỉnh sửa
        intent.putExtra("transactionId", giaoDich.getID_GD());
        intent.putExtra("categoryId", giaoDich.getID_DM());
        intent.putExtra("amount", giaoDich.getSoTien());
        intent.putExtra("time", giaoDich.getThoiGian());
        intent.putExtra("note", giaoDich.getGhiChu());

        // Lấy thông tin danh mục để truyền icon và tên danh mục
        category selectedCategory = mapDanhMuc.get(giaoDich.getID_DM());
        if (selectedCategory != null) {
            intent.putExtra("selectedCategoryName", selectedCategory.getTenDM());
            intent.putExtra("selectedCategoryIcon", selectedCategory.getHinhAnh());
        }

        startActivityForResult(intent, REQUEST_EDIT_TRANSACTION); // Sử dụng request code mới
    }
}
