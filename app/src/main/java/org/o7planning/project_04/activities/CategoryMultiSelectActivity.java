package org.o7planning.project_04.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import org.o7planning.project_04.Adapter.CategoryCheckboxAdapter;
import org.o7planning.project_04.Adapter.IconAdapter;
import org.o7planning.project_04.R;
import org.o7planning.project_04.databases.CategoryDAO;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.model.category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryMultiSelectActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CategoryCheckboxAdapter adapter;
    private SwitchCompat switchSelectAll;
    private List<category> allCategories = new ArrayList<>();
    private Set<Integer> selectedId = new HashSet<>();
    private CategoryDAO dbcate;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId()==R.id.action_done){
                Intent resultIntent = new Intent();
                resultIntent.putIntegerArrayListExtra("selected_category_id",new ArrayList<>(selectedId));
                setResult(RESULT_OK,resultIntent);
                finish();
                return true;

            }
            return false;
        });

        //Nhan ds ID_DM da truyen tu man hinh trc
        ArrayList<Integer> selectedFromIntent = getIntent().getIntegerArrayListExtra("selected_category_id");
        if(selectedFromIntent !=null){
            selectedId.clear();
            selectedId.addAll(selectedFromIntent);
        }


        recyclerView = findViewById(R.id.recycler_categories);
        switchSelectAll = findViewById(R.id.switch_select_all);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadCategories();

        adapter = new CategoryCheckboxAdapter(allCategories,selectedId);
        recyclerView.setAdapter(adapter);

        switchSelectAll.setChecked(selectedId.size() == allCategories.size());


        switchSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                adapter.selectAll();
            }else {
                adapter.deselectAll();
            }
        });
    }
    private void loadCategories(){
        CategoryDAO dbcate = new CategoryDAO(this);
        allCategories.clear();
        allCategories.addAll(dbcate.getAllCategories("ChiTieu"));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
