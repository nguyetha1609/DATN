package org.o7planning.project_04.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.o7planning.project_04.Adapter.LimitAdapter;
import org.o7planning.project_04.R;
import org.o7planning.project_04.activities.activity_add_spendinglimit;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.model.Limit;

import java.util.List;

public class spending_limit_fragment extends Fragment {
    public spending_limit_fragment(){};
    private RecyclerView recyclerView;
    private LimitAdapter limitAdapter;
    private DBHelper db;
    private final ActivityResultLauncher<Intent> addLimitLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result ->{
                        if(result.getResultCode()== Activity.RESULT_OK){
                            loadLimitList();
                        }
                    });
    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.spending_limit_fragment,container,false);

        db = new DBHelper(getContext());
        recyclerView= view.findViewById(R.id.recyclerViewSpendingLimit);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        Button btn_add = view.findViewById(R.id.btn_add_spending);
        btn_add.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), activity_add_spendinglimit.class);
            addLimitLauncher.launch(intent);
        });
        loadLimitList();
        return view;
    }
    private void loadLimitList() {
        List<Limit> limitList = db.getAllLimits();
        if (limitAdapter == null) {
            limitAdapter = new LimitAdapter(getContext(), limitList);
            recyclerView.setAdapter(limitAdapter);
        } else {
            limitAdapter.updateData(limitList);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        loadLimitList();
    }
}
