package org.o7planning.project_04.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton; // Import ImageButton
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.o7planning.project_04.R;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

public class HomeFragment extends Fragment {

    private TextView tvDateTitle;
    private Button btnReset;
    private ImageButton btnPrev, btnNext; // Khai báo ImageButton
    private LocalDate selectedDate;

    // Thêm giao diện để giao tiếp với Fragment cha (TransactionFragment)
    public interface OnDateSelectedListener {
        void onDateSelected(LocalDate selectedDate);
        void onResetToToday();
    }

    private OnDateSelectedListener dateSelectedListener;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle b) {
        tvDateTitle = view.findViewById(R.id.tvDateTitle);
        btnReset = view.findViewById(R.id.btnReset);
        btnPrev = view.findViewById(R.id.btnPrev); // Khởi tạo btnPrev
        btnNext = view.findViewById(R.id.btnNext); // Khởi tạo btnNext

        // Khởi tạo trạng thái ban đầu
        updateDateDisplay(null); // Gọi phương thức để thiết lập trạng thái ban đầu

        tvDateTitle.setOnClickListener(v -> openCalendar());

        btnPrev.setOnClickListener(v -> {
            // Nếu selectedDate đang là null (tức là "Hôm nay"), lấy LocalDate.now() để tính toán
            LocalDate dateToProcess = (selectedDate != null) ? selectedDate : LocalDate.now();
            updateDateDisplay(dateToProcess.minusDays(1));
        });

        btnNext.setOnClickListener(v -> {
            // Nếu selectedDate đang là null (tức là "Hôm nay"), lấy LocalDate.now() để tính toán
            LocalDate dateToProcess = (selectedDate != null) ? selectedDate : LocalDate.now();
            updateDateDisplay(dateToProcess.plusDays(1));
        });

        btnNext.setOnClickListener(v -> {
            if (selectedDate != null) {
                updateDateDisplay(selectedDate.plusDays(1));
            }
        });
    }

    private void openCalendar() {
        LocalDate init = (selectedDate != null ? selectedDate : LocalDate.now());
        CalendarBottomSheetDialogFragment sheet =
                CalendarBottomSheetDialogFragment.newInstance(init, date -> {
                    // khi chọn ngày mới
                    updateDateDisplay(date); // Gọi phương thức để cập nhật
                });
        sheet.show(getChildFragmentManager(), "CALENDAR_SHEET");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Kiểm tra nếu Fragment cha là TransactionFragment hoặc Activity chứa TransactionFragment
        Fragment parent = getParentFragment();
        if (parent instanceof OnDateSelectedListener) {
            dateSelectedListener = (OnDateSelectedListener) parent;
        } else if (context instanceof OnDateSelectedListener) { // Nếu HomeFragment được nhúng trực tiếp vào Activity
            dateSelectedListener = (OnDateSelectedListener) context;
        }
    }

    // Phương thức trợ giúp để cập nhật hiển thị ngày và trạng thái nút reset
    private void updateDateDisplay(@Nullable LocalDate date) {
        selectedDate = date;
        if (selectedDate == null || selectedDate.isEqual(LocalDate.now())) {
            tvDateTitle.setText(R.string.today);
            btnReset.setVisibility(View.GONE);
            if (dateSelectedListener != null) dateSelectedListener.onResetToToday();
        } else {
            tvDateTitle.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            btnReset.setVisibility(View.VISIBLE);
            if (dateSelectedListener != null) dateSelectedListener.onDateSelected(selectedDate);
        }
    }
}