package com.nile.networthcalculator.ui.history;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.nile.networthcalculator.BalanceSheetModel;
import com.nile.networthcalculator.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class HistoryFragment extends Fragment {
    String TAG = "HistoryFragment";

    private static class MyValueFormatter implements IValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return "$" + value;
        }
    }

    private static class MyAxisValueFormatter implements IAxisValueFormatter {
        HashMap<Integer, String> intToMonth = createIntToMonth();

        private HashMap<Integer, String> createIntToMonth() {
           HashMap<Integer, String> intToMonth = new HashMap<Integer, String>();
           intToMonth.put(1, "Jan"); intToMonth.put(2, "Feb"); intToMonth.put(3, "Mar");
           intToMonth.put(4, "Apr"); intToMonth.put(5, "May"); intToMonth.put(6, "Jun");
           intToMonth.put(7, "Jul"); intToMonth.put(8, "Aug"); intToMonth.put(9, "Sep");
           intToMonth.put(10, "Oct"); intToMonth.put(11, "Nov"); intToMonth.put(12, "Dec");
           return intToMonth;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int month = (int)(12 * (value - (int)value));
            int year = (int)value % 100;
            return intToMonth.get(month + 1) + " " + year;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        BalanceSheetModel viewModel = ViewModelProviders.of(getActivity()).get(BalanceSheetModel.class);

        LineChart chart = root.findViewById(R.id.line_chart);
        chart.setDoubleTapToZoomEnabled(true);

        List<Entry> value = new ArrayList<>();

        for (float key : viewModel.history.keySet()) {
            value.add(new Entry(key, viewModel.history.get(key)));
        }
        LineDataSet dataSet = new LineDataSet(value, "Historical net worth");
        LineData data = new LineData(dataSet);
        chart.setData(data);
        chart.setBackgroundColor(0xFFF2F2F2);
        chart.animateXY(1400, 1400);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new MyAxisValueFormatter());
        return root;
    }

}



