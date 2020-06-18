package com.nile.networthcalculator.ui.liabilities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static com.nile.networthcalculator.util.Utilities.parseDouble;

import com.nile.networthcalculator.R;

import java.text.NumberFormat;

public class LiabilityFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_liability, container, false);
        final View currentTable = root.findViewById(R.id.current_liability_table);
        final View longTermTable = root.findViewById(R.id.long_term_liability_table);
        final TextView txtTotalLiabilities = root.findViewById(R.id.total_liabilities);
        Button calculateButton = root.findViewById(R.id.calculate_button);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantities(txtTotalLiabilities, currentTable, longTermTable);
            }
        });

        return root;
    }

    public void updateQuantities(TextView txtTotalLiabilities, View currentTable, View longTermTable) {
        EditText txtCreditCardBalances = currentTable.findViewById(R.id.credit_card_balances);
        EditText txtIncomeTaxOwed = currentTable.findViewById(R.id.estimated_income_tax_owed);
        EditText txtOtherBills = currentTable.findViewById(R.id.other_outstanding_bills);

        EditText txtHomeMortgage = longTermTable.findViewById(R.id.home_mortgage);
        EditText txtHomeEquityLoan = longTermTable.findViewById(R.id.home_equity_loan);
        EditText txtMortgagesOnRentals = longTermTable.findViewById(R.id.mortgages_on_rental_properties);
        EditText txtCarLoans = longTermTable.findViewById(R.id.car_loans);
        EditText txtStudentLoans = longTermTable.findViewById(R.id.student_loans);
        EditText txtLifeInsurancePolicyLoans = longTermTable.findViewById(R.id.life_insurance_policy_loans);
        EditText txtOtherLongTermDebt = longTermTable.findViewById(R.id.other_long_term_debt);

        double creditCardBalances = parseDouble(txtCreditCardBalances.getText().toString());
        double incomeTaxOwed = parseDouble(txtIncomeTaxOwed.getText().toString());
        double otherBills = parseDouble(txtOtherBills.getText().toString());

        double homeMortgage = parseDouble(txtHomeMortgage.getText().toString());
        double homeEquityLoan = parseDouble(txtHomeEquityLoan.getText().toString());
        double mortgagesOnRentals = parseDouble(txtMortgagesOnRentals.getText().toString());
        double carLoans = parseDouble(txtCarLoans.getText().toString());
        double studentLoans = parseDouble(txtStudentLoans.getText().toString());
        double lifeInsurancePolicyLoans = parseDouble(txtLifeInsurancePolicyLoans.getText().toString());
        double otherLongTermDebt = parseDouble(txtOtherLongTermDebt.getText().toString());

        double totalLiabilities = creditCardBalances + incomeTaxOwed + otherBills + homeMortgage +
                homeEquityLoan + mortgagesOnRentals + carLoans + studentLoans +
                lifeInsurancePolicyLoans + otherLongTermDebt;

        txtTotalLiabilities.setText(NumberFormat.getNumberInstance().format(totalLiabilities));
    }

}