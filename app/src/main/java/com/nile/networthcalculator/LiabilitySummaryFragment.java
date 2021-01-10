package com.nile.networthcalculator;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class LiabilitySummaryFragment extends Fragment {

    // TODO: Get back button to SummaryFragment working

    String TAG = "Liability summary fragment";;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_liability_summary, container, false);
        final BalanceSheetModel viewModel = ViewModelProviders.of(getActivity()).get(BalanceSheetModel.class);

        // LIABILITY PIE
        PieChart liabilityPie = root.findViewById(R.id.liability_pie);
        liabilityPie.setDrawEntryLabels(false);
        liabilityPie.setCenterText(String.format("Total liabilities: $%.2f", viewModel.total_liabilites));
        liabilityPie.getDescription().setEnabled(false);
        List<PieEntry> liabilityPieValues = new ArrayList<>();

        liabilityPieValues.add(new PieEntry((float)viewModel.total_current_liabilities, "CURRENT"));
        liabilityPieValues.add(new PieEntry((float)viewModel.total_long_term_liabilities, "LONG-TERM"));

        PieDataSet liabilityDataSet = new PieDataSet(liabilityPieValues, "Quantities");
        PieData liabilityData = new PieData(liabilityDataSet);
        liabilityData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if (value != 0) {
                    return String.format("$%.2f (%.0f%%)", value, 100 * value / viewModel.total_liabilites);
                }
                return "";
            }
        });
        liabilityDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        liabilityPie.setData(liabilityData);

        liabilityPie.animateXY(1400, 1400);

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().onBackPressed();
        return true;
    }
}