package org.o7planning.project_04.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.project_04.R;
import org.o7planning.project_04.model.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CategoryCheckboxAdapter extends RecyclerView.Adapter<CategoryCheckboxAdapter.ViewHolder> {

    private List<category> categoryList;
    private Set<Integer> selectedId;

    public CategoryCheckboxAdapter(List<category> categoryList, Set<Integer> selectedId) {
        this.categoryList = categoryList;
        this.selectedId = selectedId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_checkbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        category cate = categoryList.get(position);
        holder.tvName.setText(cate.getTenDM());
        holder.imgIcon.setImageResource(
                holder.itemView.getContext().getResources().getIdentifier(
                        cate.getHinhAnh(), "drawable", holder.itemView.getContext().getPackageName()
                )
        );

        // 1. Bỏ listener cũ để tránh callback khi setChecked
        holder.checkBox.setOnCheckedChangeListener(null);

        // 2. Set trạng thái checkbox theo dữ liệu selectedId
        holder.checkBox.setChecked(selectedId.contains(cate.getID()));

        // 3. Gán lại listener mới cho checkbox
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedId.add(cate.getID());
            } else {
                selectedId.remove(cate.getID());
            }
        });

        // 4. Cho phép click itemView toggle checkbox
        holder.itemView.setOnClickListener(v -> {
            boolean newChecked = !holder.checkBox.isChecked();
            holder.checkBox.setChecked(newChecked);
        });
    }



    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void selectAll() {
        for (category cat : categoryList) {
            selectedId.add(cat.getID());
        }
        notifyDataSetChanged();
    }

    public void deselectAll() {
        selectedId.clear();
        notifyDataSetChanged();
    }
    public List<Integer> getSelectedDMId(){
        return new ArrayList<>(selectedId);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvName;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_category);
            tvName = itemView.findViewById(R.id.tv_category_name);
            checkBox = itemView.findViewById(R.id.checkbox_category);
        }
    }
}
