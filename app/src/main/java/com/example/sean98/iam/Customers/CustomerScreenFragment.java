package com.example.sean98.iam.Customers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sean98.iam.CustomerActivity;
import com.example.sean98.iam.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Adapters.CustomerViewAdapter;
import Databases.CachedDao;
import Databases.Exceptions.DatabaseException;
import Models.Cards.CustomerParams;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import permmisionModels.PermissionMethods;


public class CustomerScreenFragment extends Fragment {

    public CustomerScreenManger.OnCustomerClickedListener onCustomerClickedListener;
    private SwipeRefreshLayout refreshLayout;
    private CustomerViewAdapter customerViewAdapter;
    private RecyclerView recyclerView;
    private SharedViewModel model;
    private CustomerParams params;
    private FloatingActionButton addFab;


    public CustomerScreenFragment() {
        //Empty Constructor
    }

    public static CustomerScreenFragment newInstance() {
        return new CustomerScreenFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        customerViewAdapter = new CustomerViewAdapter();
        model.getCustomers().observe(this, customers -> {
            customerViewAdapter.updateDataSet(customers);
            customerViewAdapter.notifyDataSetChanged();
        });
        model.getParams().observe(this, params -> {
            this.params = params;
        });

        customerViewAdapter.onCustomerViewClickListener = (v, c) -> {
            if (onCustomerClickedListener != null)
                    onCustomerClickedListener.onCustomerClicked(v, c);
        };

        model.getLoadData().observe(this, value -> {
            if (value)
                refreshLayout.setRefreshing(true);
            else
                refreshLayout.setRefreshing(false);
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customers_screen, container, false);
        refreshLayout = view.findViewById(R.id.refreshLayout);

        recyclerView = view.findViewById(R.id.customers_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(customerViewAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                lm.getOrientation());

        recyclerView.addItemDecoration(dividerItemDecoration);

        addFab = view.findViewById(R.id.add_fab);
        addFab.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CustomerActivity.CUSTOMER_KEY, null);
            Intent intent = new Intent(getActivity(), CustomerActivity.class);
            intent.putExtra(CustomerActivity.BUNDLE_KEY, bundle);
            startActivity(intent);
        });

        refreshLayout.setOnRefreshListener(() -> {
            model.setLoadData(true);
            CachedDao db = new CachedDao();
            db.forceOnline().searchCustomers(params).addOnSuccessListener(customers ->{
                model.updateCustomers(customers);
                customerViewAdapter.updateDataSet(customers);
                if (getActivity()!=null)
                    getActivity().runOnUiThread(customerViewAdapter::notifyDataSetChanged);
            }).addOnFailureListener((de)->{
                        String msg;
                        if (de.getType() == DatabaseException.Type.TimeOut
                                && PermissionMethods.isInternetAvailable())
                            msg = getString(R.string.no_internet_access);
                        else
                            msg = getString(R.string.no_access_to_server);
                        if (getActivity()!=null)
                            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());
            }).addOnPostExecuteListener((r)-> model.setLoadData(false))
            .execute();
        });

        return view;
    }
}
