package org.o7planning.project_04.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.o7planning.project_04.R;
import org.o7planning.project_04.model.GIAODICH;
import org.o7planning.project_04.model.category;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final Context context;
    private List<GIAODICH> listGD;
    private final Map<Integer, category> mapDM;
    private OnItemClickListener itemClickListener; // Thêm listener

    public interface OnItemClickListener {
        void onItemClick(GIAODICH giaoDich);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
// mapDM: key=ID_DM, value=category object từ bảng DANHMUC

    public TransactionAdapter(Context ctx,
                              List<GIAODICH> data,
                              Map<Integer, category> mapDM) {
        this.context = ctx;
        this.listGD = data;
        this.mapDM = mapDM;
    }

    // Phương thức mới để cập nhật dữ liệu
    public void updateData(List<GIAODICH> newData) {
        this.listGD.clear();
        this.listGD.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        GIAODICH gd = listGD.get(position);

        // 1. Lấy thông tin danh mục
        category dm = mapDM.get(gd.getID_DM());
        if (dm != null) {
            holder.txtCategory.setText(dm.getTenDM());
            // Lấy icon từ tên HinhAnh (tên file drawable, không có .png)
            int resId = context.getResources()
                    .getIdentifier(dm.getHinhAnh(),
                            "drawable",
                            context.getPackageName());
            holder.imgCategory.setImageResource(
                    resId != 0 ? resId : R.drawable.ic_default);
        } else {
            holder.txtCategory.setText("Không xác định");
            holder.imgCategory.setImageResource(R.drawable.ic_default);
        }

        // 2. Ghi chú
        holder.txtNote.setText(gd.getGhiChu());

        // 3. Số tiền + định dạng + màu
        NumberFormat fmt = NumberFormat.getCurrencyInstance(
                new Locale("vi", "VN"));
        String s = fmt.format(gd.getSoTien());
        holder.txtAmount.setText(s);
        holder.txtAmount.setTextColor(
                gd.getSoTien() < 0
                        ? 0xFFFF3B30  // đỏ
                        : 0xFF34C759  // xanh
        );
        // Thiết lập OnClickListener cho itemView
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(gd); // Gọi interface khi click
            }
        });
    }

    @Override
    public int getItemCount() {
        return listGD.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory;
        TextView txtCategory, txtNote, txtAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtNote     = itemView.findViewById(R.id.txtNote);
            txtAmount   = itemView.findViewById(R.id.txtAmount);
        }
    }
}