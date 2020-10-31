package com.nile.networthcalculator.ui.summary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nile.networthcalculator.BalanceSheetModel;
import com.nile.networthcalculator.R;

import java.util.ArrayList;
import java.util.List;

public class SummaryFragment extends Fragment {

    // TODO: Include breakdown of asset and liability proportions such as cash, invested assets, etc.
    // TODO: Scrap pie chart of assets and liabilities as it is not informative. Maybe use bars

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_summary, container, false);
        BalanceSheetModel viewModel = ViewModelProviders.of(getActivity()).get(BalanceSheetModel.class);

        // BAR CHART
        HorizontalBarChart barChart = root.findViewById(R.id.networth_chart);
        ArrayList <BarEntry> barValues = new ArrayList<>();
        if (viewModel.total_assets == 0 && viewModel.total_liabilites == 0) {
            barValues.add(new BarEntry(0, 10f));
            barValues.add(new BarEntry(1, 20f));

        } else {
            barValues.add(new BarEntry(0, (float)viewModel.total_assets));
            barValues.add(new BarEntry(1, new float[]{(float)viewModel.total_liabilites}));
        }
        BarDataSet set1 = new BarDataSet(barValues, "Breakdown");
        set1.setColors(ColorTemplate.LIBERTY_COLORS);
        BarData barData = new BarData(set1);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
//        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        barChart.animateY(2000);

        // ASSET PIE
        PieChart assetPie = root.findViewById(R.id.asset_pie);
        assetPie.setUsePercentValues(true);

        List<PieEntry> assetPieValues = new ArrayList<>();

        if (viewModel.total_assets == 0) {
            assetPieValues.add(new PieEntry(33.3f, "CASH"));
            assetPieValues.add(new PieEntry(33.3f, "INVESTMENTS"));
            assetPieValues.add(new PieEntry(33.3f, "USE ASSETS"));
        } else {
            assetPieValues.add(new PieEntry((float)viewModel.total_cash, "CASH"));
            assetPieValues.add(new PieEntry((float)viewModel.total_invested_assets, "INVESTMENTS"));
            assetPieValues.add(new PieEntry((float)viewModel.total_use_assets, "USE ASSETS"));
        }

        PieDataSet assetDataSet = new PieDataSet(assetPieValues, "Quantities");
        PieData assetData = new PieData(assetDataSet);
        assetDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        assetPie.setData(assetData);

        assetPie.animateXY(1400, 1400);

        // LIABILITY PIE
        PieChart liabilityPie = root.findViewById(R.id.liability_pie);
        assetPie.setUsePercentValues(true);

        List<PieEntry> liabilityPieValues = new ArrayList<>();

        if (viewModel.total_liabilites == 0) {
            liabilityPieValues.add(new PieEntry(50f, "CURRENT"));
            liabilityPieValues.add(new PieEntry(50f, "LONG-TERM"));

        } else {
            liabilityPieValues.add(new PieEntry((float)viewModel.total_current_liabilities, "CURRENT"));
            liabilityPieValues.add(new PieEntry((float)viewModel.total_long_term_liabilities, "LONG-TERM"));
        }

        PieDataSet liabilityDataSet = new PieDataSet(liabilityPieValues, "Quantities");
        PieData liabilityData = new PieData(liabilityDataSet);
        liabilityDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        liabilityPie.setData(liabilityData);

        liabilityPie.animateXY(1400, 1400);
        return root;
    }
}