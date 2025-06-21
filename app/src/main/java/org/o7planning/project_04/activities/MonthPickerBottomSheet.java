package org.o7planning.project_04.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.o7planning.project_04.R;

import java.util.Calendar;

public class MonthPickerBottomSheet extends BottomSheetDialogFragment {

    private TextView tvYear;
    private GridLayout monthGrid;
    private int selectedMonth = -1;
    private int selectedYear;
    private OnMonthSelectedListener listener;

    public interface OnMonthSelectedListener {
        void onMonthSelected(String selectedMonthText);
    }

    public MonthPickerBottomSheet(OnMonthSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_select_month, container, false);
        tvYear = view.findViewById(R.id.tvYear);
        monthGrid = view.findViewById(R.id.monthGrid);
        ImageView btnPreviousYear = view.findViewById(R.id.btnPreviousYear);
        ImageView btnNextYear = view.findViewById(R.id.btnNextYear);
        Button btnDone = view.findViewById(R.id.btnDone);

        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        updateYearDisplay();

        populateMonths();

        btnPreviousYear.setOnClickListener(v -> {
            selectedYear--;
            updateYearDisplay();
        });

        btnNextYear.setOnClickListener(v -> {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (selectedYear < currentYear) {
                selectedYear++;
                updateYearDisplay();
            }
        });

        btnDone.setOnClickListener(v -> {
            if (selectedMonth != -1) {
                String monthText = String.format("Tháng %02d/%d", selectedMonth + 1, selectedYear);
                listener.onMonthSelected(monthText);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Vui lòng chọn tháng", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void updateYearDisplay() {
        tvYear.setText(String.valueOf(selectedYear));
    }

    @SuppressLint("SetTextI18n")
    private void populateMonths() {
        monthGrid.removeAllViews();

        String[] monthNames = {
                "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4",
                "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8",
                "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
        };

        for (int i = 0; i < 12; i++) {
            TextView monthView = new TextView(getContext());
            monthView.setText(monthNames[i]);
            monthView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            monthView.setPadding(16, 16, 16, 16);
            monthView.setBackgroundResource(R.drawable.bg_month_default);
            monthView.setTextColor(getResources().getColor(R.color.black));

            int finalI = i;
            monthView.setOnClickListener(v -> {
                selectedMonth = finalI;
                highlightSelectedMonth();
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            monthGrid.addView(monthView, params);
        }
    }

    private void highlightSelectedMonth() {
        for (int i = 0; i < monthGrid.getChildCount(); i++) {
            TextView monthView = (TextView) monthGrid.getChildAt(i);
            if (i == selectedMonth) {
                monthView.setBackgroundResource(R.drawable.bg_month_selected);
                monthView.setTextColor(getResources().getColor(R.color.white));
            } else {
                monthView.setBackgroundResource(R.drawable.bg_month_default);
                monthView.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }
}
