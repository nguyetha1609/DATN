package org.o7planning.project_04.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.o7planning.project_04.Adapter.TransactionAdapter;
import org.o7planning.project_04.R;
import org.o7planning.project_04.activities.AddTransactionActivity;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.databases.LimitDAO;
import org.o7planning.project_04.model.GIAODICH;
import org.o7planning.project_04.model.Limit;
import org.o7planning.project_04.model.category;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionFragment extends Fragment {
    private Button btnAdd, btnChiTieu, btnThuNhap;
    private TextView tabExpense, tabIncome, filterDay, filterMonth, filterYear, filterAll,tvLimit, tvNotice, tvSoDu;
    private static final int REQUEST_ADD_TRANSACTION = 1001;
    private static final int REQUEST_EDIT_TRANSACTION = 1002;
    private RecyclerView recyclerView;
    private List<GIAODICH> listGiaoDich;
    private Map<Integer, category> mapDanhMuc; // Map chứa danh mục
    private TransactionAdapter transactionAdapter;
    private String currentTransactionType = "all"; // Biến để lưu loại giao dịch hiện tại: "all", "expense", "income"
    private String currentFilterPeriod = "all"; // Biến để lưu loại lọc thời gian: "all", "day", "month", "year"


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
            String orderBy = " ORDER BY ThoiGian DESC"; // Sắp xếp theo thời gian mới nhất
            List<String> whereClauses = new ArrayList<>();
            List<String> selectionArgs = new ArrayList<>();

            // Lọc theo loại giao dịch (chi tiêu/thu nhập)
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

            // Lọc theo thời gian (ngày/tháng/năm/tất cả)
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            if ("day".equals(currentFilterPeriod)) {
                String today = sdfDate.format(calendar.getTime());
                whereClauses.add("strftime('%Y-%m-%d', ThoiGian) = ?");
                selectionArgs.add(today);
            } else if ("month".equals(currentFilterPeriod)) {
                String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.getTime());
                whereClauses.add("strftime('%Y-%m', ThoiGian) = ?");
                selectionArgs.add(currentMonth);
            } else if ("year".equals(currentFilterPeriod)) {
                String currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.getTime());
                whereClauses.add("strftime('%Y', ThoiGian) = ?");
                selectionArgs.add(currentYear);
            }

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

                // Thêm ItemTouchHelper cho chức năng vuốt để xóa
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
                transactionAdapter.updateData(listGiaoDich);
            }
            // Sau khi cập nhật danh sách giao dịch, cập nhật hạn mức
            updateBudgetUI();
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

    private void showDeleteConfirmationDialog(GIAODICH giaoDich, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa giao dịch này?")
                .setPositiveButton("OK", (dialog, which) -> deleteTransaction(giaoDich, position))
                .setNegativeButton("Hủy", (dialog, which) -> {
                    // Nếu hủy, cập nhật lại adapter để item trở về vị trí cũ
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
//            listGiaoDich.remove(position);
//            transactionAdapter.notifyItemRemoved(position);
            loadTransactions();
        } else {
            // If deletion fails, revert the swipe
            transactionAdapter.notifyItemChanged(position);
            // Toast.makeText(getContext(), "Lỗi khi xóa giao dịch", Toast.LENGTH_SHORT).show();
            updateBudgetUI();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_ADD_TRANSACTION || requestCode == REQUEST_EDIT_TRANSACTION) && resultCode == Activity.RESULT_OK) {
            loadTransactions(); // gọi hàm load lại dữ liệu vào ListView
            updateBudgetUI();
        }
    }

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
        loadTransactions();

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddTransactionActivity.class);
            startActivityForResult(intent, REQUEST_ADD_TRANSACTION);
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
            // Xử lý khi nút "Chi tiêu" được click
        });

        btnThuNhap.setOnClickListener(v -> {
            // Xử lý khi nút "Thu nhập" được click
        });

        filterDay.setOnClickListener(v -> {
            currentFilterPeriod = "day";
            loadTransactions();
            updateFilterTabColors(filterDay);
        });

        filterMonth.setOnClickListener(v -> {
            currentFilterPeriod = "month";
            loadTransactions();
            updateFilterTabColors(filterMonth);
        });

        filterYear.setOnClickListener(v -> {
            currentFilterPeriod = "year";
            loadTransactions();
            updateFilterTabColors(filterYear);
        });

        filterAll.setOnClickListener(v -> {
            currentFilterPeriod = "all";
            loadTransactions();
            updateFilterTabColors(filterAll);
        });

        return view;
    }

    private void updateFilterTabColors(TextView selectedTab) {
        filterDay.setTextColor(getResources().getColor(R.color.textPrimary));
        filterMonth.setTextColor(getResources().getColor(R.color.textPrimary));
        filterYear.setTextColor(getResources().getColor(R.color.textPrimary));
        filterAll.setTextColor(getResources().getColor(R.color.textPrimary));
        selectedTab.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    //Phương thức chỉnh sửa giao dịch
    public void openEditTransaction(GIAODICH giaoDich) {
        Intent intent = new Intent(getContext(), AddTransactionActivity.class);
        intent.putExtra("isEditMode", true);
        intent.putExtra("transactionId", giaoDich.getID_GD());
        intent.putExtra("categoryId", giaoDich.getID_DM());
        intent.putExtra("amount", giaoDich.getSoTien());
        intent.putExtra("time", giaoDich.getThoiGian());
        intent.putExtra("note", giaoDich.getGhiChu());

        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_PREF", getContext().MODE_PRIVATE);
        int userId = preferences.getInt("ID_TK", -1);
        intent.putExtra("ID_TK", userId);

        category selectedCategory = mapDanhMuc.get(giaoDich.getID_DM());
        if (selectedCategory != null) {
            intent.putExtra("selectedCategoryName", selectedCategory.getTenDM());
            intent.putExtra("selectedCategoryIcon", selectedCategory.getHinhAnh());
        }

        startActivityForResult(intent, REQUEST_EDIT_TRANSACTION);
    }

    private void updateBudgetUI() {
        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE);
        int userId = preferences.getInt("ID_TK", -1);

        LimitDAO limitDAO = new LimitDAO(getContext());
        List<Limit> limits = limitDAO.getAllLimits(userId);

        if (limits.isEmpty()) return; // Không có hạn mức nào

        // Ví dụ lấy hạn mức đầu tiên (hoặc giới hạn logic theo thời gian bạn muốn)
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

        // Cập nhật tổng chi/thu nếu cần:
        long tongChi = 0, tongThu = 0;
        for (GIAODICH gd : listGiaoDich) {
            category cat = mapDanhMuc.get(gd.getID_DM());
            if (cat == null) continue;
            if ("ChiTieu".equals(cat.getLoaiDM())) {
                tongChi += gd.getSoTien();
            } else if ("ThuNhap".equals(cat.getLoaiDM())) {
                tongThu += gd.getSoTien();
            }
        }

        btnChiTieu.setText("Chi tiêu:\n " + formatCurrency(tongChi));
        btnThuNhap.setText("Thu nhập:\n " + formatCurrency(tongThu));
    }

    private String formatCurrency(long value) {
        return String.format(Locale.getDefault(), "%,d", value).replace(',', '.');
    }

}