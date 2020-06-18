package com.nile.networthcalculator.ui.history;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nile.networthcalculator.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        LineChart chart = root.findViewById(R.id.line_chart);
        chart.setDoubleTapToZoomEnabled(true);


        List<Entry> value = new ArrayList<>();
        value.add(new Entry(30f,30f));
        value.add(new Entry(40f, 40f));
        LineDataSet dataSet = new LineDataSet(value, "Historical net worth");
        LineData data = new LineData(dataSet);
        chart.setData(data);
        chart.setBackgroundColor(0xFFF2F2F2);
        chart.animateXY(1400, 1400);
        return root;
    }
}