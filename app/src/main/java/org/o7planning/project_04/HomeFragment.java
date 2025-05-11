package org.o7planning.project_04;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.threeten.bp.LocalDate;

public class HomeFragment extends Fragment {

    private TextView tvDateTitle;
    private Button btnReset;
    private LocalDate selectedDate;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle b) {
        tvDateTitle = view.findViewById(R.id.tvDateTitle);
        btnReset   = view.findViewById(R.id.btnReset);

        // 1. Ban đầu: chỉ “Hôm nay”, nút reset ẩn
        selectedDate = null;
        tvDateTitle.setText(R.string.today);
        btnReset.setVisibility(View.GONE);

        tvDateTitle.setOnClickListener(v -> openCalendar());

        btnReset.setOnClickListener(v -> {
            // reset về Today
            selectedDate = null;
            tvDateTitle.setText(R.string.today);
            btnReset.setVisibility(View.GONE);
        });
    }

    private void openCalendar() {
        LocalDate init = (selectedDate != null ? selectedDate : LocalDate.now());
        CalendarBottomSheetDialogFragment sheet =
                CalendarBottomSheetDialogFragment.newInstance(init, date -> {
                    // khi chọn ngày mới
                    selectedDate = date;
                    String txt = date.getDayOfMonth() + "/"
                            + date.getMonthValue() + "/"
                            + date.getYear();
                    tvDateTitle.setText(txt);
                    // hiện nút Quay Lại
                    btnReset.setVisibility(View.VISIBLE);
                });
        sheet.show(getChildFragmentManager(), "CALENDAR_SHEET");
    }
}
