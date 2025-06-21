package org.o7planning.project_04.Adapter;

import org.o7planning.project_04.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.project_04.model.GIAODICH;
import org.o7planning.project_04.model.SpendingGroup;

import java.util.List;

public class SpendingGroupAdapter extends RecyclerView.Adapter<SpendingGroupAdapter.ViewHolder> {
    private List<SpendingGroup> spendingGroups;

    public SpendingGroupAdapter(List<SpendingGroup> spendingGroups){
        this.spendingGroups =spendingGroups;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgCategoryIcon,imgToggleArrow;
        TextView txtCategoryName,txtCategoryAmount;
        LinearLayout layoutChildContainer;
        LinearLayout layoutHeader;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            imgCategoryIcon = itemView.findViewById(R.id.imgCategoryIcon);
            imgToggleArrow =itemView.findViewById(R.id.imgToggleArrow);
            txtCategoryAmount= itemView.findViewById(R.id.txt_Category_Amount);
            txtCategoryName= itemView.findViewById(R.id.txtCategoryName);
            layoutChildContainer=itemView.findViewById(R.id.layoutChildContainer);
            layoutHeader =itemView.findViewById(R.id.layout_Header);
        }
    }
    @NonNull
    @Override
    public SpendingGroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spending_group,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SpendingGroupAdapter.ViewHolder holder, int position){
        SpendingGroup group = spendingGroups.get(position);

        holder.txtCategoryName.setText(group.getTnDM());
        holder.txtCategoryAmount.setText(String.format("%,d đ",group.getTongChi()));

        String iconName= group.getIconName();
        int iconResId = holder.itemView.getContext().getResources().getIdentifier(iconName,"drawable",holder.itemView.getContext().getPackageName());
        if(iconResId != 0){
            holder.imgCategoryIcon.setImageResource(iconResId);

        }else {
            holder.imgCategoryIcon.setImageResource(R.drawable.ic_food);
        }

        if (group.isExpanded()) {
            holder.layoutChildContainer.setVisibility(View.VISIBLE);
            holder.imgToggleArrow.setRotation(180f);
        } else {
            holder.layoutChildContainer.setVisibility(View.GONE);
            holder.imgToggleArrow.setRotation(0f);
        }

        holder.layoutChildContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
        for(GIAODICH gd : group.getGiaodichList()){
            View childView = inflater.inflate(R.layout.item_child_spending,holder.layoutChildContainer,false);

            TextView txtDate = childView.findViewById(R.id.tv_child_date);
            TextView txtAmount = childView.findViewById(R.id.tv_child_amount);

            txtDate.setText(gd.getThoiGian());
            Log.d("DEBUG_DATE", "Ngày giao dịch: " + gd.getThoiGian());

            txtAmount.setText(String.format("%,d đ",gd.getSoTien()));

            holder.layoutChildContainer.addView(childView);
        }
        holder.layoutHeader.setOnClickListener(v -> {
            boolean newState = !group.isExpanded();
            group.setExpanded(newState);
            notifyItemChanged(holder.getAdapterPosition());
        });
        holder.imgToggleArrow.setOnClickListener(v -> {
            boolean newState = !group.isExpanded();
            group.setExpanded(newState);
            notifyItemChanged(holder.getAdapterPosition());
        });
    }
    @Override
    public int getItemCount(){
        return spendingGroups.size();
    }
}
