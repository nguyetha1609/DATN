package org.o7planning.project_04.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.o7planning.project_04.Adapter.CategoryDetailAdapter;
import org.o7planning.project_04.R;
import org.o7planning.project_04.activities.CustomRangeBottomSheet;
import org.o7planning.project_04.activities.MonthPickerBottomSheet;
import org.o7planning.project_04.activities.WeekPickerBottomSheet;
import org.o7planning.project_04.activities.YearPickerBottomSheet;
import org.o7planning.project_04.databases.DBHelper;
import org.o7planning.project_04.model.categoryStat;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class statfragment extends Fragment {
    public statfragment() {
    }

    private View tabIndicator;
    private Button btnWeek, btnMonth, btnYear, btnoption;
    private ImageView ivBarChart, ivPieChart;
    private PieChart pieChart;
    private BarChart barChart;
    private TextView tvExpenseTab, tvIncomeTab, tvDateRange,tvChi, tvThu;
    private LinearLayout llDateNavigation;
    private int selectedTimeFrame = 0;
    private int selectedTab = 0;
    private int selectedChartType = 0;
    private RecyclerView rvCategoryDetails;
    private CategoryDetailAdapter adapter;
    private LinearLayout llTabSelector;
    private LocalDate currentStartDate,currentEndDate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stat_fragment, container, false);
        initViews(view);
        setUpListeners();
        selectTimeFrame(selectedTimeFrame);
        selectTab(selectedTab);
        selectChartType(selectedChartType);

        barChart.setVisibility(View.GONE);


        return view;
    }

    private void initViews(View view) {
        btnWeek = view.findViewById(R.id.btnWeek);
        btnMonth = view.findViewById(R.id.btnMonth);
        btnYear = view.findViewById(R.id.btnYear);
        btnoption = view.findViewById(R.id.btnCustom);
        ivBarChart = view.findViewById(R.id.ivBarChartIcon);
        ivPieChart = view.findViewById(R.id.ivPieChartIcon);
        tvExpenseTab = view.findViewById(R.id.tvExpenseTab);
        tvIncomeTab = view.findViewById(R.id.tvIncomeTab);
        tabIndicator = view.findViewById(R.id.tabIndicator);
        llDateNavigation = view.findViewById(R.id.llDateNavigation);
        tvDateRange = view.findViewById(R.id.tvDateRange);
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);
        rvCategoryDetails = view.findViewById(R.id.rvCategoryDetail);
        rvCategoryDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        llTabSelector = view.findViewById(R.id.llTabSelector);
        tvChi = view.findViewById(R.id.tvTotalExpense);
        tvThu = view.findViewById(R.id.tvTotalIncome);

    }

    private void setUpListeners() {
        btnWeek.setOnClickListener(v -> selectTimeFrame(0));
        btnMonth.setOnClickListener(v -> selectTimeFrame(1));
        btnYear.setOnClickListener(v -> selectTimeFrame(2));
        btnoption.setOnClickListener(v -> selectTimeFrame(3));

        llDateNavigation.setOnClickListener(v -> openBottomSheetForTimeFrame());

        tvExpenseTab.setOnClickListener(v -> {
            selectTab(0);
            selectChartType(selectedChartType);
        });

        tvIncomeTab.setOnClickListener(v -> {
            selectTab(1);
            selectChartType(selectedChartType);

        });

        ivPieChart.setOnClickListener(v -> selectChartType(0));
        ivBarChart.setOnClickListener(v -> selectChartType(1));
    }

    private void selectChartType(int type) {
        selectedChartType = type;

        if (type == 0) {
            ivPieChart.setScaleX(1.2f);
            ivPieChart.setScaleY(1.2f);
            ivPieChart.setAlpha(1.0f);

            ivBarChart.setScaleX(1.0f);
            ivBarChart.setScaleY(1.0f);
            ivBarChart.setAlpha(0.5f);
        } else {
            ivBarChart.setScaleX(1.2f);
            ivBarChart.setScaleY(1.2f);
            ivBarChart.setAlpha(1.0f);

            ivPieChart.setScaleX(1.0f);
            ivPieChart.setScaleY(1.0f);
            ivPieChart.setAlpha(0.5f);
        }
        if (currentStartDate != null && currentEndDate != null) {
            if (type == 0) {
                pieChart.setVisibility(View.VISIBLE);
                barChart.setVisibility(View.GONE);
                loadChartData(currentStartDate, currentEndDate);
            } else {
                pieChart.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);
                loadBarChartData(currentStartDate, currentEndDate);
            }

            // loadChartData();
        }
    }

    private void selectTimeFrame(int frame) {
        selectedTimeFrame = frame;

        btnWeek.setSelected(frame == 0);
        btnMonth.setSelected(frame == 1);
        btnYear.setSelected(frame == 2);
        btnoption.setSelected(frame == 3);

        updateDateRangeDisplay();
    }

    private void updateDateRangeDisplay() {
        switch (selectedTimeFrame) {
            case 0:
                tvDateRange.setText("13/05 - 19/05/2025");
                break;
            case 1:
                tvDateRange.setText("Tháng 5/2025");
                break;
            case 2:
                tvDateRange.setText("Năm 2025");
                break;
            case 3:
                tvDateRange.setText("25/04 - 09/05/2025");
                break;
        }
    }

    private void openBottomSheetForTimeFrame() {
        switch (selectedTimeFrame) {
            case 0:
                WeekPickerBottomSheet weekSheet = new WeekPickerBottomSheet();
                weekSheet.setOnWeekSelectedListener(new WeekPickerBottomSheet.OnWeekSelectedListener() {
                    @Override
                    public void onWeekSelected(String startDate, String endDate, String displayText) {
                        tvDateRange.setText(displayText);

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
                        currentStartDate = LocalDate.parse(startDate, formatter);
                        currentEndDate = LocalDate.parse(endDate, formatter);

                        updateChart(currentStartDate, currentEndDate);

                        updateSummary(currentStartDate.format(formatter), currentEndDate.format(formatter));



                    }
                });
                weekSheet.show(getParentFragmentManager(), "WeekPicker");
                break;
            case 1:
                new MonthPickerBottomSheet(selectedMonthText -> {
                    tvDateRange.setText(selectedMonthText);

                    String monthYear = selectedMonthText.replace("Tháng ", "").trim();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy", Locale.getDefault());
                    YearMonth yearMonth = YearMonth.parse(monthYear, formatter);

                    currentStartDate = yearMonth.atDay(1); // ngày đầu tháng
                    currentEndDate = yearMonth.atEndOfMonth(); // ngày cuối tháng

                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
                    updateChart(currentStartDate, currentEndDate);
                    updateSummary(currentStartDate.format(outputFormatter), currentEndDate.format(outputFormatter));
                }).show(getParentFragmentManager(), "MonthPicker");
                break;

            case 2:
                new YearPickerBottomSheet(selectedYearText -> {
                    tvDateRange.setText(selectedYearText);

                    String yearString = selectedYearText.replace("Năm ", "").trim();
                    int year = Integer.parseInt(yearString);

                    currentStartDate = LocalDate.of(year, 1, 1);
                    currentEndDate = LocalDate.of(year, 12, 31);

                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
                    updateChart(currentStartDate, currentEndDate);
                    updateSummary(currentStartDate.format(outputFormatter), currentEndDate.format(outputFormatter));
                }).show(getParentFragmentManager(), "YearPicker");
                break;

            case 3:
                CustomRangeBottomSheet bottomSheet = new CustomRangeBottomSheet();
                bottomSheet.setOnDateRangeSelectedListener(new CustomRangeBottomSheet.OnDateRangeSelectedListener() {
                    @Override
                    public void onDateRangeSelected(String start, String end, String tvrangeText) {
                        tvDateRange.setText(tvrangeText);

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
                         currentStartDate = LocalDate.parse(start, formatter);
                         currentEndDate = LocalDate.parse(end, formatter);

                        updateChart(currentStartDate, currentEndDate);
                        updateSummary(currentStartDate.format(formatter), currentEndDate.format(formatter));


                    }
                });
                bottomSheet.show(getParentFragmentManager(), "CustomRangeBottomSheet");
                break;
        }
    }

    private void selectTab(int tab) {
        selectedTab = tab;
        tvExpenseTab.setTextColor(getResources().getColor(tab == 0 ? R.color.black : R.color.grey));
        tvIncomeTab.setTextColor(getResources().getColor(tab == 1 ? R.color.black : R.color.grey));
        moveTabIndicator(tab);



        if (currentStartDate != null && currentEndDate != null) {
            selectChartType(selectedChartType);
        }
    }

    private void moveTabIndicator(int tab) {
        int containerWidth = llTabSelector.getWidth();

        if (containerWidth == 0) {
            llTabSelector.post(() -> moveTabIndicator(tab));
            return;
        }

        int tabCount = 2; // 2 tab
        int tabWidth = containerWidth / tabCount;

        // Set width cho indicator
        ViewGroup.LayoutParams params = tabIndicator.getLayoutParams();
        params.width = tabWidth;
        tabIndicator.setLayoutParams(params);

        // Di chuyển indicator sang trái hoặc phải (tab=0 hay 1)
        tabIndicator.animate()
                .translationX(tabWidth * tab)
                .setDuration(200)
                .start();
    }

    private void updateChart(LocalDate startDate, LocalDate endDate) {
        if (selectedChartType == 0) {
            pieChart.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
            loadChartData(startDate, endDate);
        } else {
            pieChart.setVisibility(View.GONE);
            barChart.setVisibility(View.VISIBLE);
            loadBarChartData(startDate, endDate);
        }
    }

    private  void loadChartData(LocalDate startDate, LocalDate endDate){
        DBHelper db = new DBHelper(requireContext());

        String loai = selectedTab == 0 ? "ChiTieu" : "ThuNhap";
//format theo định dạng ngày lưu trong db để truy vấn được yyyy-MM-dd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String start = startDate.format(formatter);
        String end = endDate.format(formatter);

        List<categoryStat> catestat = db.getStatsByDateRange(loai,start,end);



        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for(categoryStat stat :catestat){
            entries.add(new PieEntry(stat.getAmount(),stat.getCateName()));
            colors.add(stat.getColor());
        }

        PieDataSet dataSet = new PieDataSet(entries,"");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));


        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
       // pieChart.setNoDataText("");
        pieChart.setDrawEntryLabels(true);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();

        // Cập nhật RecyclerView
        if (adapter == null) {
            adapter = new CategoryDetailAdapter(catestat);
            rvCategoryDetails.setAdapter(adapter);
        } else {
            adapter.categoryList = catestat;
            adapter.notifyDataSetChanged();
        }


    }

  private  void loadBarChartData(LocalDate startDate,LocalDate endDate){
        DBHelper db = new DBHelper(requireContext());
        String loai = selectedTab ==0 ? "ChiTieu" :"ThuNhap";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String start = startDate.format(formatter);
        String end = endDate.format(formatter);

        List<categoryStat> catestat= db.getStatsByDateRange(loai,start,end);


        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for(int i= 0; i<catestat.size();i++){
            categoryStat stat = catestat.get(i);
            entries.add(new BarEntry(i,stat.getAmount()));
            labels.add(stat.getCateName());
            colors.add(stat.getColor());
        }

      BarDataSet dataSet = new BarDataSet(entries,"");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(14f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f);
// Kiểm tra null
      if (barChart == null) {
          barChart = requireView().findViewById(R.id.barChart);
          if (barChart == null) {
              return; // Không thể hiển thị biểu đồ
          }
      }

        barChart.setData(barData);
      //  barChart.setNoDataText("");
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

      XAxis xAxis = barChart.getXAxis();
      xAxis.setValueFormatter(new ValueFormatter() {
          @Override
          public String getFormattedValue(float value) {
              int index = (int) value;
              return (index >= 0 && index < labels.size()) ? labels.get(index) : "";
          }
      });
       xAxis.setGranularity(1f);
       xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
       xAxis.setDrawAxisLine(false);
       xAxis.setLabelRotationAngle(-45);
       xAxis.setTextSize(12f);

       barChart.getAxisRight().setEnabled(false);
       barChart.getAxisLeft().setTextSize(12f);
       barChart.getAxisLeft().setDrawGridLines(true);

       barChart.invalidate();

      if (adapter == null) {
          adapter = new CategoryDetailAdapter(catestat);
          rvCategoryDetails.setAdapter(adapter);
      } else {
          adapter.categoryList = catestat;
          adapter.notifyDataSetChanged();
      }

  }

    private  void updateSummary(String startDate,String endDate){
        DBHelper dbHelper = new DBHelper(getContext());

        List<categoryStat> chiTieuList =dbHelper.getStatsByDateRange("ChiTieu",startDate,endDate);
        List<categoryStat> thuNhapList= dbHelper.getStatsByDateRange("ThuNhap",startDate,endDate);

        float tongChiTieu = 0f;
        for(categoryStat item :chiTieuList){
            tongChiTieu +=item.getAmount();
        }
        float tongThuNhap =0f;
        for(categoryStat item:thuNhapList){
            tongThuNhap +=item.getAmount();
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));
        String chiFormatted = formatter.format(tongChiTieu);
        String thuFormatted = formatter.format(tongThuNhap);


        tvChi.setText("Chi Tiêu : -"+chiFormatted);
        tvThu.setText("Thu Nhập : +"+thuFormatted);
    }
}


