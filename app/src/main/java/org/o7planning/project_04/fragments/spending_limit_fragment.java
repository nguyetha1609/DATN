package org.o7planning.project_04.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import org.o7planning.project_04.R; // Đảm bảo import R
import org.o7planning.project_04.activities.AccountInforActivity; // Import đã cập nhật
import org.o7planning.project_04.activities.ChangePasswordActivity;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.databases.LimitDAO;


public class spending_limit_fragment extends Fragment {
    public spending_limit_fragment(){};
    private DBHelper db;
    private LimitDAO dblimit;
    private final ActivityResultLauncher<Intent> addLimitLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result ->{
                        if(result.getResultCode()== Activity.RESULT_OK){
                        }
                    });

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.activity_account,container,false);
        // Xử lý click cho layoutAccount
        LinearLayout layoutAccount = view.findViewById(R.id.layoutAccount);
        if (layoutAccount != null) {
            layoutAccount.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AccountInforActivity.class);
                startActivity(intent);
            });
        }

        // Xử lý click cho layoutChangePassword
        LinearLayout layoutChangePassword = view.findViewById(R.id.layoutChangePassword);
        if (layoutChangePassword != null) {
            layoutChangePassword.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            });
        }
        return view;
    }

//    private void loadLimitList() {
//        dblimit = new LimitDAO(getContext());
//        List<Limit> limitList = dblimit.getAllLimits();
//        if (limitAdapter == null) {
//            limitAdapter = new LimitAdapter(getContext(), limitList);
//            recyclerView.setAdapter(limitAdapter);
//        } else {
//            limitAdapter.updateData(limitList);
//        }
//    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        loadLimitList();
//    }
}


