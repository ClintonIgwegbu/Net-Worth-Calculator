package com.nile.networthcalculator.ui.assets;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.nile.networthcalculator.BalanceSheetModel;
import com.nile.networthcalculator.R;

import java.text.NumberFormat;

import static com.nile.networthcalculator.util.Utilities.parseDouble;

public class AssetFragment extends Fragment {

    // Cash table entries and total
    EditText txtCheckingAccounts;
    EditText txtSavingsAccounts;
    EditText txtMoneyMarketAccounts;
    EditText txtSavingsBonds;
    EditText txtCds;
    EditText txtCashValueLifeInsurance;
    TextView txtTotalCash;

    // Invested asset table entries and total
    EditText txtBrokerage;
    EditText txtOtherTaxable;
    EditText txtIRA;
    EditText txtRothIRA;
    EditText txt401k;
    EditText txtSEPIRA;
    EditText txtKeogh;
    EditText txtPension;
    EditText txtAnnuity;
    EditText txtRealEstate;
    EditText txtSolePropietorship;
    EditText txtPartnership;
    EditText txtCCorporation;
    EditText txtSCorporation;
    EditText txtLLC;
    EditText txtOtherBusinessInterests;
    TextView txtTotalInvestedAssets;

    // Use asset table entries and total
    EditText txtPrincipalHome;
    EditText txtVacationHome;
    EditText txtCarsTrucksBoats;
    EditText txtHomeFurnishings;
    EditText txtArt;
    EditText txtJewelryFurs;
    EditText txtOtherUseAssets;
    TextView txtTotalUseAssets;

    // Total asset table entry
    TextView txtTotalAssets;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Something happening", "onCreateView: ");

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_asset, container, false);

        final BalanceSheetModel viewModel = ViewModelProviders.of(getActivity()).get(BalanceSheetModel.class);

        final View cashTable = root.findViewById(R.id.cash_table);
        final View investedAssetsTable = root.findViewById(R.id.invested_assets_table);
        final View useAssetsTable = root.findViewById(R.id.use_assets_table);
        Button calculateButton = root.findViewById(R.id.calculate_button);

        fetchTableReferences(root, cashTable, investedAssetsTable, useAssetsTable);
        populateTables(viewModel);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantities(viewModel);
//                viewModel.storeTableInDatabase();
                storeTableInDatabase(viewModel);
            }
        });

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Something happening", "onPause: here! ");
        BalanceSheetModel viewModel = ViewModelProviders.of(getActivity()).get(BalanceSheetModel.class);
        saveTableEntries(viewModel);  // Persist table data
    }

    public void fetchTableReferences(View root, View cashTable, View investedAssetsTable, View useAssetsTable) {
        // Cash table references
        txtCheckingAccounts = cashTable.findViewById(R.id.checking_accounts);
        txtSavingsAccounts = cashTable.findViewById(R.id.savings_accounts);
        txtMoneyMarketAccounts = cashTable.findViewById(R.id.money_market_accounts);
        txtSavingsBonds = cashTable.findViewById(R.id.savings_bonds);
        txtCds = cashTable.findViewById(R.id.cds);
        txtCashValueLifeInsurance = cashTable.findViewById(R.id.cash_value_of_life_insurance);

        // Invested asset table references
        txtBrokerage = investedAssetsTable.findViewById(R.id.brokerage);
        txtOtherTaxable = investedAssetsTable.findViewById(R.id.other_taxable);
        txtIRA = investedAssetsTable.findViewById(R.id.ira);
        txtRothIRA = investedAssetsTable.findViewById(R.id.roth_ira);
        txt401k = investedAssetsTable.findViewById(R.id._401_k_or_403_b);
        txtSEPIRA = investedAssetsTable.findViewById(R.id.sep_ira);
        txtKeogh = investedAssetsTable.findViewById(R.id.keogh);
        txtPension = investedAssetsTable.findViewById(R.id.pension_vested_benefit);
        txtAnnuity = investedAssetsTable.findViewById(R.id.annuity_accumulated_value);
        txtRealEstate = investedAssetsTable.findViewById(R.id.real_estate_rental_property_or_land);
        txtSolePropietorship = investedAssetsTable.findViewById(R.id.sole_propietorship);
        txtPartnership = investedAssetsTable.findViewById(R.id.partnership);
        txtCCorporation = investedAssetsTable.findViewById(R.id.c_corporation);
        txtSCorporation = investedAssetsTable.findViewById(R.id.s_corporation);
        txtLLC = investedAssetsTable.findViewById(R.id.limited_liability_company);
        txtOtherBusinessInterests = investedAssetsTable.findViewById(R.id.other_business_interests);

        // Use asset table references
        txtPrincipalHome = useAssetsTable.findViewById(R.id.principal_home);
        txtVacationHome = useAssetsTable.findViewById(R.id.vacation_home);
        txtCarsTrucksBoats = useAssetsTable.findViewById(R.id.cars_trucks_boats);
        txtHomeFurnishings = useAssetsTable.findViewById(R.id.home_furnishings);
        txtArt = useAssetsTable.findViewById(R.id.art_antiques_coins_collectibles);
        txtJewelryFurs = useAssetsTable.findViewById(R.id.jewelry_furs);
        txtOtherUseAssets = useAssetsTable.findViewById(R.id.other_use_assets);

        // Table totals
        txtTotalCash = cashTable.findViewById(R.id.total_cash);
        txtTotalInvestedAssets = investedAssetsTable.findViewById(R.id.total_invested_assets);
        txtTotalUseAssets = useAssetsTable.findViewById(R.id.total_use_assets);

        // Overall total
        txtTotalAssets = root.findViewById(R.id.total_assets);
    }

    public void storeTableInDatabase(BalanceSheetModel viewModel) {
        // I'm not sure that this stuff should be done in the asset fragment
        // Maybe it should be done in the viewmodel if possible
        // Balance sheet columns
        String COLUMN_NAME = "item_name";
        String COLUMN_VALUE = "item_value";

        ContentResolver contentResolver = getActivity().getContentResolver();
        final String AUTHORITY = "com.nile.networthcalculator.NetWorthProvider";
        final Uri BALANCE_SHEET_URI = Uri.parse("content://" + AUTHORITY + "/networth/balancesheet");

        ContentValues[] values = new ContentValues[34];
        for (int i = 0; i < values.length; i++)
            values[i] = new ContentValues(2);
        values[0].put(COLUMN_VALUE, viewModel.cash_entries[0]);
        values[1].put(COLUMN_VALUE, viewModel.cash_entries[1]);
        values[2].put(COLUMN_VALUE, viewModel.cash_entries[2]);
        values[3].put(COLUMN_VALUE, viewModel.cash_entries[3]);
        values[4].put(COLUMN_VALUE, viewModel.cash_entries[4]);
        values[5].put(COLUMN_VALUE, viewModel.cash_entries[5]);
        values[6].put(COLUMN_VALUE, viewModel.total_cash);
        values[7].put(COLUMN_VALUE, viewModel.invested_asset_entries[0]);
        values[8].put(COLUMN_VALUE, viewModel.invested_asset_entries[1]);
        values[9].put(COLUMN_VALUE, viewModel.invested_asset_entries[2]);
        values[10].put(COLUMN_VALUE, viewModel.invested_asset_entries[3]);
        values[11].put(COLUMN_VALUE, viewModel.invested_asset_entries[4]);
        values[12].put(COLUMN_VALUE, viewModel.invested_asset_entries[5]);
        values[13].put(COLUMN_VALUE, viewModel.invested_asset_entries[6]);
        values[14].put(COLUMN_VALUE, viewModel.invested_asset_entries[7]);
        values[15].put(COLUMN_VALUE, viewModel.invested_asset_entries[8]);
        values[16].put(COLUMN_VALUE, viewModel.invested_asset_entries[9]);
        values[17].put(COLUMN_VALUE, viewModel.invested_asset_entries[10]);
        values[18].put(COLUMN_VALUE, viewModel.invested_asset_entries[11]);
        values[19].put(COLUMN_VALUE, viewModel.invested_asset_entries[12]);
        values[20].put(COLUMN_VALUE, viewModel.invested_asset_entries[13]);
        values[21].put(COLUMN_VALUE, viewModel.invested_asset_entries[14]);
        values[22].put(COLUMN_VALUE, viewModel.invested_asset_entries[15]);
        values[23].put(COLUMN_VALUE, viewModel.total_invested_assets);
        values[24].put(COLUMN_VALUE, viewModel.use_asset_entries[0]);
        values[25].put(COLUMN_VALUE, viewModel.use_asset_entries[1]);
        values[26].put(COLUMN_VALUE, viewModel.use_asset_entries[2]);
        values[27].put(COLUMN_VALUE, viewModel.use_asset_entries[3]);
        values[28].put(COLUMN_VALUE, viewModel.use_asset_entries[4]);
        values[29].put(COLUMN_VALUE, viewModel.use_asset_entries[5]);
        values[30].put(COLUMN_VALUE, viewModel.use_asset_entries[6]);
        values[31].put(COLUMN_VALUE, viewModel.total_use_assets);
        values[32].put(COLUMN_VALUE, viewModel.total_assets);
        values[33].put(COLUMN_VALUE, viewModel.net_worth);

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
    }

    public void saveTableEntries(BalanceSheetModel viewModel) {
        viewModel.cash_entries[0] = parseDouble(txtCheckingAccounts.getText().toString());
        viewModel.cash_entries[1] = parseDouble(txtSavingsAccounts.getText().toString());
        viewModel.cash_entries[2] = parseDouble(txtMoneyMarketAccounts.getText().toString());
        viewModel.cash_entries[3] = parseDouble(txtSavingsBonds.getText().toString());
        viewModel.cash_entries[4] = parseDouble(txtCds.getText().toString());
        viewModel.cash_entries[5] = parseDouble(txtCashValueLifeInsurance.getText().toString());

        viewModel.invested_asset_entries[0] = parseDouble(txtBrokerage.getText().toString());
        viewModel.invested_asset_entries[1] = parseDouble(txtOtherTaxable.getText().toString());
        viewModel.invested_asset_entries[2] = parseDouble(txtIRA.getText().toString());
        viewModel.invested_asset_entries[3] = parseDouble(txtRothIRA.getText().toString());
        viewModel.invested_asset_entries[4] = parseDouble(txt401k.getText().toString());
        viewModel.invested_asset_entries[5] = parseDouble(txtSEPIRA.getText().toString());
        viewModel.invested_asset_entries[6] = parseDouble(txtKeogh.getText().toString());
        viewModel.invested_asset_entries[7] = parseDouble(txtPension.getText().toString());
        viewModel.invested_asset_entries[8] = parseDouble(txtAnnuity.getText().toString());
        viewModel.invested_asset_entries[9] = parseDouble(txtRealEstate.getText().toString());
        viewModel.invested_asset_entries[10] = parseDouble(txtSolePropietorship.getText().toString());
        viewModel.invested_asset_entries[11] = parseDouble(txtPartnership.getText().toString());
        viewModel.invested_asset_entries[12] = parseDouble(txtCCorporation.getText().toString());
        viewModel.invested_asset_entries[13] = parseDouble(txtSCorporation.getText().toString());
        viewModel.invested_asset_entries[14] = parseDouble(txtLLC.getText().toString());
        viewModel.invested_asset_entries[15] = parseDouble(txtOtherBusinessInterests.getText().toString());

        viewModel.use_asset_entries[0] = parseDouble(txtPrincipalHome.getText().toString());
        viewModel.use_asset_entries[1] = parseDouble(txtVacationHome.getText().toString());
        viewModel.use_asset_entries[2] = parseDouble(txtCarsTrucksBoats.getText().toString());
        viewModel.use_asset_entries[3] = parseDouble(txtHomeFurnishings.getText().toString());
        viewModel.use_asset_entries[4] = parseDouble(txtArt.getText().toString());
        viewModel.use_asset_entries[5] = parseDouble(txtJewelryFurs.getText().toString());
        viewModel.use_asset_entries[6] = parseDouble(txtOtherUseAssets.getText().toString());

        viewModel.updateAssets();
    }

    public void populateTables(BalanceSheetModel viewModel) {
        NumberFormat nm = NumberFormat.getNumberInstance();

        // Populate cash table
        txtCheckingAccounts.setText(nm.format(viewModel.cash_entries[0]));
        txtSavingsAccounts.setText(nm.format(viewModel.cash_entries[1]));
        txtMoneyMarketAccounts.setText(nm.format(viewModel.cash_entries[2]));
        txtSavingsBonds.setText(nm.format(viewModel.cash_entries[3]));
        txtCds.setText(nm.format(viewModel.cash_entries[4]));
        txtCashValueLifeInsurance.setText(nm.format(viewModel.cash_entries[5]));

        // Populate invested asset table
        txtBrokerage.setText(nm.format(viewModel.invested_asset_entries[0]));
        txtOtherTaxable.setText(nm.format(viewModel.invested_asset_entries[1]));
        txtIRA.setText(nm.format(viewModel.invested_asset_entries[2]));
        txtRothIRA.setText(nm.format(viewModel.invested_asset_entries[3]));
        txt401k.setText(nm.format(viewModel.invested_asset_entries[4]));
        txtSEPIRA.setText(nm.format(viewModel.invested_asset_entries[5]));
        txtKeogh.setText(nm.format(viewModel.invested_asset_entries[6]));
        txtPension.setText(nm.format(viewModel.invested_asset_entries[7]));
        txtAnnuity.setText(nm.format(viewModel.invested_asset_entries[8]));
        txtRealEstate.setText(nm.format(viewModel.invested_asset_entries[9]));
        txtSolePropietorship.setText(nm.format(viewModel.invested_asset_entries[10]));
        txtPartnership.setText(nm.format(viewModel.invested_asset_entries[11]));
        txtCCorporation.setText(nm.format(viewModel.invested_asset_entries[12]));
        txtSCorporation.setText(nm.format(viewModel.invested_asset_entries[13]));
        txtLLC.setText(nm.format(viewModel.invested_asset_entries[14]));
        txtOtherBusinessInterests.setText(nm.format(viewModel.invested_asset_entries[15]));

        // Populate use asset table
        txtPrincipalHome.setText(nm.format(viewModel.use_asset_entries[0]));
        txtVacationHome.setText(nm.format(viewModel.use_asset_entries[1]));
        txtCarsTrucksBoats.setText(nm.format(viewModel.use_asset_entries[2]));
        txtHomeFurnishings.setText(nm.format(viewModel.use_asset_entries[3]));
        txtArt.setText(nm.format(viewModel.use_asset_entries[4]));
        txtJewelryFurs.setText(nm.format(viewModel.use_asset_entries[5]));
        txtOtherUseAssets.setText(nm.format(viewModel.use_asset_entries[6]));

        // Populate table totals
        txtTotalCash.setText(nm.format(viewModel.total_cash));
        txtTotalInvestedAssets.setText(nm.format(viewModel.total_invested_assets));
        txtTotalUseAssets.setText(nm.format(viewModel.total_use_assets));

        // Populate overall total
        txtTotalAssets.setText(nm.format(viewModel.total_assets));
    }

    public void updateQuantities(BalanceSheetModel viewModel) {
        saveTableEntries(viewModel);

        NumberFormat nm = NumberFormat.getNumberInstance();
        txtTotalCash.setText(nm.format(viewModel.total_cash));
        txtTotalInvestedAssets.setText(nm.format(viewModel.total_invested_assets));
        txtTotalUseAssets.setText(nm.format(viewModel.total_use_assets));
        txtTotalAssets.setText(nm.format(viewModel.total_assets));
    }

}