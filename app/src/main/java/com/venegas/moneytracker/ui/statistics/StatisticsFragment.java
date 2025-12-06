package com.venegas.moneytracker.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.chip.Chip;
import com.venegas.moneytracker.R;
import com.venegas.moneytracker.data.local.dao.CategorySum;
import com.venegas.moneytracker.utils.Constants;
import com.venegas.moneytracker.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private StatisticsViewModel viewModel;

    // Views
    private TextView tvTotalCount, tvAverageAmount;
    private PieChart pieChart;
    private RecyclerView rvCategories;
    private LinearLayout layoutEmptyStats;
    private Chip chipThisMonth, chipLastMonth;

    private CategoryStatAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupPieChart();
        setupObservers();
        setupListeners();
    }

    private void initViews(View view) {
        tvTotalCount = view.findViewById(R.id.tv_total_count);
        tvAverageAmount = view.findViewById(R.id.tv_average_amount);
        pieChart = view.findViewById(R.id.pie_chart);
        rvCategories = view.findViewById(R.id.rv_categories);
        layoutEmptyStats = view.findViewById(R.id.layout_empty_stats);
        chipThisMonth = view.findViewById(R.id.chip_this_month);
        chipLastMonth = view.findViewById(R.id.chip_last_month);
    }

    private void setupRecyclerView() {
        adapter = new CategoryStatAdapter(new ArrayList<>(), viewModel.getCurrency());
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategories.setAdapter(adapter);
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setEnabled(true);
    }

    private void setupObservers() {
        // Gastos por categoría
        viewModel.getExpensesByCategory().observe(getViewLifecycleOwner(), categorySums -> {
            if (categorySums != null && !categorySums.isEmpty()) {
                updatePieChart(categorySums);
                adapter.updateCategories(categorySums);

                // Calcular total de transacciones
                tvTotalCount.setText(String.valueOf(categorySums.size()));

                layoutEmptyStats.setVisibility(View.GONE);
            } else {
                layoutEmptyStats.setVisibility(View.VISIBLE);
                pieChart.clear();
            }
        });

        // Promedio diario
        viewModel.getDailyAverage().observe(getViewLifecycleOwner(), average -> {
            String formatted = CurrencyUtils.formatAmount(average, viewModel.getCurrency());
            tvAverageAmount.setText(formatted);
        });
    }

    private void updatePieChart(List<CategorySum> categorySums) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // Calcular total para porcentajes
        double total = 0;
        for (CategorySum categorySum : categorySums) {
            total += categorySum.total;
        }

        // Crear entradas del gráfico
        for (CategorySum categorySum : categorySums) {
            float percentage = (float) ((categorySum.total / total) * 100);
            entries.add(new PieEntry(percentage, categorySum.category));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Constants.CHART_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));

        pieChart.setData(data);
        pieChart.invalidate(); // Refresh
        pieChart.animateY(1000);
    }

    private void setupListeners() {
        chipThisMonth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.setPeriod("THIS_MONTH");
            }
        });

        chipLastMonth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.setPeriod("LAST_MONTH");
            }
        });
    }
}