package com.nile.networthcalculator.ui.summary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nile.networthcalculator.R;

import java.util.ArrayList;
import java.util.List;

public class SummaryFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_summary, container, false);

        PieChart chart = root.findViewById(R.id.chart);
        chart.setUsePercentValues(true);

        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(40f, "ASSETS"));
        value.add(new PieEntry(60f, "LIABILITIES"));

        PieDataSet dataSet = new PieDataSet(value, "Quantities");
        PieData data = new PieData(dataSet);
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        chart.setData(data);

        chart.animateXY(1400, 1400);

        return root;
    }
}