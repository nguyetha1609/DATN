package org.o7planning.project_04.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.project_04.Adapter.TransactionAdapter;
import org.o7planning.project_04.R;
import org.o7planning.project_04.activities.AddTransactionActivity;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.databases.LimitDAO;
import org.o7planning.project_04.model.GIAODICH;
import org.o7planning.project_04.model.Limit;
import org.o7planning.project_04.model.category;
import org.threeten.bp.LocalDate; // Import LocalDate từ ThreeTenABP
import org.threeten.bp.format.DateTimeFormatter; // Import DateTimeFormatter từ ThreeTenABP

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionFragment extends Fragment implements HomeFragment.OnDateSelectedListener { // Triển khai giao diện
    private Button btnAdd, btnChiTieu, btnThuNhap;
    private TextView tabExpense, tabIncome, filterDay, filterMonth, filterYear, filterAll, tvLimit, tvNotice, tvSoDu;
    private static final int REQUEST_ADD_TRANSACTION = 1001;
    private static final int REQUEST_EDIT_TRANSACTION = 1002;
    private RecyclerView recyclerView;
    private List<GIAODICH> listGiaoDich;
    private static final int REQUEST_ADD_CATEGORY = 1003;
    private ActivityResultLauncher<Intent> transactionLauncher;


    private int userId;
    private Map<Integer, category> mapDanhMuc; // Map chứa danh mục


    private TransactionAdapter transactionAdapter;

    private String currentTransactionType = "all";
    private String currentFilterPeriod = "all";
    private LocalDate selectedFilterDate = LocalDate.now(); // Thêm biến để lưu ngày đã chọn

    @Override
    public void onDateSelected(LocalDate date) {
        selectedFilterDate = date;
        currentFilterPeriod = "day"; // Khi chọn một ngày cụ thể, lọc theo ngày
        loadTransactions();
        updateFilterTabColors(filterDay); // Cập nhật màu tab lọc ngày
    }

    @Override
    public void onResetToToday() {
        selectedFilterDate = LocalDate.now();
        currentFilterPeriod = "all"; // Reset về "all" để hiển thị tất cả giao dịch
        loadTransactions();
        updateFilterTabColors(filterAll); // Cập nhật màu tab lọc tất cả
    }

    private void loadDanhMuc() {
        mapDanhMuc = new HashMap<>();
        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursorCategory = null;

        try {
            cursorCategory = db.rawQuery(
                    "SELECT ID_DM, TenDM, HinhANh, LoaiDM, DMMacDinh FROM DANHMUC WHERE ID_TK = ? OR DMMacDinh = 1",
                    new String[]{String.valueOf(userId)}
            );
            while (cursorCategory.moveToNext()) {
                int idDm = cursorCategory.getInt(0);
                String tenDm = cursorCategory.getString(1);
                String hinhAnh = cursorCategory.getString(2);
                String loaiDm = cursorCategory.getString(3);
                int dmMacDinh = cursorCategory.getInt(4);

                category cat = new category(idDm, tenDm, loaiDm, hinhAnh, dmMacDinh);
                mapDanhMuc.put(idDm, cat);
            }
        } finally {
            if (cursorCategory != null) cursorCategory.close();
            db.close();
        }
    }


    private void loadTransactions() {
        loadDanhMuc();
        listGiaoDich = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursorGiaoDich = null;

        try {
            String query = "SELECT ID_GD, ID_DM, SoTien, ThoiGian, GhiChu FROM GIAODICH";
            String orderBy = " ORDER BY ThoiGian DESC";
            List<String> whereClauses = new ArrayList<>();
            List<String> selectionArgs = new ArrayList<>();

            // Lọc loại giao dịch
            if ("expense".equals(currentTransactionType)) {
                List<Integer> expenseCategoryIds = new ArrayList<>();
                for (Map.Entry<Integer, category> entry : mapDanhMuc.entrySet()) {
                    if ("ChiTieu".equals(entry.getValue().getLoaiDM())) {
                        expenseCategoryIds.add(entry.getKey());
                    }
                }
                if (!expenseCategoryIds.isEmpty()) {
                    whereClauses.add("ID_DM IN (" + TextUtils.join(",", expenseCategoryIds) + ")");
                } else {
                    if (transactionAdapter != null) {
                        transactionAdapter.updateData(listGiaoDich);
                    }
                    return;
                }
            } else if ("income".equals(currentTransactionType)) {
                List<Integer> incomeCategoryIds = new ArrayList<>();
                for (Map.Entry<Integer, category> entry : mapDanhMuc.entrySet()) {
                    if ("ThuNhap".equals(entry.getValue().getLoaiDM())) {
                        incomeCategoryIds.add(entry.getKey());
                    }
                }
                if (!incomeCategoryIds.isEmpty()) {
                    whereClauses.add("ID_DM IN (" + TextUtils.join(",", incomeCategoryIds) + ")");
                } else {
                    if (transactionAdapter != null) {
                        transactionAdapter.updateData(listGiaoDich);
                    }
                    return;
                }
            }

            // Lọc thời gian
            if (selectedFilterDate != null) {
                if ("day".equals(currentFilterPeriod)) {
                    String today = selectedFilterDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    whereClauses.add("strftime('%Y-%m-%d', ThoiGian) = ?");
                    selectionArgs.add(today);
                } else if ("month".equals(currentFilterPeriod)) {
                    String currentMonth = selectedFilterDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                    whereClauses.add("strftime('%Y-%m', ThoiGian) = ?");
                    selectionArgs.add(currentMonth);
                } else if ("year".equals(currentFilterPeriod)) {
                    String currentYear = selectedFilterDate.format(DateTimeFormatter.ofPattern("yyyy"));
                    whereClauses.add("strftime('%Y', ThoiGian) = ?");
                    selectionArgs.add(currentYear);
                }
            }

            whereClauses.add("ID_TK = ?");
            selectionArgs.add(String.valueOf(userId));

            String finalWhereClause = "";
            if (!whereClauses.isEmpty()) {
                finalWhereClause = " WHERE " + TextUtils.join(" AND ", whereClauses);
            }

            cursorGiaoDich = db.rawQuery(query + finalWhereClause + orderBy, selectionArgs.toArray(new String[0]));

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
                transactionAdapter.setOnItemClickListener(this::openEditTransaction);

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        GIAODICH giaoDichToDelete = listGiaoDich.get(position);
                        showDeleteConfirmationDialog(giaoDichToDelete, position);
                    }
                });
                itemTouchHelper.attachToRecyclerView(recyclerView);
            } else {
                transactionAdapter.setMapDanhMuc(mapDanhMuc);
                transactionAdapter.updateData(listGiaoDich);
            }

            updateBudgetUI();

        } finally {
            if (cursorGiaoDich != null) cursorGiaoDich.close();
            db.close();
        }
    }

    private void showDeleteConfirmationDialog(GIAODICH giaoDich, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa giao dịch này?")
                .setPositiveButton("OK", (dialog, which) -> deleteTransaction(giaoDich, position))
                .setNegativeButton("Hủy", (dialog, which) -> {
                    transactionAdapter.notifyItemChanged(position);
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteTransaction(GIAODICH giaoDich, int position) {
        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete("GIAODICH", "ID_GD = ?", new String[]{String.valueOf(giaoDich.getID_GD())});
        db.close();

        if (rowsAffected > 0) {
            loadTransactions();
        } else {
            transactionAdapter.notifyItemChanged(position);
            updateBudgetUI();
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if ((requestCode == REQUEST_ADD_TRANSACTION || requestCode == REQUEST_EDIT_TRANSACTION || requestCode == REQUEST_ADD_CATEGORY)
//                && resultCode == Activity.RESULT_OK) {
//
//            // Luôn gọi loadTransactions(), vì nó tự load lại cả mapDanhMuc + giao dịch
//            loadTransactions();
//            updateBudgetUI();
//        }
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.transaction_fragment, container, false);

        btnAdd = view.findViewById(R.id.btnAdd);
        tabExpense = view.findViewById(R.id.tabExpense);
        tabIncome = view.findViewById(R.id.tabIncome);
        btnChiTieu = view.findViewById(R.id.btnChiTieu);
        btnThuNhap = view.findViewById(R.id.btnThuNhap);
        filterDay = view.findViewById(R.id.filter_day);
        filterMonth = view.findViewById(R.id.filter_month);
        filterYear = view.findViewById(R.id.filter_year);
        filterAll = view.findViewById(R.id.filter_all);
        tvLimit = view.findViewById(R.id.tvLimit);
        tvNotice = view.findViewById(R.id.tvNotice);
        tvSoDu = view.findViewById(R.id.tvSoDu);

        recyclerView = view.findViewById(R.id.rvTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_PREF", getContext().MODE_PRIVATE);
         userId = preferences.getInt("ID_TK", -1);
        loadDanhMuc();
        loadTransactions();


        // Thêm HomeFragment vào fragment_container
        if (getChildFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }


        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddTransactionActivity.class);
            transactionLauncher.launch(intent);
        });

        tabExpense.setOnClickListener(v -> {
            currentTransactionType = "expense";
            loadTransactions();
            tabExpense.setTextColor(getResources().getColor(R.color.colorPrimary));
            tabIncome.setTextColor(getResources().getColor(R.color.textPrimary));
        });

        tabIncome.setOnClickListener(v -> {
            currentTransactionType = "income";
            loadTransactions();
            tabIncome.setTextColor(getResources().getColor(R.color.colorPrimary));
            tabExpense.setTextColor(getResources().getColor(R.color.textPrimary));
        });

        btnChiTieu.setOnClickListener(v -> {
            //Không xử lý, chỉ để hiển thị
        });

        btnThuNhap.setOnClickListener(v -> {
            //Không xử lý, chỉ để hiển thị
        });

        filterDay.setOnClickListener(v -> {
            currentFilterPeriod = "day";
            selectedFilterDate = LocalDate.now(); // Đặt lại ngày hiện tại khi chọn lọc theo ngày
            loadTransactions();
            updateFilterTabColors(filterDay);
        });

        filterMonth.setOnClickListener(v -> {
            currentFilterPeriod = "month";
            selectedFilterDate = LocalDate.now(); // Đặt lại ngày hiện tại khi chọn lọc theo tháng
            loadTransactions();
            updateFilterTabColors(filterMonth);
        });

        filterYear.setOnClickListener(v -> {
            currentFilterPeriod = "year";
            selectedFilterDate = LocalDate.now(); // Đặt lại ngày hiện tại khi chọn lọc theo năm
            loadTransactions();
            updateFilterTabColors(filterYear);
        });

        filterAll.setOnClickListener(v -> {
            currentFilterPeriod = "all";
            selectedFilterDate = null; // Đặt selectedFilterDate về null khi chọn lọc tất cả
            loadTransactions();
            updateFilterTabColors(filterAll);
        });

        transactionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        loadDanhMuc();
                        loadTransactions();
                        updateBudgetUI();
                    }
                }
        );

        return view;
    }

    private void updateFilterTabColors(TextView selectedTab) {
        filterDay.setTextColor(getResources().getColor(R.color.textPrimary));
        filterMonth.setTextColor(getResources().getColor(R.color.textPrimary));
        filterYear.setTextColor(getResources().getColor(R.color.textPrimary));
        filterAll.setTextColor(getResources().getColor(R.color.textPrimary));
        selectedTab.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    public void openEditTransaction(GIAODICH giaoDich) {
        Intent intent = new Intent(getContext(), AddTransactionActivity.class);
        intent.putExtra("isEditMode", true);
        intent.putExtra("transactionId", giaoDich.getID_GD());
        intent.putExtra("categoryId", giaoDich.getID_DM());
        intent.putExtra("amount", giaoDich.getSoTien());
        intent.putExtra("time", giaoDich.getThoiGian());
        intent.putExtra("note", giaoDich.getGhiChu());


        intent.putExtra("ID_TK", userId);

        category selectedCategory = mapDanhMuc.get(giaoDich.getID_DM());
        if (selectedCategory != null) {
            intent.putExtra("selectedCategoryName", selectedCategory.getTenDM());
            intent.putExtra("selectedCategoryIcon", selectedCategory.getHinhAnh());
        }

        transactionLauncher.launch(intent);
    }

    private void updateBudgetUI() {
        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE);
        int userId = preferences.getInt("ID_TK", -1);

        LimitDAO limitDAO = new LimitDAO(getContext());
        List<Limit> limits = limitDAO.getAllLimits(userId);

        if (limits.isEmpty()) return;

        Limit limit = limits.get(0);

        long totalLimit = limit.getSoTien();
        long spentAmount = limitDAO.getTotalSpentInLimit(limit.getID_HM(), userId, limit.getNgayGD(), limit.getNgayKetThuc());
        long remaining = totalLimit - spentAmount;

        tvLimit.setText("Hạn mức: " + formatCurrency(totalLimit));

        if (remaining < 0) {
            tvNotice.setText("Vượt quá chi tiêu: " + formatCurrency(-remaining));
            tvNotice.setTextColor(Color.RED);
        } else {
            tvNotice.setText("");
        }

        tvSoDu.setText("Số dư: " + formatCurrency(Math.max(remaining, 0)));

        long tongChi = 0, tongThu = 0;
        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID_DM, SoTien FROM GIAODICH WHERE ID_TK = ?", new String[]{String.valueOf(userId)});
        while (cursor.moveToNext()) {
            int idDM = cursor.getInt(0);
            long soTien = cursor.getLong(1);
            category cat = mapDanhMuc.get(idDM);
            if (cat == null) continue;

            if ("ChiTieu".equals(cat.getLoaiDM())) {
                tongChi += soTien;
            } else if ("ThuNhap".equals(cat.getLoaiDM())) {
                tongThu += soTien;
            }
        }
        cursor.close();
        db.close();

        btnChiTieu.setText("Chi tiêu:\n " + formatCurrency(tongChi));
        btnThuNhap.setText("Thu nhập:\n " + formatCurrency(tongThu));
    }

    private String formatCurrency(long value) {
        return String.format(Locale.getDefault(), "%,d", value).replace(',', '.');
    }
    @Override
    public void onResume() {
        super.onResume();
        loadDanhMuc();
        loadTransactions();  // Luôn reload lại khi Fragment hiển thị lại
    }
}