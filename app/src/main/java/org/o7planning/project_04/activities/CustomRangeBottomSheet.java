package org.o7planning.project_04.activities;


import android.graphics.Color;
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
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.ViewContainer;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;
import org.o7planning.project_04.R;


public class CustomRangeBottomSheet extends BottomSheetDialogFragment {

    public interface OnDateRangeSelectedListener {
        void onDateRangeSelected(String startDate, String endDate, String displayText);
    }

    private OnDateRangeSelectedListener listener;
    private CalendarView calendarView;
    private TextView tvSelectedRange,tvMonthTitle;
    private CalendarDay startDate = null;
    private CalendarDay endDate = null;
    private ImageView btnPrevMonth, btnNextMonth;
    private YearMonth currentMonth;



    public void setOnDateRangeSelectedListener(OnDateRangeSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_date_range, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        tvSelectedRange = view.findViewById(R.id.tvSelectedRange);
        Button btnDone = view.findViewById(R.id.btnDone);
        btnPrevMonth= view.findViewById(R.id.btnPrevMonth);
        btnNextMonth = view.findViewById(R.id.btnNextMonth);
        tvMonthTitle=view.findViewById(R.id.tvMonthTitle);

        initCalendar();

        btnPrevMonth.setOnClickListener(v -> {
            currentMonth = currentMonth.minusMonths(1);
            calendarView.smoothScrollToMonth(currentMonth);
            updateMonthTitle(currentMonth);
        });
        btnNextMonth.setOnClickListener(v -> {
            currentMonth = currentMonth.plusMonths(1);
            calendarView.smoothScrollToMonth(currentMonth);
            updateMonthTitle(currentMonth);
        });

        btnDone.setOnClickListener(v -> {
            if (startDate != null && endDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String startStr = startDate.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String endStr = endDate.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String displayText = "Từ " + startStr + " đến " + endStr;
                listener.onDateRangeSelected(startStr, endStr, displayText);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Vui lòng chọn đủ 2 ngày", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private class DayViewContainerBinder implements MonthDayBinder<DayViewContainer> {
        @NonNull
        @Override
        public DayViewContainer create(@NonNull View view) {
            return new DayViewContainer(view);
        }

        @Override
        public void bind(@NonNull DayViewContainer container, @NonNull CalendarDay day) {
            TextView dayText = container.dayText;
            View dayContainer = container.dayContainer;

            dayText.setText(String.valueOf(day.getDate().getDayOfMonth()));
            LocalDate date = day.getDate();


            // Reset giao diện
            dayContainer.setBackgroundResource(0);
            dayText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

            LocalDate today = LocalDate.now();

            if (date.isAfter(today)) {
                dayText.setAlpha(0.3f);
                dayText.setTypeface(null, Typeface.NORMAL);
                dayContainer.setOnClickListener(null);  // Không cho chọn
            } else {
                dayText.setAlpha(1f);
                dayText.setTypeface(null, Typeface.BOLD);
                dayContainer.setOnClickListener(v -> onDateClicked(day));
            }

            // Highlight logic
            if (startDate != null && endDate != null) {
                if (!day.getDate().isBefore(startDate.getDate()) &&
                        !day.getDate().isAfter(endDate.getDate())) {
                    dayContainer.setBackgroundResource(R.drawable.range_middle);
                    dayText.setTextColor(Color.BLACK);
                }
            }

            if (startDate != null && day.getDate().equals(startDate.getDate())) {
                dayContainer.setBackgroundResource(R.drawable.range_start_end);
                dayText.setTextColor(Color.BLACK);
            } else if (endDate != null && day.getDate().equals(endDate.getDate())) {
                dayContainer.setBackgroundResource(R.drawable.range_start_end);
                dayText.setTextColor(Color.BLACK);
            }




        }
    }

    private void initCalendar() {
        currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(12);
        YearMonth endMonth = currentMonth.plusMonths(12);
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();

        calendarView.setup(startMonth, endMonth, firstDayOfWeek);
        calendarView.scrollToMonth(currentMonth);
        calendarView.setDayBinder(new DayViewContainerBinder());

        updateMonthTitle(currentMonth);

        calendarView.setMonthScrollListener(month -> {
            updateMonthTitle(month.getYearMonth());
            return null;
        });
    }


    private void onDateClicked(CalendarDay day) {

        if (startDate == null || (startDate != null && endDate != null)) {
            startDate = day;
            endDate = null;
        } else {
            if (day.getDate().isBefore(startDate.getDate())) {
                endDate = startDate;
                startDate = day;
            } else {
                endDate = day;
            }
        }

        updateSelectedRangeText();
        calendarView.notifyCalendarChanged(); // Cập nhật giao diện
    }

    private void updateSelectedRangeText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String text = "Từ: " + (startDate != null ? formatter.format(startDate.getDate()) : "");
        if (endDate != null) {
            text += " đến: " + formatter.format(endDate.getDate());
        }
        tvSelectedRange.setText(text);
    }
    private void updateMonthTitle(YearMonth month) {
        Locale locale = new Locale("vi");
        String monthStr = month.getMonth().getDisplayName(java.time.format.TextStyle.FULL, locale);
        String title = capitalize(monthStr) + " " + month.getYear();
        tvMonthTitle.setText(title);
    }
    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
    // Container cho mỗi ngày
    static class DayViewContainer extends ViewContainer {
        final View dayContainer;
        final TextView dayText;

        public DayViewContainer(@NonNull View view) {
            super(view);
            dayContainer = view.findViewById(R.id.dayContainer);
            dayText = view.findViewById(R.id.dayText);
        }
    }
}