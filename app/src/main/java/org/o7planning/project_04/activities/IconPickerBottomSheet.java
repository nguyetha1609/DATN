package org.o7planning.project_04.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.o7planning.project_04.Adapter.IconAdapter;
import org.o7planning.project_04.R;
import org.o7planning.project_04.model.CategoryIcon;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class IconPickerBottomSheet extends BottomSheetDialogFragment implements IconAdapter.OnIconClickListener{
    private IconAdapter adapter;
    private RecyclerView recyclerView;
    private List<CategoryIcon> iconList;

    public interface IconSelectedListener {
        void onIconSelected(String iconName, int resId);
    }

    private IconSelectedListener listener;

    public void setIconSelectedListener(IconSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_icon_picker, container, false);

        recyclerView = view.findViewById(R.id.recycler_icons);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

        iconList = getAllIcons();
        adapter = new IconAdapter(iconList, this);
        recyclerView.setAdapter(adapter);

        // Nút hủy
        view.findViewById(R.id.btn_cancel_icon_picker).setOnClickListener(v -> dismiss());

        return view;
    }

    private List<CategoryIcon> getAllIcons() {
        List<CategoryIcon> icons = new ArrayList<>();
        Field[] drawables = R.drawable.class.getDeclaredFields();

        for (Field field : drawables) {
            String name = field.getName();
            if (name.startsWith("cate_")) {
                try {
                    int resId = field.getInt(null);
                    icons.add(new CategoryIcon(name, resId));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return icons;
    }

    @Override
    public void onIconClick(String iconName, int resId) {
        if (listener != null) {
            listener.onIconSelected(iconName, resId);
        }
        dismiss();
    }
}
