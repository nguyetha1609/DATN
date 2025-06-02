package org.o7planning.project_04.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.project_04.R;

import java.util.List;

public class YearAdapter extends RecyclerView.Adapter<YearAdapter.YearViewHolder> {

    public interface OnYearClickListener {
        void onYearClick(int year);
    }

    private List<Integer> yearList;
    private OnYearClickListener listener;
    private List<Integer> years;
    private int selectedYear;

//    public YearAdapter(List<Integer> yearList, OnYearClickListener listener) {
//        this.yearList = yearList;
//        this.listener = listener;
//    }
public YearAdapter(List<Integer> yearList, int selectedYear, OnYearClickListener listener) {
    this.yearList = yearList;
    this.selectedYear = selectedYear;
    this.listener = listener;
}


    @NonNull
    @Override
    public YearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_year, parent, false);
        return new YearViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YearViewHolder holder, int position) {
        int year = yearList.get(position);
        holder.tvYear.setText(String.valueOf(year));
        holder.itemView.setOnClickListener(v -> {
            selectedYear = year;
            listener.onYearClick(year);
            notifyDataSetChanged();
        });

        if (year == selectedYear) {
            holder.tvYear.setTextColor(holder.itemView.getResources().getColor(R.color.purple_200));
        } else {
            holder.tvYear.setTextColor(holder.itemView.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return yearList.size();
    }

    public static class YearViewHolder extends RecyclerView.ViewHolder {
        TextView tvYear;

        public YearViewHolder(@NonNull View itemView) {
            super(itemView);
            tvYear = itemView.findViewById(R.id.tvYearItem);
        }
    }
}
