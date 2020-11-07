package com.nile.networthcalculator.ui.liabilities;

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
import com.nile.networthcalculator.BalanceSheetModelFactory;
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

        final BalanceSheetModel viewModel = ViewModelProviders.of(getActivity(), new BalanceSheetModelFactory(getActivity().getContentResolver())).get(BalanceSheetModel.class);

        final View currentTable = root.findViewById(R.id.current_liability_table);
        final View longTermTable = root.findViewById(R.id.long_term_liability_table);
        Button updateBtn = root.findViewById(R.id.update_btn);
        Button saveBtn = root.findViewById(R.id.save_btn);
        Button resetBtn = root.findViewById(R.id.reset_btn);

        fetchTableReferences(root, currentTable, longTermTable);
        populateTables(viewModel);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistTableEntries(viewModel);
                updateTotals(viewModel);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistTableEntries(viewModel);
                updateTotals(viewModel);
                viewModel.storeLiabilities();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.resetLiabilities();
                populateTables(viewModel);
            }
        });

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        BalanceSheetModel viewModel = ViewModelProviders.of(getActivity(), new BalanceSheetModelFactory(getActivity().getContentResolver())).get(BalanceSheetModel.class);
        persistTableEntries(viewModel);
        updateTotals(viewModel);
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
        txtTotalLiabilities.setText(getString(R.string.total_liabilities, viewModel.total_liabilites));
    }

    public void persistTableEntries(BalanceSheetModel viewModel) {
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

    public void updateTotals(BalanceSheetModel viewModel) {
        txtTotalLiabilities.setText(getString(R.string.total_liabilities, viewModel.total_liabilites));
    }

}