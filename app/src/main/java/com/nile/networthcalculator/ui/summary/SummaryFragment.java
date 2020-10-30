package com.nile.networthcalculator.ui.summary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nile.networthcalculator.BalanceSheetModel;
import com.nile.networthcalculator.R;

import java.util.ArrayList;
import java.util.List;

public class SummaryFragment extends Fragment {

    // TODO: Include breakdown of asset and liablity proportions such as cash, invested assets, etc.
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

        PieChart chart = root.findViewById(R.id.chart);
        chart.setUsePercentValues(true);

        List<PieEntry> value = new ArrayList<>();

        if (viewModel.total_assets == 0 && viewModel.total_liabilites == 0) {
            value.add(new PieEntry(50f, "ASSETS"));
            value.add(new PieEntry(50f, "LIABILITIES"));
        } else {
            value.add(new PieEntry((float)viewModel.total_assets, "ASSETS"));
            value.add(new PieEntry((float)viewModel.total_liabilites, "LIABILITIES"));
        }

        PieDataSet dataSet = new PieDataSet(value, "Quantities");
        PieData data = new PieData(dataSet);
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        chart.setData(data);

        chart.animateXY(1400, 1400);

        return root;
    }
}