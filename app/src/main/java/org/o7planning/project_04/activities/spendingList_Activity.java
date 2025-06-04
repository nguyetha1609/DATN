package org.o7planning.project_04.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import org.o7planning.project_04.Adapter.SpendingGroupAdapter;
import org.o7planning.project_04.R;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.model.GIAODICH;
import org.o7planning.project_04.model.Limit;
import org.o7planning.project_04.model.SpendingGroup;
import org.o7planning.project_04.model.spendingsummary;

import java.util.ArrayList;
import java.util.List;

public class spendingList_Activity extends AppCompatActivity {
    private int limitId;


    protected  void onCreate(@NonNull Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spending_list);

        int limitId = getIntent().getIntExtra(LimitDetailActivity.EXTRA_LIMIT_ID, -1);
        if (limitId == -1) {
            Toast.makeText(this, "Không tìm thấy hạn mức", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        MaterialToolbar toolbar = findViewById(R.id.toolbar_spending_list);
//        setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Gọi hàm load dữ liệu chi tiêu theo hạn mức
        loadSpendingsForLimit(limitId);
    }

    private void loadSpendingsForLimit(int limitId) {
        DBHelper db = new DBHelper(this);
        Limit limit = db.getLimitById(limitId);
        if (limit == null) return;

        if (limit == null) {
            Toast.makeText(this, "Hạn mức không tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        String startDate = limit.getNgayGD();
        String endDate = limit.getNgayKetThuc();

        List<Integer> listDmId = limit.getListDanhMuc();
        List<spendingsummary> summaryList = db.getSpendingsByLimit(limitId,startDate,endDate);

        List<SpendingGroup> spendingGroups = new ArrayList<>();

        for(spendingsummary summary : summaryList){
            List<GIAODICH> giaodichList = db.getTransactionsByCategoryAndLimit(summary.getIdDM(),startDate,endDate,limitId);
            SpendingGroup group = new SpendingGroup(summary.getIdDM(),summary.getTenDM(),summary.getTongChi(),summary.getHinhAnh(),giaodichList);
            spendingGroups.add(group);
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerSpendingGroup);
        SpendingGroupAdapter adapter = new SpendingGroupAdapter(spendingGroups);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        long total =0;
        for(spendingsummary s : summaryList){
            total +=s.getTongChi();
        }
        TextView txtTotalSpending = findViewById(R.id.txtTotalSpending);
        txtTotalSpending.setText(String.format("%,d đ",total));

    }
}
