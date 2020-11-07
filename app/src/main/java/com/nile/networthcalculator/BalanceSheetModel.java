package com.nile.networthcalculator;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.ViewModel;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BalanceSheetModel extends ViewModel {

    private static final String TAG = "BalanceSheetModel";
    private final ContentResolver contentResolver;
    private static final String AUTHORITY = "com.nile.networthcalculator.NetWorthProvider";
    private static final Calendar calendar = Calendar.getInstance();

    // VIEW MODEL properties
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

    // Liability totals
    public double total_current_liabilities;
    public double total_long_term_liabilities;
    public double total_liabilites;

    public double net_worth = total_assets - total_liabilites;

    public Map<Float, Float> history;

    // DATABASE properties
    // Common database columns
    private static final String COLUMN_ID = "_id";

    // Balance sheet database columns
    private static final String COLUMN_NAME = "item_name";
    public static final String COLUMN_VALUE = "item_value";

    // History table database columns
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_ASSETS = "total_assets";
    private static final String COLUMN_LIABILITIES = "total_liabilities";
    private static final String COLUMN_NETWORTH = "net_worth";

    // Database URIs
    private static final Uri BALANCE_SHEET_URI = Uri.parse("content://" + AUTHORITY + "/networth/balancesheet");
    private static final Uri HISTORY_URI = Uri.parse("content://" + AUTHORITY + "/networth/history");

    public BalanceSheetModel(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
        LoadHistory();
    }

    public void resetAssets() {
        cash_entries = new double[6];
        invested_asset_entries = new double[16];
        use_asset_entries = new double[7];

        total_cash = 0;
        total_invested_assets = 0;
        total_use_assets = 0;
        total_assets = 0;
    }

    public void resetLiabilities() {
        current_liabilities = new double[3];
        long_term_liabilities = new double[7];

        total_current_liabilities  = 0;
        total_long_term_liabilities = 0;
        total_liabilites = 0;
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
        total_current_liabilities = 0;
        total_long_term_liabilities = 0;
        total_liabilites = 0;

        for (double entry : current_liabilities) {
            total_current_liabilities += entry;
        }

        for (double entry: long_term_liabilities) {
            total_long_term_liabilities += entry;
        }

        total_liabilites = total_current_liabilities + total_long_term_liabilities;
        net_worth = total_assets - total_liabilites;
    }

    public void storeAssets() {
        ContentValues[] values = new ContentValues[34];
        for (int i = 0; i < values.length; i++)
            values[i] = new ContentValues(1);
        values[0].put(COLUMN_VALUE, cash_entries[0]);
        values[1].put(COLUMN_VALUE, cash_entries[1]);
        values[2].put(COLUMN_VALUE, cash_entries[2]);
        values[3].put(COLUMN_VALUE, cash_entries[3]);
        values[4].put(COLUMN_VALUE, cash_entries[4]);
        values[5].put(COLUMN_VALUE, cash_entries[5]);
        values[6].put(COLUMN_VALUE, total_cash);
        values[7].put(COLUMN_VALUE, invested_asset_entries[0]);
        values[8].put(COLUMN_VALUE, invested_asset_entries[1]);
        values[9].put(COLUMN_VALUE, invested_asset_entries[2]);
        values[10].put(COLUMN_VALUE, invested_asset_entries[3]);
        values[11].put(COLUMN_VALUE, invested_asset_entries[4]);
        values[12].put(COLUMN_VALUE, invested_asset_entries[5]);
        values[13].put(COLUMN_VALUE, invested_asset_entries[6]);
        values[14].put(COLUMN_VALUE, invested_asset_entries[7]);
        values[15].put(COLUMN_VALUE, invested_asset_entries[8]);
        values[16].put(COLUMN_VALUE, invested_asset_entries[9]);
        values[17].put(COLUMN_VALUE, invested_asset_entries[10]);
        values[18].put(COLUMN_VALUE, invested_asset_entries[11]);
        values[19].put(COLUMN_VALUE, invested_asset_entries[12]);
        values[20].put(COLUMN_VALUE, invested_asset_entries[13]);
        values[21].put(COLUMN_VALUE, invested_asset_entries[14]);
        values[22].put(COLUMN_VALUE, invested_asset_entries[15]);
        values[23].put(COLUMN_VALUE, total_invested_assets);
        values[24].put(COLUMN_VALUE, use_asset_entries[0]);
        values[25].put(COLUMN_VALUE, use_asset_entries[1]);
        values[26].put(COLUMN_VALUE, use_asset_entries[2]);
        values[27].put(COLUMN_VALUE, use_asset_entries[3]);
        values[28].put(COLUMN_VALUE, use_asset_entries[4]);
        values[29].put(COLUMN_VALUE, use_asset_entries[5]);
        values[30].put(COLUMN_VALUE, use_asset_entries[6]);
        values[31].put(COLUMN_VALUE, total_use_assets);
        values[32].put(COLUMN_VALUE, total_assets);
        values[33].put(COLUMN_VALUE, net_worth);

        contentResolver.update(BALANCE_SHEET_URI, values[0], COLUMN_NAME + " = ?", new String[]{"checking_accounts"});
        contentResolver.update(BALANCE_SHEET_URI, values[1], COLUMN_NAME + " = ?", new String[]{"savings_accounts"});
        contentResolver.update(BALANCE_SHEET_URI, values[2], COLUMN_NAME + " = ?", new String[]{"money_market_accounts"});
        contentResolver.update(BALANCE_SHEET_URI, values[3], COLUMN_NAME + " = ?", new String[]{"savings_bonds"});
        contentResolver.update(BALANCE_SHEET_URI, values[4], COLUMN_NAME + " = ?", new String[]{"cds"});
        contentResolver.update(BALANCE_SHEET_URI, values[5], COLUMN_NAME + " = ?", new String[]{"cash_value_life_insurance"});
        contentResolver.update(BALANCE_SHEET_URI, values[6], COLUMN_NAME + " = ?", new String[]{"total_cash"});
        contentResolver.update(BALANCE_SHEET_URI, values[7], COLUMN_NAME + " = ?", new String[]{"brokerage"});
        contentResolver.update(BALANCE_SHEET_URI, values[8], COLUMN_NAME + " = ?", new String[]{"other_taxable"});
        contentResolver.update(BALANCE_SHEET_URI, values[9], COLUMN_NAME + " = ?", new String[]{"ira"});
        contentResolver.update(BALANCE_SHEET_URI, values[10], COLUMN_NAME + " = ?", new String[]{"roth_ira"});
        contentResolver.update(BALANCE_SHEET_URI, values[11], COLUMN_NAME + " = ?", new String[]{"401k"});
        contentResolver.update(BALANCE_SHEET_URI, values[12], COLUMN_NAME + " = ?", new String[]{"sep_ira"});
        contentResolver.update(BALANCE_SHEET_URI, values[13], COLUMN_NAME + " = ?", new String[]{"keogh"});
        contentResolver.update(BALANCE_SHEET_URI, values[14], COLUMN_NAME + " = ?", new String[]{"pension"});
        contentResolver.update(BALANCE_SHEET_URI, values[15], COLUMN_NAME + " = ?", new String[]{"annuity"});
        contentResolver.update(BALANCE_SHEET_URI, values[16], COLUMN_NAME + " = ?", new String[]{"real_estate"});
        contentResolver.update(BALANCE_SHEET_URI, values[17], COLUMN_NAME + " = ?", new String[]{"sole_propietorship"});
        contentResolver.update(BALANCE_SHEET_URI, values[18], COLUMN_NAME + " = ?", new String[]{"partnership"});
        contentResolver.update(BALANCE_SHEET_URI, values[19], COLUMN_NAME + " = ?", new String[]{"ccorporation"});
        contentResolver.update(BALANCE_SHEET_URI, values[20], COLUMN_NAME + " = ?", new String[]{"scorporation"});
        contentResolver.update(BALANCE_SHEET_URI, values[21], COLUMN_NAME + " = ?", new String[]{"llc"});
        contentResolver.update(BALANCE_SHEET_URI, values[22], COLUMN_NAME + " = ?", new String[]{"other_business_interests"});
        contentResolver.update(BALANCE_SHEET_URI, values[23], COLUMN_NAME + " = ?", new String[]{"total_invested_assets"});
        contentResolver.update(BALANCE_SHEET_URI, values[24], COLUMN_NAME + " = ?", new String[]{"principal_home"});
        contentResolver.update(BALANCE_SHEET_URI, values[25], COLUMN_NAME + " = ?", new String[]{"vacation_home"});
        contentResolver.update(BALANCE_SHEET_URI, values[26], COLUMN_NAME + " = ?", new String[]{"cars_trucks_boats"});
        contentResolver.update(BALANCE_SHEET_URI, values[27], COLUMN_NAME + " = ?", new String[]{"home_furnishings"});
        contentResolver.update(BALANCE_SHEET_URI, values[28], COLUMN_NAME + " = ?", new String[]{"art"});
        contentResolver.update(BALANCE_SHEET_URI, values[29], COLUMN_NAME + " = ?", new String[]{"jewelry_furs"});
        contentResolver.update(BALANCE_SHEET_URI, values[30], COLUMN_NAME + " = ?", new String[]{"other_use_assets"});
        contentResolver.update(BALANCE_SHEET_URI, values[31], COLUMN_NAME + " = ?", new String[]{"total_use_assets"});
        contentResolver.update(BALANCE_SHEET_URI, values[32], COLUMN_NAME + " = ?", new String[]{"total_assets"});
        contentResolver.update(BALANCE_SHEET_URI, values[33], COLUMN_NAME + " = ?", new String[]{"net_worth"});

        updateHistory();
    }

    public void storeLiabilities() {
        ContentValues[] values = new ContentValues[12];
        for (int i = 0; i < values.length; i++)
            values[i] = new ContentValues(1);
        values[0].put(COLUMN_VALUE, current_liabilities[0]);
        values[1].put(COLUMN_VALUE, current_liabilities[1]);
        values[2].put(COLUMN_VALUE, current_liabilities[2]);
        values[3].put(COLUMN_VALUE, long_term_liabilities[0]);
        values[4].put(COLUMN_VALUE, long_term_liabilities[1]);
        values[5].put(COLUMN_VALUE, long_term_liabilities[2]);
        values[6].put(COLUMN_VALUE, long_term_liabilities[3]);
        values[7].put(COLUMN_VALUE, long_term_liabilities[4]);
        values[8].put(COLUMN_VALUE, long_term_liabilities[5]);
        values[9].put(COLUMN_VALUE, long_term_liabilities[6]);
        values[10].put(COLUMN_VALUE, total_liabilites);
        values[11].put(COLUMN_VALUE, net_worth);

        contentResolver.update(BALANCE_SHEET_URI, values[0], COLUMN_NAME + " = ?", new String[]{"credit_card_balances"});
        contentResolver.update(BALANCE_SHEET_URI, values[1], COLUMN_NAME + " = ?", new String[]{"income_tax_owed"});
        contentResolver.update(BALANCE_SHEET_URI, values[2], COLUMN_NAME + " = ?", new String[]{"other_bills"});
        contentResolver.update(BALANCE_SHEET_URI, values[3], COLUMN_NAME + " = ?", new String[]{"home_mortgage"});
        contentResolver.update(BALANCE_SHEET_URI, values[4], COLUMN_NAME + " = ?", new String[]{"home_equity_loan"});
        contentResolver.update(BALANCE_SHEET_URI, values[5], COLUMN_NAME + " = ?", new String[]{"mortgages_on_rentals"});
        contentResolver.update(BALANCE_SHEET_URI, values[6], COLUMN_NAME + " = ?", new String[]{"car_loans"});
        contentResolver.update(BALANCE_SHEET_URI, values[7], COLUMN_NAME + " = ?", new String[]{"student_loans"});
        contentResolver.update(BALANCE_SHEET_URI, values[8], COLUMN_NAME + " = ?", new String[]{"life_insurance_policy_loans"});
        contentResolver.update(BALANCE_SHEET_URI, values[9], COLUMN_NAME + " = ?", new String[]{"other_long_term_debt"});
        contentResolver.update(BALANCE_SHEET_URI, values[10], COLUMN_NAME + " = ?", new String[]{"total_liabilities"});
        contentResolver.update(BALANCE_SHEET_URI, values[11], COLUMN_NAME + " = ?", new String[]{"net_worth"});

        updateHistory();
    }

    @SuppressLint("DefaultLocale")
    public void updateHistory() {
        float date = calendar.get(Calendar.YEAR) + calendar.get(Calendar.MONTH) / 12f;
        ContentValues values = new ContentValues(4);
        values.put(COLUMN_NETWORTH, net_worth);
        values.put(COLUMN_ASSETS, total_assets);
        values.put(COLUMN_LIABILITIES, total_liabilites);
        if (history.containsKey(date)) {
            contentResolver.update(HISTORY_URI, values, COLUMN_DATE + " = ?", new String[]{String.format("%f", date)});
        } else {
            values.put(COLUMN_DATE, date);
            contentResolver.insert(HISTORY_URI, values);
        }
        history.put(date, (float)net_worth);
    }

    // TODO: Think of potential future optimisations. Must I load the entire history? What are the costs?
    private void LoadHistory() {
        // Called during initialisation of the view model
        history = new HashMap<Float, Float>();
        Log.d(TAG, String.format("%d", calendar.get(Calendar.MONTH)));
        Cursor c = contentResolver.query(HISTORY_URI, null, null, null, null);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            history.put(c.getFloat(1), c.getFloat(4)); c.moveToNext();
        }
        c.close();
    }

}
