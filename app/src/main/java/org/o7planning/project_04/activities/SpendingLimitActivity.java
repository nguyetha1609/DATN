package org.o7planning.project_04.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.project_04.Adapter.LimitAdapter;
import org.o7planning.project_04.R;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.databases.LimitDAO;
import org.o7planning.project_04.model.Limit;

import java.util.List;

public class SpendingLimitActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LimitAdapter limitAdapter;
    private DBHelper db;
    private LimitDAO dblimit;

    private final ActivityResultLauncher<Intent> addLimitLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            loadLimitList();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spending_limit_fragment);

        db = new DBHelper(this);
        recyclerView = findViewById(R.id.recyclerViewSpendingLimit);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btn_add = findViewById(R.id.btn_add_spending);
        btn_add.setOnClickListener(v -> {
            Intent intent = new Intent(this, SpendingLimitActivity.class);
            addLimitLauncher.launch(intent);
        });

        loadLimitList();
    }

    private void loadLimitList() {
        dblimit = new LimitDAO(this);
        List<Limit> limitList = dblimit.getAllLimits();
        if (limitAdapter == null) {
            limitAdapter = new LimitAdapter(this, limitList);
            recyclerView.setAdapter(limitAdapter);
        } else {
            limitAdapter.updateData(limitList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLimitList();
    }
}
