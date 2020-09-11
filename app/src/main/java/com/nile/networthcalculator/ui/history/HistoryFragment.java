package com.nile.networthcalculator.ui.history;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
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
import com.github.mikephil.charting.components.YAxis;
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

    private class MyValueFormatter implements IValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return "$" + value;
        }
    }

    private class MyAxisValueFormatter implements IAxisValueFormatter {
        HashMap<Integer, String> intToMonth = createIntToMonth();

        private HashMap<Integer, String> createIntToMonth() {
           HashMap<Integer, String> intToMonth = new HashMap<Integer, String>();
           intToMonth.put(1, "Jan"); intToMonth.put(2, "Feb"); intToMonth.put(3, "Mar");
           intToMonth.put(4, "Apr"); intToMonth.put(5, "May"); intToMonth.put(6, "Jun");
           intToMonth.put(7, "Jul"); intToMonth.put(8, "Aug"); intToMonth.put(9, "Sep");
           intToMonth.put(10, "Oct"); intToMonth.put(11, "Nov"); intToMonth.put(12, "Dec");
           return intToMonth;
        }

        private HashMap<Integer, Integer> createMonthToDays(boolean leapYear) {
            HashMap<Integer, Integer> monthToDays = new HashMap<Integer, Integer>();
            monthToDays.put(1, 0); monthToDays.put(2, 31);
            if (leapYear) {
                monthToDays.put(3, 59);
                monthToDays.put(4, 90); monthToDays.put(5, 120); monthToDays.put(6, 151);
                monthToDays.put(7, 181); monthToDays.put(8, 212); monthToDays.put(9, 243);
                monthToDays.put(10, 273); monthToDays.put(11, 304); monthToDays.put(12, 334);
            } else {
                monthToDays.put(3, 60);
                monthToDays.put(4, 91); monthToDays.put(5, 121); monthToDays.put(6, 152);
                monthToDays.put(7, 182); monthToDays.put(8, 213); monthToDays.put(9, 244);
                monthToDays.put(10, 274); monthToDays.put(11, 305); monthToDays.put(12, 335);
            }
            return monthToDays;
        }

        private int getMonth(int days, HashMap<Integer, Integer> monthToDays) {
            int left = 1;
            int right = 12;
            int mid = 1;

            while (left < right) {
                mid = (int)Math.ceil(left + (right - left) / 2.0);
                if (monthToDays.get(mid) == days)
                    return mid;
                else if (monthToDays.get(mid) > days)
                    right = mid - 1;
                else
                    left = mid;
            }

            return left;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int year = (int)value;
            boolean leapYear = year % 4 == 0;
            int days = (int)Math.ceil((value - (int)value) * 365.0);
            if (leapYear)
                days = (int)Math.ceil((value - (int)value) * 366.0);
            HashMap<Integer, Integer> monthToDays = createMonthToDays(leapYear);
            int month = getMonth(days, monthToDays);
//            return intToMonth.get(month) + " " + (days - monthToDays.get(month)) + " " + year % 100;
            return intToMonth.get(month) + " " + (days - monthToDays.get(month));

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



