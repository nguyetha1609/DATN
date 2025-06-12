package org.o7planning.project_04.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.project_04.R;
import org.o7planning.project_04.model.CategoryStat;

import java.util.List;
import java.util.Locale;

public class CategoryDetailAdapter extends RecyclerView.Adapter<CategoryDetailAdapter.ViewHolder> {

    public List<CategoryStat> categoryList;

    public CategoryDetailAdapter(List<CategoryStat> categoryList) {
        this.categoryList = categoryList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCateName, tvAmount;
        View colorIndicator;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCateName = itemView.findViewById(R.id.tvCategoryName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
        }
    }

    @Override
    public CategoryDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryDetailAdapter.ViewHolder holder, int position) {
        CategoryStat item = categoryList.get(position);
        holder.tvCateName.setText(item.getCateName());
        holder.tvAmount.setText(String.format(Locale.getDefault(), "%,.0f", item.getAmount()));

        holder.colorIndicator.setBackgroundColor(item.getColor());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
