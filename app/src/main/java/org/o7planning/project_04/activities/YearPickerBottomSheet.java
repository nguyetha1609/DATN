package org.o7planning.project_04.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import org.o7planning.project_04.Adapter.YearAdapter;

import org.o7planning.project_04.R;

import java.util.ArrayList;
import java.util.List;

public class YearPickerBottomSheet extends BottomSheetDialogFragment {

    public interface OnYearSelectedListener {
        void onYearSelected(String yearText);
    }

    private OnYearSelectedListener listener;

    public YearPickerBottomSheet(OnYearSelectedListener listener) {
        this.listener = listener;
    }

    private RecyclerView rvYearList;
    private TextView tvSelectedYear;
    private Button btnDoneYear;

    private int selectedYear = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_select_year, container, false);

        rvYearList = view.findViewById(R.id.rvYearList);
        tvSelectedYear = view.findViewById(R.id.tvSelectedYear);
        btnDoneYear = view.findViewById(R.id.btnDoneYear);

        setupYearList();


        btnDoneYear.setOnClickListener(v -> {
            if (selectedYear != -1 && listener != null) {
                listener.onYearSelected("Năm " + selectedYear);
            }
            dismiss();
        });

        return view;
    }

    private void setupYearList() {
        List<Integer> years = new ArrayList<>();
        for (int i = 2020; i <= 2025; i++) {
            years.add(i);
        }
        selectedYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        YearAdapter adapter = new YearAdapter(years, selectedYear, year -> {
            YearPickerBottomSheet.this.selectedYear = year;
            tvSelectedYear.setText("Đã chọn: " + year);
        });

        rvYearList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvYearList.setAdapter(adapter);

        // Hiển thị năm đã chọn mặc định
        tvSelectedYear.setText("Đã chọn: " + selectedYear);

        // Scroll đến năm hiện tại
        int position = years.indexOf(selectedYear);
        if (position != -1) {
            rvYearList.scrollToPosition(position);
        }
    }
}
