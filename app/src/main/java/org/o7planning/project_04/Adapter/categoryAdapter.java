package org.o7planning.project_04.Adapter;

import org.o7planning.project_04.model.Category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerobranch.layout.SwipeLayout;

import org.o7planning.project_04.R;

import java.util.List;

public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.ViewHolder> {
    private Context context;
    private List<Category> categoryList;
    private boolean isEditMode = false;

    public interface OnItemActionListener{
        void onDelete(Category cat);
        void onEdit (Category cat);
    }
    private OnItemActionListener actionListener;

    public void setOnItemActionListener(OnItemActionListener listener){
        this.actionListener = listener;
    }

    public categoryAdapter(Context cont, List<Category> categoryList){
        this.context= cont;
        this.categoryList= categoryList;

    }

    @NonNull
    @Override
    public categoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull categoryAdapter.ViewHolder holder, int position) {
        Category cat = categoryList.get(position);
        holder.txtName.setText(cat.getTenDM());

        // Giả định bạn có method lấy id resource icon từ tên
        int iconResId = context.getResources().getIdentifier(cat.getHinhAnh(),"drawable",context.getPackageName());
        holder.imgIcon.setImageResource(iconResId !=0? iconResId :R.drawable.ic_default);

        if (isEditMode) {
            holder.imgEdit.setVisibility(View.VISIBLE);
            holder.imgDelete.setVisibility(View.VISIBLE);
        } else {
            holder.imgEdit.setVisibility(View.GONE);
            holder.imgDelete.setVisibility(View.GONE);
            holder.swipeLayout.close();
        }

        holder.imgDelete.setOnClickListener(v -> {
            holder.swipeLayout.openRight();
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDelete(cat);
            }
        });

        holder.imgEdit.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEdit(cat);
            }
        });
    }
//    @Override
//    public void onBindViewHolder(@NonNull CategoryViewHolder holder,int position){
//        category cate = categoryList.get(position);
//        holder.txtname.setText(cate.getTenDM());
//        int iconResId = cont.getResources().getIdentifier(cate.getHinhAnh(),"drawable",cont.getPackageName());
//        holder.imgIcon.setImageResource(iconResId !=0? iconResId :R.drawable.ic_default);
//
//        holder.imgIcon.setOnClickListener(v -> {
//            Intent intent = new Intent(cont, EditCategoryActivity.class);
//            intent.putExtra("ID_DM",cate.getID());
//            cont.startActivity(intent);
//        });
//
//    }
    @Override
    public int getItemCount(){
        return categoryList.size();
    }
    public void setData(List<Category> newList) {
        this.categoryList = newList;
    }
    public void setEditMode(boolean editMode){
        this.isEditMode =editMode;
    }
public static class ViewHolder extends RecyclerView.ViewHolder{
    SwipeLayout swipeLayout;
        ImageView imgIcon,imgEdit, imgDelete;
        TextView txtName;
        TextView btnDelete;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            imgIcon = itemView.findViewById(R.id.img_icon);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
            txtName= itemView.findViewById(R.id.txt_name);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
}
}
