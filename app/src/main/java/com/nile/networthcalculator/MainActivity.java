package com.nile.networthcalculator;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_summary, R.id.navigation_asset, R.id.navigation_liability,
                R.id.navigation_history)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        final BalanceSheetModel viewModel = ViewModelProviders.of(this).get(BalanceSheetModel.class);
        loadBalanceSheet(viewModel);
        // Load most recent user data into viewModel

//        final AssetModel viewModel = ViewModelProviders.of(this).get(AssetModel.class);
//        viewModel.userLiveData.observer(this, new Observer() {
//            @Override
//            public void onChanged(@Nullable User data) {
//                // update ui.
//            }
//        });
//        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewModel.doAction();
//            }
//        });

    }

    private void loadBalanceSheet(BalanceSheetModel viewModel) {
        // This method is only called once when app is started up
        ContentResolver contentResolver = getContentResolver();
        final String AUTHORITY = "com.nile.networthcalculator.NetWorthProvider";
        final Uri BALANCE_SHEET_URI = Uri.parse("content://" + AUTHORITY + "/networth/balancesheet");
        // Balance sheet columns
        String COLUMN_ID = "_id";
        String COLUMN_NAME = "item_name";
        String COLUMN_VALUE = "item_value";
        // TODO: Query database for most recent balance sheet
        // NOTE: This code assumes that the order of items on the balance sheet is maintained
        if (viewModel.total_assets == 0 && viewModel.total_liabilites == 0) {
            Cursor c = contentResolver.query(BALANCE_SHEET_URI, new String[]{COLUMN_VALUE}, null, null, null);
            c.moveToFirst();
            Log.d(TAG, String.format("Cursor count is %d", c.getCount()));
            for (int i = 0; i < viewModel.cash_entries.length; i++) {
                viewModel.cash_entries[i] = c.getDouble(2);
                c.moveToNext();
            }
            for (int i = 0; i < viewModel.invested_asset_entries.length; i++) {
                viewModel.invested_asset_entries[i] = c.getDouble(2);
                c.moveToNext();
            }
            for (int i = 0; i < viewModel.use_asset_entries.length; i++) {
                viewModel.use_asset_entries[i] = c.getDouble(2);
                c.moveToNext();
            }

            viewModel.total_cash = c.getDouble(2); c.moveToNext();
            viewModel.total_invested_assets = c.getDouble(2); c.moveToNext();
            viewModel.total_use_assets = c.getDouble(2); c.moveToNext();
            viewModel.total_assets = c.getDouble(2); c.moveToNext();

            for (int i = 0; i < viewModel.current_liabilities.length; i++) {
                viewModel.current_liabilities[i] = c.getDouble(2);
                c.moveToNext();
            }
            for (int i = 0; i < viewModel.long_term_liabilities.length; i++) {
                viewModel.long_term_liabilities[i] = c.getDouble(2);
                c.moveToNext();
            }

            viewModel.total_liabilites = c.getDouble(2); c.moveToNext();
            viewModel.net_worth = c.getDouble(2);
            c.close();
        }
    }

}