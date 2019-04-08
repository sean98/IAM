package com.example.sean98.iam.Customers.Finance;

import android.content.Context;

import Models.Documents.FinanceRecord;
import android.os.Bundle;

import Models.Company.SystemVariables;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sean98.iam.R;

import java.text.SimpleDateFormat;

public class FinanceRecordView extends Fragment {
    public static String FINANCE_RECORD_KEY = "FINANCE_RECORD_KEY";
    private FinanceRecord financeRecord;

    private TextView date_text;
    private TextView type_text;
    private TextView ref1_text;
    private TextView ref2_text;
    private TextView debt_text;
    private TextView balance_text;

    public FinanceRecordView() {
        // Required empty public constructor
    }



    public static FinanceRecordView newInstance(FinanceRecord financeRecord) {
        FinanceRecordView fragment = new FinanceRecordView();
        Bundle args = new Bundle();
        args.putSerializable(FINANCE_RECORD_KEY,financeRecord);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            financeRecord = (FinanceRecord) getArguments().getSerializable(FINANCE_RECORD_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_finance_record_view,
                container, false) ;
        date_text = v.findViewById(R.id.date_text);
        type_text = v.findViewById(R.id.type_text);
        ref1_text = v.findViewById(R.id.ref1_text);
        ref2_text = v.findViewById(R.id.ref2_text);
        debt_text = v.findViewById(R.id.debt_text);
        balance_text = v.findViewById(R.id.balance_text);

        date_text.setText(new SimpleDateFormat("dd-MM-yy").
                format(financeRecord.getRefDate()));
        type_text.setText(financeRecord.getType().toString());
        ref1_text.setText(financeRecord.getBaseDocSN());
        ref2_text.setText(financeRecord.getBase2DocSN());
        debt_text.setText(financeRecord.getDebt()+" "+ SystemVariables.GetSystemCurrency());
        balance_text.setText(financeRecord.getBalanceDue()+" "+SystemVariables.GetSystemCurrency());
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
