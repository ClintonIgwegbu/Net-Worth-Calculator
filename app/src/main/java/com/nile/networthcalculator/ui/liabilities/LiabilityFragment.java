package com.nile.networthcalculator.ui.liabilities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static com.nile.networthcalculator.util.Utilities.parseDouble;

import com.nile.networthcalculator.BalanceSheetModel;
import com.nile.networthcalculator.R;

import java.text.NumberFormat;

public class LiabilityFragment extends Fragment {

    // Current table references
    EditText txtCreditCardBalances;
    EditText txtIncomeTaxOwed;
    EditText txtOtherBills;

    // Long term table references
    EditText txtHomeMortgage;
    EditText txtHomeEquityLoan;
    EditText txtMortgagesOnRentals;
    EditText txtCarLoans;
    EditText txtStudentLoans;
    EditText txtLifeInsurancePolicyLoans;
    EditText txtOtherLongTermDebt;

    TextView txtTotalLiabilities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_liability, container, false);

        final BalanceSheetModel viewModel = ViewModelProviders.of(getActivity()).get(BalanceSheetModel.class);

        final View currentTable = root.findViewById(R.id.current_liability_table);
        final View longTermTable = root.findViewById(R.id.long_term_liability_table);
        Button calculateButton = root.findViewById(R.id.calculate_button);

        fetchTableReferences(root, currentTable, longTermTable);
        populateTables(viewModel);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantities(viewModel);
                storeTableInDatabase(viewModel);
            }
        });

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        BalanceSheetModel viewModel = ViewModelProviders.of(getActivity()).get(BalanceSheetModel.class);
        saveTableEntries(viewModel);  // Persist table data
    }

    public void fetchTableReferences(View root, View currentTable, View longTermTable) {
        // Current table references
        txtCreditCardBalances = currentTable.findViewById(R.id.credit_card_balances);
        txtIncomeTaxOwed = currentTable.findViewById(R.id.estimated_income_tax_owed);
        txtOtherBills = currentTable.findViewById(R.id.other_outstanding_bills);

        // Long term table references
        txtHomeMortgage = longTermTable.findViewById(R.id.home_mortgage);
        txtHomeEquityLoan = longTermTable.findViewById(R.id.home_equity_loan);
        txtMortgagesOnRentals = longTermTable.findViewById(R.id.mortgages_on_rental_properties);
        txtCarLoans = longTermTable.findViewById(R.id.car_loans);
        txtStudentLoans = longTermTable.findViewById(R.id.student_loans);
        txtLifeInsurancePolicyLoans = longTermTable.findViewById(R.id.life_insurance_policy_loans);
        txtOtherLongTermDebt = longTermTable.findViewById(R.id.other_long_term_debt);

        txtTotalLiabilities = root.findViewById(R.id.total_liabilities);

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
        ContentValues[] values = new ContentValues[12];
        for (int i = 0; i < values.length; i++)
            values[i] = new ContentValues(1);
        values[0].put(COLUMN_VALUE, viewModel.current_liabilities[0]);
        values[1].put(COLUMN_VALUE, viewModel.current_liabilities[1]);
        values[2].put(COLUMN_VALUE, viewModel.current_liabilities[2]);
        values[3].put(COLUMN_VALUE, viewModel.long_term_liabilities[0]);
        values[4].put(COLUMN_VALUE, viewModel.long_term_liabilities[1]);
        values[5].put(COLUMN_VALUE, viewModel.long_term_liabilities[2]);
        values[6].put(COLUMN_VALUE, viewModel.long_term_liabilities[3]);
        values[7].put(COLUMN_VALUE, viewModel.long_term_liabilities[4]);
        values[8].put(COLUMN_VALUE, viewModel.long_term_liabilities[5]);
        values[9].put(COLUMN_VALUE, viewModel.long_term_liabilities[6]);
        values[10].put(COLUMN_VALUE, viewModel.total_liabilites);
        values[11].put(COLUMN_VALUE, viewModel.net_worth);

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
    }

    public void populateTables(BalanceSheetModel viewModel) {
        NumberFormat nm = NumberFormat.getNumberInstance();

        // Populate current table
        txtCreditCardBalances.setText(nm.format(viewModel.current_liabilities[0]));
        txtIncomeTaxOwed.setText(nm.format(viewModel.current_liabilities[1]));
        txtOtherBills.setText(nm.format(viewModel.current_liabilities[2]));

        // Populate long term table
        txtHomeMortgage.setText(nm.format(viewModel.long_term_liabilities[0]));
        txtHomeEquityLoan.setText(nm.format(viewModel.long_term_liabilities[1]));
        txtMortgagesOnRentals.setText(nm.format(viewModel.long_term_liabilities[2]));
        txtCarLoans.setText(nm.format(viewModel.long_term_liabilities[3]));
        txtStudentLoans.setText(nm.format(viewModel.long_term_liabilities[4]));
        txtLifeInsurancePolicyLoans.setText(nm.format(viewModel.long_term_liabilities[5]));
        txtOtherLongTermDebt.setText(nm.format(viewModel.long_term_liabilities[6]));

        // Populate overall total
        txtTotalLiabilities.setText(nm.format(viewModel.total_liabilites));
    }

    public void saveTableEntries(BalanceSheetModel viewModel) {
        viewModel.current_liabilities[0] = parseDouble(txtCreditCardBalances.getText().toString());
        viewModel.current_liabilities[1] = parseDouble(txtIncomeTaxOwed.getText().toString());
        viewModel.current_liabilities[2] = parseDouble(txtOtherBills.getText().toString());

        viewModel.long_term_liabilities[0] = parseDouble(txtHomeMortgage.getText().toString());
        viewModel.long_term_liabilities[1] = parseDouble(txtHomeEquityLoan.getText().toString());
        viewModel.long_term_liabilities[2] = parseDouble(txtMortgagesOnRentals.getText().toString());
        viewModel.long_term_liabilities[3] = parseDouble(txtCarLoans.getText().toString());
        viewModel.long_term_liabilities[4] = parseDouble(txtStudentLoans.getText().toString());
        viewModel.long_term_liabilities[5] = parseDouble(txtLifeInsurancePolicyLoans.getText().toString());
        viewModel.long_term_liabilities[6] = parseDouble(txtOtherLongTermDebt.getText().toString());

        viewModel.updateLiabilities();
    }

    public void updateQuantities(BalanceSheetModel viewModel) {
        saveTableEntries(viewModel);
        txtTotalLiabilities.setText(NumberFormat.getNumberInstance().format(viewModel.total_liabilites));
    }

}