package org.o7planning.project_04.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.project_04.R;
import org.o7planning.project_04.model.CategoryIcon;

import java.util.List;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconViewHolder> {

    public interface OnIconClickListener {
        void onIconClick(String iconName, int ResId);
    }

    private List<CategoryIcon> iconList;
    private OnIconClickListener listener;

    public IconAdapter(List<CategoryIcon> iconList, OnIconClickListener listener) {
        this.iconList = iconList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_icon, parent, false);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
        CategoryIcon icon = iconList.get(position);
        holder.imgIcon.setImageResource(icon.getResId());

        holder.imgIcon.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIconClick(icon.getName(), icon.getResId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return iconList.size();
    }

    static class IconViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;

        public IconViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_icon_item);
        }
    }
}
