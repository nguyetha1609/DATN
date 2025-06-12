package org.o7planning.project_04.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.project_04.R;
import org.o7planning.project_04.activities.LimitDetailActivity;
import org.o7planning.project_04.databases.LimitDAO;
import org.o7planning.project_04.model.Limit;
import org.o7planning.project_04.model.Category;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LimitAdapter extends RecyclerView.Adapter<LimitAdapter.LimitViewHolder> {
    private List<Limit> limitList;
    private Context context;
    private LimitDAO dblimit;
    public  LimitAdapter (List<Limit> limitList){
        this.limitList= limitList;
    }
    public LimitAdapter(Context context, List<Limit> limitList) {
        this.context = context;
        this.limitList = limitList;
    }
    @NonNull
    @Override
    public LimitViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spending_limit,parent,false);
        return new LimitViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull LimitViewHolder holder,int position){
        Limit limit = limitList.get(position);
        holder.tvTenHM.setText(limit.getTenHM());
        String ngayGD = formatDateDayMonth(limit.getNgayGD());
        String ngayKT = formatDateDayMonth(limit.getNgayKetThuc());
        holder.tvThoiGian.setText(ngayGD + " - " + ngayKT);
        holder.TvSotien.setText(formatCurrency(limit.getSoTien())+ "đ");

        long tienConlai= limit.getSoTien() - getSotienDaDung(limit.getID_HM());
        holder.TvTienconLai.setText(formatCurrency(tienConlai)+ "đ");

        long daysLeft= getDaysLeft(limit.getNgayKetThuc());
        if (daysLeft == 0) {
            holder.tvConlai.setText("Đã hết hạn");
            holder.tvConlai.setTextColor(context.getResources().getColor(R.color.red));
            holder.tvConlai.setBackgroundResource(R.drawable.bg_button_delete); // file drawable bạn đã tạo
        } else {
            holder.tvConlai.setText("Còn " + daysLeft + " ngày");

        }

        int progress= (int) ((limit.getSoTien()-tienConlai)/limit.getSoTien()*100);
        holder.progressBar.setProgress(progress);

        holder.imgIcon.setImageResource(getIconResId(limit));




holder.itemView.setOnClickListener(v -> {
    Intent intent = new Intent(context, LimitDetailActivity.class);
    intent.putExtra(LimitDetailActivity.EXTRA_LIMIT_ID,limit.getID_HM());
    context.startActivity(intent);
});

    }
    @Override
    public int getItemCount(){
        return limitList.size();
    }

    public static class LimitViewHolder extends RecyclerView.ViewHolder{
        TextView tvTenHM, tvThoiGian,TvSotien,tvConlai,TvTienconLai;
        ProgressBar progressBar;
        ImageView imgIcon;
        public LimitViewHolder(@NonNull View itemView){
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvTenHM= itemView.findViewById(R.id.tvTenHanMuc);
            tvThoiGian= itemView.findViewById(R.id.tvThoiGian);
            TvSotien=itemView.findViewById(R.id.tvSoTien);
            tvConlai= itemView.findViewById(R.id.tvConLai);
            TvTienconLai=itemView.findViewById(R.id.tvTienConLai);
            progressBar=itemView.findViewById(R.id.progressBar);

        }
    }
    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
    private long getDaysLeft(String ngayKT) {
        try {
            // Định dạng đúng với chuỗi ngày trong database
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date end = sdf.parse(ngayKT);

            Calendar calEnd = Calendar.getInstance();
            calEnd.setTime(end);
            calEnd.set(Calendar.HOUR_OF_DAY, 0);
            calEnd.set(Calendar.MINUTE, 0);
            calEnd.set(Calendar.SECOND, 0);
            calEnd.set(Calendar.MILLISECOND, 0);

            Calendar calNow = Calendar.getInstance();
            calNow.set(Calendar.HOUR_OF_DAY, 0);
            calNow.set(Calendar.MINUTE, 0);
            calNow.set(Calendar.SECOND, 0);
            calNow.set(Calendar.MILLISECOND, 0);

            long diff = calEnd.getTimeInMillis() - calNow.getTimeInMillis();
            long daysLeft = TimeUnit.MILLISECONDS.toDays(diff);

            return daysLeft >= 0 ? daysLeft : 0;

        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


    private String formatDateDayMonth(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr; // trả về nguyên bản nếu lỗi
        }
    }


    public void updateData(List<Limit> newLimitList){
        this.limitList.clear();
        this.limitList.addAll(newLimitList);
        notifyDataSetChanged();
    }
    public String formatCurrency(long amount) {
        return String.format("%,d", amount);
    }
    private Drawable getIconDrawable(Limit limit) {
         dblimit = new LimitDAO(context);
        List<Category> categories = dblimit.getCategoriesForLimit(limit.getID_HM());

        if (categories == null || categories.isEmpty()) {
            return context.getResources().getDrawable(R.drawable.ic_default);
        }

        if (categories.size() == 1) {
            int resId = getDrawableIdByName(categories.get(0).getHinhAnh());
            return context.getResources().getDrawable(resId != 0 ? resId : R.drawable.ic_default);
        } else {
            List<Drawable> drawables = new ArrayList<>();
            for (int i = 0; i < Math.min(3, categories.size()); i++) {
                int resId = getDrawableIdByName(categories.get(i).getHinhAnh());
                Drawable drawable = context.getResources().getDrawable(resId != 0 ? resId : R.drawable.ic_default);
                drawables.add(drawable);
            }
            return createStackedDrawable(drawables);
        }
    }

    private Drawable createStackedDrawable(List<Drawable> drawableList) {
        int count = Math.min(drawableList.size(), 3); // chỉ tối đa 3 icon
        Drawable[] drawables = new Drawable[count];
        for (int i = 0; i < count; i++) {
            drawables[i] = drawableList.get(i);
        }

        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        int overlapOffset = 20; // mức chồng sang trái, px (hoặc dp nếu chuyển đổi)

        for (int i = 0; i < count; i++) {
            // Chồng icon sang TRÁI (icon sau bị icon trước đè lên)
            int left = (count - i - 1) * overlapOffset;
            int right = i * overlapOffset;

            layerDrawable.setLayerInset(i, left, 0, right, 0);
        }

        return layerDrawable;
    }

    private int getIconResId(Limit limit){
        // gia dinh truoc
        return R.drawable.ic_default;

    }

    private int getDrawableIdByName(String iconName) {
        if (iconName == null || iconName.isEmpty()) return 0;
        return context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
    }

private long getSotienDaDung(int idHM){
        // tam truoc
    return 0;
}
}
