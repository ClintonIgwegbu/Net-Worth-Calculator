package com.nile.networthcalculator;

import android.content.ContentResolver;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BalanceSheetModel extends ViewModel {
    String TAG = "BalanceSheetModel";

    // Asset tables
    public double[] cash_entries = new double[6];
    public double[] invested_asset_entries = new double[16];
    public double[] use_asset_entries = new double[7];

    // Liability tables
    public double[] current_liabilities = new double[3];
    public double[] long_term_liabilities = new double[7];

    // Asset totals
    public double total_cash;
    public double total_invested_assets;
    public double total_use_assets;
    public double total_assets;

    // Total liabilities
    public double total_liabilites;

    public double net_worth = total_assets - total_liabilites;

    // History
    public Map<Float, Integer> history;

    public BalanceSheetModel() {
        LoadHistory();
    }

    public void updateAssets() {
        total_cash = 0;
        total_invested_assets = 0;
        total_use_assets = 0;
        total_assets = 0;

        for (double entry : cash_entries) {
            total_cash += entry;
        }

        for (double entry : invested_asset_entries) {
            total_invested_assets += entry;
        }

        for (double entry : use_asset_entries) {
            total_use_assets += entry;
        }

        total_assets = total_cash + total_invested_assets + total_use_assets;
        net_worth = total_assets - total_liabilites;
    }

    public void updateLiabilities() {
        total_liabilites = 0;

        for (double entry : current_liabilities) {
            total_liabilites += entry;
        }

        for (double entry: long_term_liabilities) {
            total_liabilites += entry;
        }

        net_worth = total_assets - total_liabilites;
    }

    private void LoadHistory() {
        history = new HashMap<Float, Integer>();
        Calendar calendar = Calendar.getInstance();
//        history.put(calendar.get(Calendar.YEAR) + (calendar.get(Calendar.DAY_OF_YEAR) - 1) / 365f, 23);
//        history.put(calendar.get(Calendar.YEAR) + (calendar.get(Calendar.DAY_OF_YEAR) - 1 + 5) / 365f, 67);
        history.put(calendar.get(Calendar.YEAR) + calendar.get(Calendar.MONTH) / 12f, 23);
        history.put(calendar.get(Calendar.YEAR) + (calendar.get(Calendar.MONTH) + 3) / 12f, 67);
        Log.d(TAG, String.format("%d", calendar.get(Calendar.MONTH)));
    }
//    public BalanceSheetModel() {
//        // trigger asset load.
          // Perhaps this could load data from a database if necessary
//    }
}
