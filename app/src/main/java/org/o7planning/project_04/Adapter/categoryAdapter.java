package org.o7planning.project_04.Adapter;

import org.o7planning.project_04.activities.EditCategoryActivity;
import org.o7planning.project_04.model.category;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.project_04.R;

import java.util.List;

public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.CategoryViewHolder> {
    private Context cont;
    private List<category> categoryList;

    public categoryAdapter(Context cont, List<category> categoryList){
        this.cont= cont;
        this.categoryList= categoryList;

    }
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View view= LayoutInflater.from(cont).inflate(R.layout.item_category,parent,false);
        return new CategoryViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder,int position){
        category cate = categoryList.get(position);
        holder.txtname.setText(cate.getTenDM());
        int iconResId = cont.getResources().getIdentifier(cate.getHinhAnh(),"drawable",cont.getPackageName());
        holder.imgIcon.setImageResource(iconResId !=0? iconResId :R.drawable.ic_default);

        holder.imgIcon.setOnClickListener(v -> {
            Intent intent = new Intent(cont, EditCategoryActivity.class);
            intent.putExtra("ID_DM",cate.getID());
            cont.startActivity(intent);
        });

    }
    @Override
    public int getItemCount(){
        return categoryList.size();
    }
    public void setData(List<category> newList) {
        this.categoryList = newList;
    }
public class CategoryViewHolder extends RecyclerView.ViewHolder{
        ImageView imgIcon;
        TextView txtname;

        public CategoryViewHolder(@NonNull View itemView){
            super(itemView);
            imgIcon= itemView.findViewById(R.id.img_icon);
            txtname = itemView.findViewById(R.id.txt_name);
        }
}
}
