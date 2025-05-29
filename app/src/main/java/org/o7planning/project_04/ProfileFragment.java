package org.o7planning.project_04;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide; // Thư viện để tải ảnh, bạn cần thêm dependency
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.materialswitch.MaterialSwitch; // Import cho MaterialSwitch
public class ProfileFragment extends Fragment{
    private TextView textUserName;
    private ShapeableImageView imageAvatar;
    private TextView textMonthlyAmount;
    private MaterialSwitch switchDarkMode;

    // Các mục cài đặt
    private LinearLayout itemAccount;
    private LinearLayout itemChangePassword;
    private LinearLayout itemLanguage;
    private LinearLayout itemDarkMode;
    private LinearLayout itemHistory;
    private LinearLayout itemExportCsv;
    private LinearLayout itemCurrencyRate;


    // Constants cho SharedPreferences
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_AVATAR_URI = "avatarUri";
    private static final String KEY_MONTHLY_AMOUNT = "monthlyAmount";
    private static final String KEY_DARK_MODE = "darkMode";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Ánh xạ các View
        textUserName = view.findViewById(R.id.text_user_name);
        imageAvatar = view.findViewById(R.id.image_avatar);
        textMonthlyAmount = view.findViewById(R.id.text_monthly_amount);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);

        itemAccount = view.findViewById(R.id.item_account);
        itemChangePassword = view.findViewById(R.id.item_change_password);
        itemLanguage = view.findViewById(R.id.item_language);
        itemDarkMode = view.findViewById(R.id.item_dark_mode);
        itemHistory = view.findViewById(R.id.item_history);
        itemExportCsv = view.findViewById(R.id.item_export_csv);
        itemCurrencyRate = view.findViewById(R.id.item_currency_rate);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUserProfile(); // Tải thông tin người dùng khi Fragment được tạo

        // Thiết lập Listener cho các mục cài đặt
        itemAccount.setOnClickListener(v -> Toast.makeText(getContext(), "Mở Tài Khoản", Toast.LENGTH_SHORT).show());
        itemChangePassword.setOnClickListener(v -> Toast.makeText(getContext(), "Mở Đổi Mật Khẩu", Toast.LENGTH_SHORT).show());
        itemLanguage.setOnClickListener(v -> Toast.makeText(getContext(), "Mở Ngôn Ngữ", Toast.LENGTH_SHORT).show());
        itemHistory.setOnClickListener(v -> Toast.makeText(getContext(), "Mở Lịch sử", Toast.LENGTH_SHORT).show());
        itemExportCsv.setOnClickListener(v -> Toast.makeText(getContext(), "Mở Xuất CSV", Toast.LENGTH_SHORT).show());
        itemCurrencyRate.setOnClickListener(v -> Toast.makeText(getContext(), "Mở Tỷ Giá Tiền Tệ", Toast.LENGTH_SHORT).show());


        // Xử lý switch Chế Độ Tối
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                saveDarkModeState(true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                saveDarkModeState(false);
            }
            // Để theme thay đổi ngay lập tức, bạn có thể cần recreate activity/fragment
            // getActivity().recreate(); // Sử dụng cẩn thận, có thể gây giật
        });

        // Tải trạng thái Chế Độ Tối ban đầu
        loadDarkModeState();
    }

    private void loadUserProfile() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_LOGGED_IN, false);

        if (isLoggedIn) {
            // Hiển thị thông tin người dùng
            String userName = prefs.getString(KEY_USER_NAME, "Người dùng");
            String avatarUriString = prefs.getString(KEY_AVATAR_URI, null);
            long monthlyAmount = prefs.getLong(KEY_MONTHLY_AMOUNT, 0);

            textUserName.setText(userName);
            textMonthlyAmount.setText(String.format("%,d VND", monthlyAmount)); // Định dạng tiền tệ

            if (avatarUriString != null && !avatarUriString.isEmpty()) {
                // Sử dụng Glide để tải ảnh từ URI
                Glide.with(this)
                        .load(Uri.parse(avatarUriString))
                        .placeholder(R.drawable.default_avatar) // Ảnh mặc định khi đang tải hoặc lỗi
                        .error(R.drawable.default_avatar) // Ảnh mặc định khi có lỗi
                        .into(imageAvatar);
            } else {
                imageAvatar.setImageResource(R.drawable.default_avatar);
            }
        } else {
            // Ẩn thông tin người dùng nếu chưa đăng nhập
            textUserName.setText("Khách"); // Hoặc một thông báo khác
            imageAvatar.setImageResource(R.drawable.default_avatar); // Ảnh mặc định
            textMonthlyAmount.setText("0 VND"); // Hoặc ẩn nó đi
            // Có thể ẩn cả các mục cài đặt liên quan đến tài khoản nếu muốn
            itemAccount.setVisibility(View.GONE);
            itemChangePassword.setVisibility(View.GONE);
        }
    }

    // Phương thức để lưu thông tin người dùng (sau khi đăng nhập/đăng ký)
    public void saveUserProfile(String userName, String avatarUri, long monthlyAmount) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_AVATAR_URI, avatarUri);
        editor.putLong(KEY_MONTHLY_AMOUNT, monthlyAmount);
        editor.apply(); // Sử dụng apply() để lưu bất đồng bộ

        loadUserProfile(); // Tải lại profile để cập nhật giao diện
    }

    // Phương thức để đăng xuất
    public void logoutUser() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Xóa tất cả dữ liệu người dùng
        editor.apply();

        loadUserProfile(); // Tải lại profile để hiển thị trạng thái chưa đăng nhập
    }

    private void loadDarkModeState() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false); // Mặc định là chế độ sáng
        switchDarkMode.setChecked(isDarkMode);
        // Thiết lập theme ngay lập tức khi Fragment được tải
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void saveDarkModeState(boolean isDarkMode) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_DARK_MODE, isDarkMode);
        editor.apply();
    }
}
