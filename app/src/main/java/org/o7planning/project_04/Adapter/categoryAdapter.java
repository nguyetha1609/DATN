package org.o7planning.project_04.Adapter;


import org.o7planning.project_04.model.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.zerobranch.layout.SwipeLayout;
import com.zerobranch.layout.SwipeLayout;
import org.o7planning.project_04.R;

import java.util.List;

public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.ViewHolder> {
    private Context context;
    private List<category> categoryList;
    private boolean isEditMode = false;

    public interface OnItemActionListener{
        void onDelete(category cat);
        void onEdit (category cat);
    }
    private OnItemActionListener actionListener;

    public void setOnItemActionListener(OnItemActionListener listener){
        this.actionListener = listener;
    }

    public categoryAdapter(Context cont, List<category> categoryList){
        this.context= cont;
        this.categoryList= categoryList;

    }
//
//        @Override
//        public void onClick(View v) {
//            int id_dm = categoryList.get(position).getID(); // ID_DM danh mục được chọn
//
//            Intent resultIntent = new Intent();
//            resultIntent.putExtra("selected_category_id", id_dm);
//            setResult(RESULT_OK, resultIntent); // Trả kết quả về
//            finish(); // Quay lại màn hình trước
//        }
//    });
    /** Callback khi user click chọn 1 item (chế độ chọn danh mục) */
    public interface OnItemClickListener {
        void onItemClick(category cat);
    }
    private OnItemClickListener clickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        category cat = categoryList.get(position);
        holder.txtName.setText(cat.getTenDM());
        int iconResId = context.getResources()
                .getIdentifier(cat.getHinhAnh(),"drawable",context.getPackageName());
        holder.imgIcon.setImageResource(iconResId !=0? iconResId :R.drawable.ic_default);

        // Nếu ở chế độ chọn (clickListener != null), chỉ xử lý click chọn
        if (clickListener != null && !isEditMode) {
            //holder.swipeLayout.setLockDrag(true);      // khóa swipe
            holder.imgEdit.setVisibility(View.GONE);
            holder.imgDelete.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                clickListener.onItemClick(cat);
            });
        }
        else {
            // mặc định xử lý edit/xóa vẫn như cũ
            //holder.swipeLayout.setLockDrag(false);
        }
    }


    @NonNull
    @Override
    public categoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);



    }
    @Override
    public int getItemCount(){
        return categoryList.size();
    }
    public void setData(List<category> newList) {
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
