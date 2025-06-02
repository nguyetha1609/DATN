package org.o7planning.project_04.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.DayPosition;

import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.ViewContainer;

import org.o7planning.project_04.R;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Locale;

public class WeekPickerBottomSheet extends BottomSheetDialogFragment {

    public interface OnWeekSelectedListener {
        void onWeekSelected(String startOfWeek, String endOfWeek, String displayText);
    }

    private OnWeekSelectedListener listener;
    private CalendarView calendarView;
    private TextView tvSelectedWeek;
    private LocalDate selectedDate;
    private LocalDate startOfWeek, endOfWeek;
    private TextView tvMonthTitle;
    private ImageView btnPrevMonth, btnNextMonth;
    private YearMonth currentMonth;

    public void setOnWeekSelectedListener(OnWeekSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_week, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        tvSelectedWeek = view.findViewById(R.id.tvSelectedRange);
        Button btnDone = view.findViewById(R.id.btnDone);

        tvMonthTitle = view.findViewById(R.id.tvMonthTitle);
        btnPrevMonth = view.findViewById(R.id.btnPrevMonth);
        btnNextMonth = view.findViewById(R.id.btnNextMonth);

        currentMonth = YearMonth.now();
        btnPrevMonth.setOnClickListener(v -> {
            currentMonth = currentMonth.minusMonths(1);
            calendarView.scrollToMonth(currentMonth);
        });

        btnNextMonth.setOnClickListener(v -> {
            currentMonth = currentMonth.plusMonths(1);
            calendarView.scrollToMonth(currentMonth);
        });

        initCalendar();

        btnDone.setOnClickListener(v -> {
            if (startOfWeek != null && endOfWeek != null) {
                String startDateStr = formatDate(startOfWeek);
                String endDateStr = formatDate(endOfWeek);
                String displayText = "Tuần: " + startDateStr + " - " + endDateStr;
                if (listener != null) {
                    listener.onWeekSelected(startDateStr, endDateStr, displayText);
                }
                dismiss();
            } else {
                Toast.makeText(getContext(), "Vui lòng chọn một ngày trong tuần", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void initCalendar() {
        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(12);
        YearMonth endMonth = currentMonth.plusMonths(12);
        DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
        calendarView.setup(startMonth, endMonth, firstDayOfWeek);
        calendarView.scrollToMonth(currentMonth);
        calendarView.setDayBinder(new DayBinder());

        updateMonthTitle(currentMonth);

        calendarView.setMonthScrollListener(month -> {
            updateMonthTitle(month.getYearMonth());
            return null;
        });
    }
    private void updateMonthTitle(YearMonth yearMonth) {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("vi"));
        tvMonthTitle.setText(yearMonth.format(formatter));
    }

    private void onDateClicked(CalendarDay day) {
        if (day.getPosition() != DayPosition.MonthDate) return;

        selectedDate = day.getDate();
        DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;

        // Trừ lùi ngày để về đúng đầu tuần
        int diff = selectedDate.getDayOfWeek().getValue() - firstDayOfWeek.getValue();
        if (diff < 0) diff += 7;

        startOfWeek = selectedDate.minusDays(diff);
        endOfWeek = startOfWeek.plusDays(6);

        tvSelectedWeek.setText("Tuần: " + formatDate(startOfWeek) + " - " + formatDate(endOfWeek));
        calendarView.notifyCalendarChanged();
    }

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
        return date.format(formatter);    }

    private class DayBinder implements MonthDayBinder<DayContainer> {
        @NonNull
        @Override
        public DayContainer create(@NonNull View view) {
            return new DayContainer(view);
        }

        @Override
        public void bind(@NonNull DayContainer container, @NonNull CalendarDay day) {
            TextView dayText = container.dayText;
            View dayContainer = container.dayContainer;

            LocalDate date = day.getDate();
            dayText.setText(String.valueOf(date.getDayOfMonth()));

            // Chỉ xử lý các ngày trong tháng hiện tại
            if (day.getPosition() != DayPosition.MonthDate) {
                dayText.setAlpha(0.2f); // Ngày không thuộc tháng hiện tại -> mờ
                dayText.setTypeface(null, Typeface.NORMAL);
                dayContainer.setOnClickListener(null);
                dayContainer.setBackgroundResource(0);
                return;
            }

            LocalDate today = LocalDate.now();

            if (date.isAfter(today)) {
                // Ngày tương lai -> làm mờ + disable click
                dayText.setAlpha(0.3f);
                dayText.setTypeface(null, Typeface.NORMAL);
                dayContainer.setOnClickListener(null);
            } else {
                // Ngày hôm nay trở về trước
                dayText.setAlpha(1f);
                dayText.setTypeface(null, Typeface.BOLD);
                dayContainer.setOnClickListener(v -> onDateClicked(day));
            }

            // Highlight tuần
            if (startOfWeek != null && endOfWeek != null) {
                if (!day.getDate().isBefore(startOfWeek) && !day.getDate().isAfter(endOfWeek)) {
                    container.dayContainer.setBackgroundResource(R.drawable.bg_range_day);
                    container.dayText.setTextColor(getResources().getColor(android.R.color.white));
                } else {
                    container.dayContainer.setBackgroundResource(0);
                    container.dayText.setTextColor(getResources().getColor(android.R.color.black));
                }
            } else {
                container.dayContainer.setBackgroundResource(0);
                container.dayText.setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }

    static class DayContainer extends ViewContainer {
        final View itemView;
        final View dayContainer;
        final TextView dayText;

        public DayContainer(@NonNull View view) {
            super(view);
            itemView = view;
            dayContainer = view.findViewById(R.id.dayContainer);
            dayText = view.findViewById(R.id.dayText);
        }
    }
}
