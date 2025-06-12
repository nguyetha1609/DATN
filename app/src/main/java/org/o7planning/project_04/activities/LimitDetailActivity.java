package org.o7planning.project_04.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.appbar.MaterialToolbar;

import org.o7planning.project_04.R;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.databases.LimitDAO;
import org.o7planning.project_04.model.Limit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LimitDetailActivity extends AppCompatActivity {
    public static final String EXTRA_LIMIT_ID = "limit_id";
    private TextView tvTotalLimit, tvDateRange, tvDayleft,tvAmountLeft;
    private ProgressBar progressLimit;
    private ImageView iconBarchart;
    private TextView tvChartLabel;
    private LineChart lineChart;
    private LinearLayout ll_SpendingList;
    private int limitId;
    private TextView tv_expired;
    private LimitDAO dblimit;


    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limit_detail);
        dblimit = new LimitDAO(this);

        tvTotalLimit= findViewById(R.id.tv_total_limit);
        tvDateRange= findViewById(R.id.tv_date_range);
        tvDayleft=findViewById(R.id.tv_days_left);
        tvAmountLeft= findViewById(R.id.tv_amount_left);
        progressLimit= findViewById(R.id.progress_limit);
        iconBarchart= findViewById(R.id.icon_barchart);
        tvChartLabel= findViewById(R.id.tv_chart_label);
        lineChart= findViewById(R.id.lineChart);
        ll_SpendingList= findViewById(R.id.ll_SpendingList);
        tv_expired = findViewById(R.id.tv_status_expired);

        ll_SpendingList.setOnClickListener(v -> {
            Intent intent =new Intent(this, SpendingListActivity.class);
            intent.putExtra(EXTRA_LIMIT_ID,limitId);
            startActivity(intent);
        });

        // Lay limit tu Intent
        limitId= getIntent().getIntExtra(EXTRA_LIMIT_ID,-1);
        if(limitId ==-1){
            Toast.makeText(this,"Khong tim thay Han muc",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadLimitDetail(limitId);


        MaterialToolbar toolbar = findViewById(R.id.toolbar_limit_detail);
         setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu){
        getMenuInflater().inflate(R.menu.menu_limit_detail,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item){
        if(item.getItemId()==R.id.action_edit){
            Intent intent = new Intent(this, EditLimitActivity.class);
            intent.putExtra(EditLimitActivity.EXTRA_LIMIT_ID, limitId);
            startActivityForResult(intent, 1002);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadLimitDetail(int limitId){
        DBHelper db = new DBHelper(this);
        Limit limit= dblimit.getLimitById(limitId);

//        if(limit==null){
//            Toast.makeText(this,"Khong tim thay han muc",Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
        tvTotalLimit.setText(formatCurrency(limit.getSoTien())+"d");
        tvDateRange.setText(formatDate(limit.getNgayGD()) + " - " + formatDate(limit.getNgayKetThuc()));

        long daysLeft = getDaysLeft(limit.getNgayKetThuc());
        if (daysLeft == 0) {
            tv_expired.setVisibility(View.VISIBLE);  // hiện chữ "Hết hạn"
        } else {
            tv_expired.setVisibility(View.GONE);     // ẩn nếu chưa hết hạn
        }

        long daDung = 0;
        long conlai=limit.getSoTien() -daDung;

        tvAmountLeft.setText(formatCurrency(conlai) + "d");
        int progress =(int) ((limit.getSoTien()- conlai)*100.0/limit.getSoTien());
        progressLimit.setProgress(progress);
    }
    private String formatCurrency(long amount){
        return String.format("%,d",amount);
    }
    private long getDaysLeft(String ngayKetThuc) {
        try {
            // Format đúng với dữ liệu lưu trong SQLite (ví dụ: yyyy-MM-dd)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Date endDate = sdf.parse(ngayKetThuc);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);
            endCalendar.set(Calendar.HOUR_OF_DAY, 0);
            endCalendar.set(Calendar.MINUTE, 0);
            endCalendar.set(Calendar.SECOND, 0);
            endCalendar.set(Calendar.MILLISECOND, 0);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long diffMillis = endCalendar.getTimeInMillis() - today.getTimeInMillis();
            long daysLeft = TimeUnit.MILLISECONDS.toDays(diffMillis);

            return daysLeft >= 0 ? daysLeft : 0;

        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr; // fallback nếu lỗi
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002 && resultCode == RESULT_OK) {
            loadLimitDetail(limitId);
        }
    }
}
