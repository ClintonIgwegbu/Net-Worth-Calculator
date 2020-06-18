package com.nile.networthcalculator.ui.assets;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nile.networthcalculator.R;

import java.io.Serializable;
import java.text.NumberFormat;

import static com.nile.networthcalculator.util.Utilities.parseDouble;

public class AssetFragment extends Fragment {

    // Constants for saving instance state
    static final String CASH_ENTRIES = "cashEntries";
    static final String INVESTED_ASSET_ENTRIES = "investedAssetEntries";
    static final String USE_ASSET_ENTRIES = "useAssetEntries";
    static final String TOTAL_CASH = "totalCash";
    static final String TOTAL_INVESTED_ASSETS = "totalInvestedAssets";
    static final String TOTAL_USE_ASSETS = "totalUseAssets";
    static final String TOTAL_ASSETS = "totalAssets";

    // Array for holding table entries
    double[] cash_entries = new double[6];
    double[] invested_asset_entries = new double[16];
    double[] use_asset_entries = new double[7];

    // Totals
    double total_cash;
    double total_invested_assets;
    double total_use_assets;
    double total_assets;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            cash_entries = savedInstanceState.getDoubleArray(CASH_ENTRIES);
            invested_asset_entries = savedInstanceState.getDoubleArray(INVESTED_ASSET_ENTRIES);
            use_asset_entries = savedInstanceState.getDoubleArray(USE_ASSET_ENTRIES);
            total_cash = savedInstanceState.getDouble(TOTAL_CASH);
            total_invested_assets = savedInstanceState.getDouble(TOTAL_INVESTED_ASSETS);
            total_use_assets = savedInstanceState.getDouble(TOTAL_USE_ASSETS);
            total_assets = savedInstanceState.getDouble(TOTAL_ASSETS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_asset, container, false);


        final View cashTable = root.findViewById(R.id.cash_table);
        final View investedAssetsTable = root.findViewById(R.id.invested_assets_table);
        final View useAssetsTable = root.findViewById(R.id.use_assets_table);
        final TextView txtTotalAssets = root.findViewById(R.id.total_assets);
        Button calculateButton = root.findViewById(R.id.calculate_button);

        fetchTableReferences(root, cashTable, investedAssetsTable, useAssetsTable);
        populateTables();

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantities(txtTotalAssets, cashTable, investedAssetsTable, useAssetsTable);
            }
        });

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Something happening", "onPause: here! ");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("Other thing happening", "onSaveInstanceState: there! ");
        saveTableEntries();
        outState.putDoubleArray(CASH_ENTRIES, cash_entries);
        outState.putDoubleArray(INVESTED_ASSET_ENTRIES, invested_asset_entries);
        outState.putDoubleArray(USE_ASSET_ENTRIES, use_asset_entries);
        outState.putDouble(TOTAL_CASH, total_cash);
        outState.putDouble(TOTAL_INVESTED_ASSETS, total_invested_assets);
        outState.putDouble(TOTAL_USE_ASSETS, total_use_assets);
        outState.putDouble(TOTAL_ASSETS, total_assets);
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

    public void saveTableEntries() {
        total_cash = 0;
        total_invested_assets = 0;
        total_use_assets = 0;

        cash_entries[0] = parseDouble(txtCheckingAccounts.getText().toString());
        cash_entries[1] = parseDouble(txtSavingsAccounts.getText().toString());
        cash_entries[2] = parseDouble(txtMoneyMarketAccounts.getText().toString());
        cash_entries[3] = parseDouble(txtSavingsBonds.getText().toString());
        cash_entries[4] = parseDouble(txtCds.getText().toString());
        cash_entries[5] = parseDouble(txtCashValueLifeInsurance.getText().toString());

        invested_asset_entries[0] = parseDouble(txtBrokerage.getText().toString());
        invested_asset_entries[1] = parseDouble(txtOtherTaxable.getText().toString());
        invested_asset_entries[2] = parseDouble(txtIRA.getText().toString());
        invested_asset_entries[3] = parseDouble(txtRothIRA.getText().toString());
        invested_asset_entries[4] = parseDouble(txt401k.getText().toString());
        invested_asset_entries[5] = parseDouble(txtSEPIRA.getText().toString());
        invested_asset_entries[6] = parseDouble(txtKeogh.getText().toString());
        invested_asset_entries[7] = parseDouble(txtPension.getText().toString());
        invested_asset_entries[8] = parseDouble(txtAnnuity.getText().toString());
        invested_asset_entries[9] = parseDouble(txtRealEstate.getText().toString());
        invested_asset_entries[10] = parseDouble(txtSolePropietorship.getText().toString());
        invested_asset_entries[11] = parseDouble(txtPartnership.getText().toString());
        invested_asset_entries[12] = parseDouble(txtCCorporation.getText().toString());
        invested_asset_entries[13] = parseDouble(txtSCorporation.getText().toString());
        invested_asset_entries[14] = parseDouble(txtLLC.getText().toString());
        invested_asset_entries[15] = parseDouble(txtOtherBusinessInterests.getText().toString());

        use_asset_entries[0] = parseDouble(txtPrincipalHome.getText().toString());
        use_asset_entries[1] = parseDouble(txtVacationHome.getText().toString());
        use_asset_entries[2] = parseDouble(txtCarsTrucksBoats.getText().toString());
        use_asset_entries[3] = parseDouble(txtHomeFurnishings.getText().toString());
        use_asset_entries[4] = parseDouble(txtArt.getText().toString());
        use_asset_entries[5] = parseDouble(txtJewelryFurs.getText().toString());
        use_asset_entries[6] = parseDouble(txtOtherUseAssets.getText().toString());

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
    }

    public void populateTables() {
        NumberFormat nm = NumberFormat.getNumberInstance();

        // Populate cash table
        txtCheckingAccounts.setText(nm.format(cash_entries[0]));
        txtSavingsAccounts.setText(nm.format(cash_entries[1]));
        txtMoneyMarketAccounts.setText(nm.format(cash_entries[2]));
        txtSavingsBonds.setText(nm.format(cash_entries[3]));
        txtCds.setText(nm.format(cash_entries[4]));
        txtCashValueLifeInsurance.setText(nm.format(cash_entries[5]));

        // Populate invested asset table
        txtBrokerage.setText(nm.format(invested_asset_entries[0]));
        txtOtherTaxable.setText(nm.format(invested_asset_entries[1]));
        txtIRA.setText(nm.format(invested_asset_entries[2]));
        txtRothIRA.setText(nm.format(invested_asset_entries[3]));
        txt401k.setText(nm.format(invested_asset_entries[4]));
        txtSEPIRA.setText(nm.format(invested_asset_entries[5]));
        txtKeogh.setText(nm.format(invested_asset_entries[6]));
        txtPension.setText(nm.format(invested_asset_entries[7]));
        txtAnnuity.setText(nm.format(invested_asset_entries[8]));
        txtRealEstate.setText(nm.format(invested_asset_entries[9]));
        txtSolePropietorship.setText(nm.format(invested_asset_entries[10]));
        txtPartnership.setText(nm.format(invested_asset_entries[11]));
        txtCCorporation.setText(nm.format(invested_asset_entries[12]));
        txtSCorporation.setText(nm.format(invested_asset_entries[13]));
        txtLLC.setText(nm.format(invested_asset_entries[14]));
        txtOtherBusinessInterests.setText(nm.format(invested_asset_entries[15]));

        // Populate use asset table
        txtPrincipalHome.setText(nm.format(use_asset_entries[0]));
        txtVacationHome.setText(nm.format(use_asset_entries[1]));
        txtCarsTrucksBoats.setText(nm.format(use_asset_entries[2]));
        txtHomeFurnishings.setText(nm.format(use_asset_entries[3]));
        txtArt.setText(nm.format(use_asset_entries[4]));
        txtJewelryFurs.setText(nm.format(use_asset_entries[5]));
        txtOtherUseAssets.setText(nm.format(use_asset_entries[6]));

        // Populate table totals
        txtTotalCash.setText(nm.format(total_cash));
        txtTotalInvestedAssets.setText(nm.format(total_invested_assets));
        txtTotalUseAssets.setText(nm.format(total_use_assets));

        // Populate overall total
        txtTotalAssets.setText(nm.format(total_assets));
    }

    public void updateQuantities(TextView txtTotalAssets, View cashTable, View investedAssetsTable, View useAssetsTable) {
//        EditText txtCheckingAccounts = cashTable.findViewById(R.id.checking_accounts);
//        EditText txtSavingsAccounts = cashTable.findViewById(R.id.savings_accounts);
//        EditText txtMoneyMarketAccounts = cashTable.findViewById(R.id.money_market_accounts);
//        EditText txtSavingsBonds = cashTable.findViewById(R.id.savings_bonds);
//        EditText txtCds = cashTable.findViewById(R.id.cds);
//        EditText txtCashValueLifeInsurance = cashTable.findViewById(R.id.cash_value_of_life_insurance);
//        TextView txtTotalCash = cashTable.findViewById(R.id.total_cash);
//
//        EditText txtBrokerage = investedAssetsTable.findViewById(R.id.brokerage);
//        EditText txtOtherTaxable = investedAssetsTable.findViewById(R.id.other_taxable);
//        EditText txtIRA = investedAssetsTable.findViewById(R.id.ira);
//        EditText txtRothIRA = investedAssetsTable.findViewById(R.id.roth_ira);
//        EditText txt401k = investedAssetsTable.findViewById(R.id._401_k_or_403_b);
//        EditText txtSEPIRA = investedAssetsTable.findViewById(R.id.sep_ira);
//        EditText txtKeogh = investedAssetsTable.findViewById(R.id.keogh);
//        EditText txtPension = investedAssetsTable.findViewById(R.id.pension_vested_benefit);
//        EditText txtAnnuity = investedAssetsTable.findViewById(R.id.annuity_accumulated_value);
//        EditText txtRealEstate = investedAssetsTable.findViewById(R.id.real_estate_rental_property_or_land);
//        EditText txtSolePropietorship = investedAssetsTable.findViewById(R.id.sole_propietorship);
//        EditText txtPartnership = investedAssetsTable.findViewById(R.id.partnership);
//        EditText txtCCorporation = investedAssetsTable.findViewById(R.id.c_corporation);
//        EditText txtSCorporation = investedAssetsTable.findViewById(R.id.s_corporation);
//        EditText txtLLC = investedAssetsTable.findViewById(R.id.limited_liability_company);
//        EditText txtOtherBusinessInterests = investedAssetsTable.findViewById(R.id.other_business_interests);
//        TextView txtTotalInvestedAssets = investedAssetsTable.findViewById(R.id.total_invested_assets);
//
//        EditText txtPrincipalHome = useAssetsTable.findViewById(R.id.principal_home);
//        EditText txtVacationHome = useAssetsTable.findViewById(R.id.vacation_home);
//        EditText txtCarsTrucksBoats = useAssetsTable.findViewById(R.id.cars_trucks_boats);
//        EditText txtHomeFurnishings = useAssetsTable.findViewById(R.id.home_furnishings);
//        EditText txtArt = useAssetsTable.findViewById(R.id.art_antiques_coins_collectibles);
//        EditText txtJewelryFurs = useAssetsTable.findViewById(R.id.jewelry_furs);
//        EditText txtOtherUseAssets = useAssetsTable.findViewById(R.id.other_use_assets);
//        TextView txtTotalUseAssets = useAssetsTable.findViewById(R.id.total_use_assets);
//
//        double checkingAccounts = parseDouble(txtCheckingAccounts.getText().toString());
//        double savingsAccounts = parseDouble(txtSavingsAccounts.getText().toString());
//        double moneyMarketAccounts = parseDouble(txtMoneyMarketAccounts.getText().toString());
//        double savingsBonds = parseDouble(txtSavingsBonds.getText().toString());
//        double cds = parseDouble(txtCds.getText().toString());
//        double cashValueLifeInsurance = parseDouble(txtCashValueLifeInsurance.getText().toString());
//
//        double brokerage = parseDouble(txtBrokerage.getText().toString());
//        double otherTaxable = parseDouble(txtOtherTaxable.getText().toString());
//        double IRA = parseDouble(txtIRA.getText().toString());
//        double RothIRA = parseDouble(txtRothIRA.getText().toString());
//        double _401k = parseDouble(txt401k.getText().toString());
//        double SEPIRA = parseDouble(txtSEPIRA.getText().toString());
//        double Keogh = parseDouble(txtKeogh.getText().toString());
//        double pension = parseDouble(txtPension.getText().toString());
//        double annuity = parseDouble(txtAnnuity.getText().toString());
//        double realEstate = parseDouble(txtRealEstate.getText().toString());
//        double solePropietorship = parseDouble(txtSolePropietorship.getText().toString());
//        double partnership = parseDouble(txtPartnership.getText().toString());
//        double CCorporation = parseDouble(txtCCorporation.getText().toString());
//        double SCorporation = parseDouble(txtSCorporation.getText().toString());
//        double LLC = parseDouble(txtLLC.getText().toString());
//        double otherBusinessInterests = parseDouble(txtOtherBusinessInterests.getText().toString());
//
//        double principalHome = parseDouble(txtPrincipalHome.getText().toString());
//        double vacationHome = parseDouble(txtVacationHome.getText().toString());
//        double carsTrucksBoats = parseDouble(txtCarsTrucksBoats.getText().toString());
//        double homeFurnishings = parseDouble(txtHomeFurnishings.getText().toString());
//        double art = parseDouble(txtArt.getText().toString());
//        double jewelryFurs = parseDouble(txtJewelryFurs.getText().toString());
//        double otherUseAssets = parseDouble(txtOtherUseAssets.getText().toString());
//
//        double totalCash = checkingAccounts + savingsAccounts + moneyMarketAccounts + savingsBonds
//                + cds + cashValueLifeInsurance;
//        double totalInvestedAssets = brokerage + otherTaxable + IRA + RothIRA + _401k + SEPIRA
//                + Keogh + pension + annuity + realEstate + solePropietorship + partnership +
//                CCorporation + SCorporation + LLC + otherBusinessInterests;
//        double totalUseAssets = principalHome + vacationHome + carsTrucksBoats + homeFurnishings +
//                art + jewelryFurs + otherUseAssets;
//        double totalAssets = totalCash + totalInvestedAssets + totalUseAssets;

        saveTableEntries();

        NumberFormat nm = NumberFormat.getNumberInstance();
        txtTotalCash.setText(nm.format(total_cash));
        txtTotalInvestedAssets.setText(nm.format(total_invested_assets));
        txtTotalUseAssets.setText(nm.format(total_use_assets));
        txtTotalAssets.setText(nm.format(total_assets));
    }

}