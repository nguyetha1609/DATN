package org.o7planning.project_04.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.o7planning.project_04.AddExpenseActivity;
import org.o7planning.project_04.MainActivity;
import org.o7planning.project_04.R;

public class Transaction_Fragment extends Fragment {
    private Button btnAdd, btnChiTieu, btnThuNhap;
    private TextView tabExpense, tabIncome, filterDay, filterMonth, filterYear, filterAll;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction_fragment, container, false);

        // các xử lý ở trang giao dịch

        btnAdd = view.findViewById(R.id.btnAdd); // Đảm bảo btnAdd là ID chính xác từ activity_main.xml
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddExpenseActivity.class);
                startActivity(intent);
            }
        });
        // Khởi tạo btnAdd và đặt OnClickListener cho nó


        // Khởi tạo các tab con Chi tiêu và Thu nhập và thêm sự kiện click
        tabExpense = view.findViewById(R.id.tabExpense);
        tabIncome = view.findViewById(R.id.tabIncome);

        tabExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi tab "Chi tiêu" được click
                // Ví dụ: thay đổi trạng thái UI, load dữ liệu chi tiêu
                // Ở đây bạn có thể thay đổi màu nền hoặc màu chữ để biểu thị tab đang được chọn
                // Giả sử có colorPrimary và textPrimary trong colors.xml
                tabExpense.setTextColor(getResources().getColor(R.color.colorPrimary));
                tabIncome.setTextColor(getResources().getColor(R.color.textPrimary));
                // Thêm logic để hiển thị danh sách chi tiêu
            }
        });
        tabIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi tab "Thu nhập" được click
                // Ví dụ: thay đổi trạng thái UI, load dữ liệu thu nhập
                tabIncome.setTextColor(getResources().getColor(R.color.colorPrimary));
                tabExpense.setTextColor(getResources().getColor(R.color.textPrimary));
                // Thêm logic để hiển thị danh sách thu nhập
            }
        });

        // Khởi tạo các Button "Chi tiêu" và "Thu nhập"
        btnChiTieu = view.findViewById(R.id.btnChiTieu);
        btnThuNhap = view.findViewById(R.id.btnThuNhap);

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

        // Khởi tạo các TextView trong phần "Tabs: Ngày, Tháng, Năm, Tất cả" và thêm sự kiện click
        filterDay = view.findViewById(R.id.filter_day);
        filterMonth = view.findViewById(R.id.filter_month);
        filterYear = view.findViewById(R.id.filter_year);
        filterAll = view.findViewById(R.id.filter_all);

        filterDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi tab "Ngày" được click
                // Ví dụ: thay đổi trạng thái UI, load dữ liệu theo ngày
            }
        });

        filterMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi tab "Tháng" được click
                // Ví dụ: thay đổi trạng thái UI, load dữ liệu theo tháng
            }
        });

        filterYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi tab "Năm" được click
                // Ví dụ: thay đổi trạng thái UI, load dữ liệu theo năm
            }
        });

        filterAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi tab "Tất cả" được click
                // Ví dụ: thay đổi trạng thái UI, load tất cả dữ liệu
            }
        });
        return  view;
    }
}
