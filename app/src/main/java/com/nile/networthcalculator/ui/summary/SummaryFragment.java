package com.nile.networthcalculator.ui.summary;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.nile.networthcalculator.BalanceSheetModel;
import com.nile.networthcalculator.R;
import java.util.ArrayList;

public class SummaryFragment extends Fragment {

    String TAG = "Summary fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_summary, container, false);
        final BalanceSheetModel viewModel = ViewModelProviders.of(getActivity()).get(BalanceSheetModel.class);

        // BAR CHART
        final BarChart barChart = root.findViewById(R.id.networth_chart);
        ArrayList <BarEntry> barValues = new ArrayList<>();
        if (viewModel.total_assets == 0 && viewModel.total_liabilites == 0) {
            barValues.add(new BarEntry(0, 10f));
            barValues.add(new BarEntry(1, 20f));
            barValues.add(new BarEntry(2, -10f));
        } else {
            barValues.add(new BarEntry(0, (float)viewModel.total_assets));
            barValues.add(new BarEntry(1, (float)viewModel.total_liabilites));
            barValues.add(new BarEntry(2, (float)viewModel.net_worth));
        }

        BarDataSet set1 = new BarDataSet(barValues, "Net worth breakdown (USD)");
        int[] colors = new int[]{Color.argb(255, 94, 198, 147), Color.argb(255, 172, 25, 44), Color.argb(255, 148, 215, 214)};
        set1.setColors(colors);
        BarData barData = new BarData(set1);
        barChart.setData(barData);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(2000);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"ASSETS", "LIABILITIES", "NET WORTH"}));
        xAxis.setGranularity(1f);
        barChart.invalidate();
        final Context context = barChart.getContext();

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e.getX() == 0 && viewModel.total_assets > 0) {  // ASSETS
                    NavDirections action =
                            SummaryFragmentDirections
                                    .actionNavigationSummaryToAssetSummaryFragment();
                    Navigation.findNavController(barChart).navigate(action);
                } else if (e.getX() == 1 && viewModel.total_liabilites > 0) {  // LIABILITIES
                    NavDirections action =
                            SummaryFragmentDirections
                                .actionNavigationSummaryToLiabilitySummaryFragment();
                    Navigation.findNavController(barChart).navigate(action);
                }
            }

            @Override
            public void onNothingSelected() {}
        });

        return root;
    }
}