package org.o7planning.project_04.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import org.o7planning.project_04.activities.MainActivity;
import org.o7planning.project_04.R;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {
    private static final int NUM_PAGES = 4;
    private final ViewPager2 viewPager;
    private final SharedPreferences prefs;

    public OnboardingAdapter(ViewPager2 viewPager, SharedPreferences prefs) {
        this.viewPager = viewPager;
        this.prefs = prefs;
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

    @Override
    public int getItemViewType(int position) {
        return position;  // để inflate đúng layout screen1→screen4
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes;
        switch (viewType) {
            case 1: layoutRes = R.layout.ic_onboarding2; break;
            case 2: layoutRes = R.layout.ic_onboarding3; break;
            case 3: layoutRes = R.layout.ic_onboarding4; break;
            default: layoutRes = R.layout.ic_onboarding1; break;
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutRes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        holder.btnNext.setOnClickListener(v -> {
            Context ctx = v.getContext();
            if (pos < NUM_PAGES - 1) {
                viewPager.setCurrentItem(pos + 1, true);
            } else {
                // Trang cuối: lưu flag và mở MainActivity
                prefs.edit().putBoolean("isFirstRun", false).apply();
                Intent i = new Intent(ctx, MainActivity.class);
                ctx.startActivity(i);
                if (ctx instanceof Activity) ((Activity) ctx).finish();
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Button btnNext;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnNext = itemView.findViewById(R.id.btnNext);
        }
    }
}
