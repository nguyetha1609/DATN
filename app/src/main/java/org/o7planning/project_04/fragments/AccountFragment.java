package org.o7planning.project_04.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.o7planning.project_04.R;
import org.o7planning.project_04.activities.AccountInforActivity;
import org.o7planning.project_04.activities.ChangePasswordActivity;
import org.o7planning.project_04.activities.MainActivity;
import org.o7planning.project_04.activities.SpendingLimitActivity;
import org.o7planning.project_04.databases.PrepopulatedDBHelper;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    private CircleImageView imgProfile;
    private TextView tvUserName;
    private Spinner spinnerIncomeType;
    private TextView tvAmount;
    private LinearLayout layoutAccount, layoutChangePassword, layoutLimit;
    private Button btnLogout;
    private BottomNavigationView bottomNav;

    private PrepopulatedDBHelper dbHelper;
    private SQLiteDatabase database;
    private int currentUserId;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        imgProfile.setImageURI(uri);
                        ContentValues cv = new ContentValues();
                        cv.put("HinhAnh", uri.toString());
                        database.update("TAIKHOAN", cv, "ID_TK = ?", new String[]{String.valueOf(currentUserId)});
                        Toast.makeText(getContext(), "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_fragment, container, false);

        dbHelper = new PrepopulatedDBHelper(requireContext());
        database = dbHelper.openDatabase();

        SharedPreferences sharedPrefs = requireActivity().getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE);
        currentUserId = sharedPrefs.getInt("ID_TK", -1);
        if (currentUserId == -1) {
            Toast.makeText(getContext(), "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
            return view;
        }

        // Ánh xạ View
        imgProfile = view.findViewById(R.id.imgProfile);
        tvUserName = view.findViewById(R.id.tvUserName);
        spinnerIncomeType = view.findViewById(R.id.spinnerIncomeType);
        tvAmount = view.findViewById(R.id.tvAmount);
        layoutAccount = view.findViewById(R.id.layoutAccount);
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);
        layoutLimit = view.findViewById(R.id.layoutLimit);
        btnLogout = view.findViewById(R.id.btnLogout);
        bottomNav = requireActivity().findViewById(R.id.bottomNav); // Nếu dùng chung BottomNav

        // Load dữ liệu
        loadProfileImageFromDB();
        loadUserNameFromDB();
        setupIncomeTypeSpinner();
        loadAmountFromDB();

        imgProfile.setOnClickListener(v -> confirmChangeProfileImage());
        layoutAccount.setOnClickListener(v -> startActivity(new Intent(getActivity(), AccountInforActivity.class)));
        layoutChangePassword.setOnClickListener(v -> startActivity(new Intent(getActivity(), ChangePasswordActivity.class)));
        layoutLimit.setOnClickListener(v -> startActivity(new Intent(getActivity(), SpendingLimitActivity.class)));
        btnLogout.setOnClickListener(v -> confirmLogout());

        return view;
    }

    private void loadProfileImageFromDB() {
        Cursor cursor = database.query("TAIKHOAN", new String[]{"HinhAnh"}, "ID_TK = ?", new String[]{String.valueOf(currentUserId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String imageUriStr = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"));
            if (imageUriStr != null && !imageUriStr.isEmpty()) {
                imgProfile.setImageURI(Uri.parse(imageUriStr));
            }
            cursor.close();
        }
    }

    private void confirmChangeProfileImage() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Thay đổi ảnh đại diện")
                .setMessage("Bạn có muốn thay đổi ảnh không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    imagePickerLauncher.launch(intent);
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void loadUserNameFromDB() {
        Cursor cursor = database.query("TAIKHOAN", new String[]{"Username"}, "ID_TK = ?", new String[]{String.valueOf(currentUserId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
            tvUserName.setText(username);
            cursor.close();
        } else {
            tvUserName.setText("Chưa có tên");
        }
    }

    private void setupIncomeTypeSpinner() {
        spinnerIncomeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selected = adapterView.getItemAtPosition(position).toString();
                //Toast.makeText(getContext(), "Bạn chọn: " + selected, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void loadAmountFromDB() {
        Cursor cursor = database.rawQuery("SELECT SUM(SoTien) AS Total FROM GIAODICH", null);
        if (cursor != null && cursor.moveToFirst()) {
            long total = cursor.getLong(cursor.getColumnIndexOrThrow("Total"));
            tvAmount.setText(String.format("%,d VND", total));
            cursor.close();
        } else {
            tvAmount.setText("0 VND");
        }
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Có", (dialog, which) -> performLogout())
                .setNegativeButton("Không", null)
                .show();
    }

    private void performLogout() {
        SharedPreferences sharedPrefs = requireActivity().getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE);
        sharedPrefs.edit().clear().apply();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
