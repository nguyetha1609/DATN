package org.o7planning.project_04.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.o7planning.project_04.Adapter.DayAdapter;
import org.o7planning.project_04.R;
import org.threeten.bp.LocalDate;
import org.threeten.bp.YearMonth;

public class CalendarBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public interface OnDateSelectedListener { void onDateSelected(LocalDate date); }

    private static final String ARG_YEAR = "arg_year";
    private static final String ARG_MONTH = "arg_month";
    private OnDateSelectedListener listener;
    private YearMonth currentMonth;

    public static CalendarBottomSheetDialogFragment newInstance(
            LocalDate initialDate,
            OnDateSelectedListener listener) {

        CalendarBottomSheetDialogFragment f = new CalendarBottomSheetDialogFragment();
        f.listener = listener;
        Bundle args = new Bundle();
        args.putInt(ARG_YEAR, initialDate.getYear());
        args.putInt(ARG_MONTH, initialDate.getMonthValue());
        f.setArguments(args);
        return f;
    }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        Bundle args = getArguments();
        int y = args.getInt(ARG_YEAR);
        int m = args.getInt(ARG_MONTH);
        currentMonth = YearMonth.of(y, m);
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup c, @Nullable Bundle b) {
        return inflater.inflate(R.layout.fragment_calendar, c, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        TextView tvMonthYear = v.findViewById(R.id.tvMonthYear);
        ImageButton btnPrev = v.findViewById(R.id.btnPrev);
        ImageButton btnNext = v.findViewById(R.id.btnNext);
        RecyclerView rv = v.findViewById(R.id.rvCalendar);

        rv.setLayoutManager(new GridLayoutManager(getContext(), 7));
        // Wrap the date selection to also dismiss the fragment
        DayAdapter adapter = new DayAdapter(currentMonth, date -> {
            // Notify listener
            listener.onDateSelected(date);
            // Close the calendar bottom sheet
            dismiss();
        });
        rv.setAdapter(adapter);

        // hiển thị header month/year
        tvMonthYear.setText(currentMonth.getMonthValue() + "/" + currentMonth.getYear());

        btnPrev.setOnClickListener(x -> {
            currentMonth = currentMonth.minusMonths(1);
            adapter.updateMonth(currentMonth);
            tvMonthYear.setText(currentMonth.getMonthValue() + "/" + currentMonth.getYear());
        });
        btnNext.setOnClickListener(x -> {
            currentMonth = currentMonth.plusMonths(1);
            adapter.updateMonth(currentMonth);
            tvMonthYear.setText(currentMonth.getMonthValue() + "/" + currentMonth.getYear());
        });
    }
}
