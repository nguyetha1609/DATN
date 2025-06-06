package org.o7planning.project_04.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.project_04.R;
import org.threeten.bp.LocalDate;
import org.threeten.bp.YearMonth;

import java.util.ArrayList;
import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    public interface OnDayClickListener {
        void onDayClick(LocalDate date);
    }

    private List<LocalDate> days = new ArrayList<>();
    private YearMonth month;
    private OnDayClickListener listener;

    public DayAdapter(YearMonth month, OnDayClickListener listener) {
        this.month = month;
        this.listener = listener;
        generateDays();
    }

    public void updateMonth(YearMonth newMonth) {
        this.month = newMonth;
        generateDays();
        notifyDataSetChanged();
    }

    private void generateDays() {
        days.clear();
        YearMonth m = month;
        LocalDate firstOfMonth = m.atDay(1);
        int shift = firstOfMonth.getDayOfWeek().getValue() % 7;
        // Android tuần bắt đầu CN=0, ta dùng mod để CN thành 0
        for (int i = 0; i < shift; i++) days.add(null);
        int length = m.lengthOfMonth();
        for (int d = 1; d <= length; d++) {
            days.add(m.atDay(d));
        }
    }

    @NonNull @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        LocalDate date = days.get(position);
        if (date == null) {
            holder.tvDay.setText("");
            holder.tvDay.setBackground(null);
        } else {
            holder.tvDay.setText(String.valueOf(date.getDayOfMonth()));
            if (date.equals(LocalDate.now())) {
                holder.tvDay.setBackgroundResource(R.drawable.bg_today);
            } else {
                holder.tvDay.setBackground(null);
            }
            holder.itemView.setOnClickListener(v -> listener.onDayClick(date));
        }
    }

    @Override public int getItemCount() { return days.size(); }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        DayViewHolder(View v) {
            super(v);
            tvDay = v.findViewById(R.id.tvDay);
        }
    }
}
