package com.example.sean98.iam.Customers.Finance;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.sean98.iam.Documents.DocumentView;
import com.example.sean98.iam.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import Adapters.FinanceRecordViewAdapter;
import Databases.Exceptions.DatabaseException;
import Databases.IDocumentsDao;
import Databases.SAP.SapMSSQL;
import Databases.util.DbTask;
import Models.Cards.Customer;
import Models.Company.SystemVariables;
import Models.Documents.Document;
import Models.Documents.FinanceRecord;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import permmisionModels.PermissionMethods;


public class FinanceRecordListView extends Fragment {
    private static final String CUSTOMER_KEY = "CUSTOMER_KEY";

    private TextInputEditText balance_text;
    private CheckBox showOpenOnlyCheckbox;
    private Customer customer;
    private FinanceRecordViewAdapter financeRecordViewAdapter;
    private RecyclerView listView;
    private SwipeRefreshLayout refreshLayout;
    private List<FinanceRecord> financeRecordList;
    private List<FinanceRecord> financeRecordListFiltered;
    private float balanceSum =0;

    public FinanceRecordListView() {
        // Required empty public constructor
    }

    public static FinanceRecordListView newInstance(Customer customer) {
        FinanceRecordListView fragment = new FinanceRecordListView();
        Bundle args = new Bundle();
        args.putSerializable(CUSTOMER_KEY, customer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customer = (Customer) getArguments().getSerializable(CUSTOMER_KEY);
            financeRecordViewAdapter = new FinanceRecordViewAdapter();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_finance_reords_list_view, container,
                false);
        balance_text = v.findViewById(R.id.balance_text);
        refreshLayout = v.findViewById(R.id.refreshLayout);
        listView = v.findViewById(R.id.records_list);
        showOpenOnlyCheckbox = v.findViewById(R.id.show_open_only_checkbox);

        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        listView.setLayoutManager(lm);
        listView.setAdapter(financeRecordViewAdapter);
        refreshLayout.setOnRefreshListener(()->updateFinanceRecordList());
        showOpenOnlyCheckbox.setOnCheckedChangeListener((b,isChecked)->applyFilter(isChecked));
        financeRecordViewAdapter.onItemClickListener = (frView, financeRecord)->{
            if(financeRecord.getType() == FinanceRecord.Type.Invoice ||
                    financeRecord.getType() == FinanceRecord.Type.CreditInvoice ){
                if(getActivity()!= null)
                    getActivity().runOnUiThread(()->refreshLayout.setRefreshing(true));
                IDocumentsDao db = new SapMSSQL();
                DbTask<List<Document>> task;
                if(financeRecord.getType() == FinanceRecord.Type.Invoice)
                    task = db.getInvoices(new Customer(financeRecord.getCardCode(),0,null,null));
                else
                    task = db.getRevokedInvoices(new Customer(financeRecord.getCardCode(),0,null,null));

                task.addOnSuccessListener(docs-> {
                            Document doc = null;
                            for (Document d : docs) {
                                if ((d.getSn() + "").equals(financeRecord.getBaseDocSN()))
                                    doc = d;
                            }
                            if (doc != null && getActivity() != null) {
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fragmentLayout, DocumentView.newInstance(doc));
                                ft.addToBackStack(null);
                                ft.commit();
                            }}
                        ).addOnFailureListener(e-> {
                            String msg;
                            if (e.getType() == DatabaseException.Type.TimeOut
                                    && PermissionMethods.isInternetAvailable())
                                msg = getString(R.string.no_internet_access);
                            else
                                msg = getString(R.string.no_access_to_server);
                            if (getActivity() != null)
                                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), msg,
                                        Toast.LENGTH_SHORT).show()); }

                        ).addOnPostExecuteListener(r-> {
                            if (getActivity() != null)
                                getActivity().runOnUiThread(() -> refreshLayout.setRefreshing(false)); }
                        ).execute();
            }
            else if(getActivity()!=null){
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.unsupported_document), Toast.LENGTH_SHORT).show());
            }
        };
        if(financeRecordList == null)
            updateFinanceRecordList();
        else {
            applyFilter(showOpenOnlyCheckbox.isChecked());
            refreshLayout.setRefreshing(false);
            balance_text.setText(balanceSum + " " + SystemVariables.GetSystemCurrency());
        }

        return v;
    }

    public void updateFinanceRecordList() {
        refreshLayout.setRefreshing(true);
        IDocumentsDao db = new SapMSSQL();
        db.getFinanceRecords(customer).addOnSuccessListener((financeRecords -> {
            financeRecordList = financeRecords;
            financeRecordListFiltered = new ArrayList<>();
            balanceSum = 0;
            for (FinanceRecord f : financeRecordList) {
                if (f.getBalanceDue() != 0) {
                    financeRecordListFiltered.add(f);
                    balanceSum += f.getBalanceDue();
                }
            }
        })).addOnFailureListener(de -> {
            String msg;
            if (de.getType() == DatabaseException.Type.TimeOut
                    && PermissionMethods.isInternetAvailable())
                msg = getString(R.string.no_internet_access);
            else
                msg = getString(R.string.no_access_to_server);
            if (getActivity() != null)
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());
        }).addOnPostExecuteListener(r -> {
            if (getActivity() != null)
                getActivity().runOnUiThread(() -> {
                    applyFilter(showOpenOnlyCheckbox.isChecked());
                    refreshLayout.setRefreshing(false);
                    balance_text.setText(balanceSum + " " + SystemVariables.GetSystemCurrency());
                });
        }).execute();
    }

    private void applyFilter(boolean apply){
        if(financeRecordList!=null ){
            if(apply)
                financeRecordViewAdapter.updateDataSet(financeRecordListFiltered);
            else
                financeRecordViewAdapter.updateDataSet(financeRecordList);
            financeRecordViewAdapter.notifyDataSetChanged();
        }
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
