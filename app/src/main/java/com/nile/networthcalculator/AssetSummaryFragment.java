package com.nile.networthcalculator;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.List;

public class AssetSummaryFragment extends Fragment {

    String TAG = "Asset summary fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(TAG, (String) getActivity().getTitle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_asset_summary, container, false);
        final BalanceSheetModel viewModel = ViewModelProviders.of(getActivity()).get(BalanceSheetModel.class);

        // ASSET PIE
        PieChart assetPie = root.findViewById(R.id.asset_pie);
        assetPie.setDrawEntryLabels(false);
        assetPie.setCenterText(String.format("Total assets: $%.2f", viewModel.total_assets));
        assetPie.getDescription().setEnabled(false);
        List<PieEntry> assetPieValues = new ArrayList<>();

        assetPieValues.add(new PieEntry((float)viewModel.total_cash, "CASH"));
        assetPieValues.add(new PieEntry((float)viewModel.total_invested_assets, "INVESTMENTS"));
        assetPieValues.add(new PieEntry((float)viewModel.total_use_assets, "USE ASSETS"));

        PieDataSet assetDataSet = new PieDataSet(assetPieValues, "Quantities");
        PieData assetData = new PieData(assetDataSet);
        assetData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if (value != 0) {
                    return String.format("$%.2f (%.0f%%)", value, 100 * value / viewModel.total_assets);
                }
                return "";
            }
        });
        assetDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        assetPie.setData(assetData);

        assetPie.animateXY(1400, 1400);

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG,"Back Button Pressed");
        getActivity().onBackPressed();
        return true;
    }
}