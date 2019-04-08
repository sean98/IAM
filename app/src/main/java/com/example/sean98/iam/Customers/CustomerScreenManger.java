package com.example.sean98.iam.Customers;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sean98.iam.Dialogs.FilterDialog;
import com.example.sean98.iam.Dialogs.SortDialog;
import com.example.sean98.iam.R;
import com.google.android.material.circularreveal.coordinatorlayout.CircularRevealCoordinatorLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import Databases.CachedDao;
import Databases.Exceptions.DatabaseException;
import Databases.ICustomerDao;
import Databases.util.DbTask;
import Models.Cards.Customer;
import Models.Cards.CustomerParams;
import Models.Company.SalesMan;
import Models.Company.SystemVariables;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import permmisionModels.PermissionMethods;

public class CustomerScreenManger extends Fragment {
    private SharedViewModel model;
    private CustomerParams params;
    private List<String> groups;
    private List<SalesMan> salesMEN;
    public FragmentManager fragmentManager;
    private Activity activity;
    private Context context;

    private CustomerMapFragment mapFragment = CustomerMapFragment.getInstance();
    private CustomerScreenFragment listFragment = CustomerScreenFragment.newInstance();
    private CircularRevealCoordinatorLayout screen;
    private String query = "";



    public OnCustomerClickedListener onCustomerClickedListener;
    public interface OnCustomerClickedListener {
        void onCustomerClicked(View v, Customer c);
    }

    public  CustomerScreenManger() {
        //Empty Constructor
    }

    public static CustomerScreenManger getInstance() {
        return new CustomerScreenManger();
    }

    public void updateCustomers(CustomerParams params) {
        this.params = params;
        Log.i("DbTask", "updateCustomers call");
        model.updateCustomers(new ArrayList<>());
        model.setLoadData(true);
        new DbTask<>("MG loading data", () -> {
            ICustomerDao db = new CachedDao();
            List<Customer> customerList = db.searchCustomers(params).call();
            groups = db.getCustomerGroups().call();
            salesMEN = db.getSalesmen().call();
            return customerList;

        }).addOnSuccessListener((customerList) ->
                model.updateCustomers(customerList)
        ).addOnFailureListener((de) -> {
            String msg;
            if (de.getType() == DatabaseException.Type.TimeOut
                    && PermissionMethods.isInternetAvailable())
                msg = getString(R.string.no_internet_access);
            else
                msg = getString(R.string.no_access_to_server);
            activity.runOnUiThread(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());

        }).addOnPostExecuteListener((r) -> model.setLoadData(false)).execute();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapFragment.onCustomerClickedListener = (v, c) -> {
          if (onCustomerClickedListener!=null)
              onCustomerClickedListener.onCustomerClicked(v,c);
        };
        listFragment.onCustomerClickedListener = (v, c) -> {
            if (onCustomerClickedListener!=null)
                onCustomerClickedListener.onCustomerClicked(v,c);
        };

            model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
            fragmentManager = getActivity().getSupportFragmentManager();
            model.getParams().observe(this, params1 -> {
                Log.i("DbTask", "this::updateCustomers");
                updateCustomers(params1);
            });


        activity = getActivity();
        context = getContext();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_screen_manger, container, false);
        if (savedInstanceState==null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_placeholder2, listFragment)
                    .commit();
        }

        screen = view.findViewById(R.id.fragment_placeholder2);

        Gson gson = new Gson();
        SharedPreferences sharedPref =activity.getPreferences(Context.MODE_PRIVATE);
        String json = sharedPref.getString(SortDialog.SORT_OPTION_TAG, gson.toJson(SortDialog.SortOptions.Balance));
        SortDialog.SortOptions options = gson.fromJson(json, SortDialog.SortOptions.class);
        model.sortBy(options);
        if (options== SortDialog.SortOptions.Distance) {
            PermissionMethods.updateLastLocation(activity, context,
                    R.string.location_request_msg1, screen,
                    location -> {
                        if (location != null)
                            model.setLocation(location);
                    });
        }
        model.setParams(new CustomerParams());
        return view;
    }

    public void notifyCacheChanged() {
        updateCustomers(params);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.customer_toolbar_menu, menu);
        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_bar).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setHintTextColor(Color.WHITE);
        ((ImageView)searchView.findViewById(androidx.appcompat.R.id.search_close_btn)).setColorFilter(Color.WHITE);
//        ((ImageView)searchView.findViewById(androidx.appcompat.R.id.search_plate)).setColorFilter(Color.WHITE);

        if (!query.equals(""))
            searchView.setQuery(query, true);
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newQuery) {
                query = newQuery;
                model.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                query = newQuery;
                model.filter(query);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.options: {
                SubMenu subMenu = item.getSubMenu();
                Fragment activeFragment  = fragmentManager.findFragmentById(R.id.fragment_placeholder2);
                boolean value = (activeFragment != mapFragment);
                subMenu.findItem(R.id.sort).setVisible(value);
                subMenu.findItem(R.id.switch_view).setVisible(value);
                subMenu.findItem(R.id.refresh).setVisible(!value);
                return true;
            }
            case R.id.filter: {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.addToBackStack(null);
                // Create and show the dialog.
                FilterDialog newFragment = FilterDialog.newInstance(params,
                        SystemVariables.getCardsGroups(), SystemVariables.GetSalesmen());
                newFragment.setOnApplyFiltersListener(customerParams -> model.setParams(params));
                newFragment.show(ft, "dialog");
                return true;
            }
            case R.id.sort: {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.addToBackStack(null);

                SortDialog sortDialog = SortDialog.newInstance();
                sortDialog.show(ft, "dialog");
                sortDialog.setOnSortOptionChoseListener(sortOptions -> {
                    model.sortBy(sortOptions);
                    if (sortOptions == SortDialog.SortOptions.Distance) {
                        PermissionMethods.updateLastLocation(activity, context,
                                R.string.location_request_msg1, screen,
                                location -> {
                                    if (location != null)
                                        model.setLocation(location);
                                });
                    }
                });
                return true;
            }
            case R.id.switch_view: {
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_placeholder2, mapFragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
            case R.id.refresh: {
                Log.i("DbTask","case R.id.refresh:");
                CachedDao db = new CachedDao();
                model.setLoadData(true);
                db.forceOnline().searchCustomers(params).addOnSuccessListener(customers ->{
                    model.updateCustomers(customers);
                    model.updateCustomers(customers);
                }).addOnFailureListener((de)->{
                    String msg;
                    if (de.getType() == DatabaseException.Type.TimeOut
                            && !PermissionMethods.isInternetAvailable())
                        msg = getString(R.string.no_internet_access);
                    else
                        msg = getString(R.string.no_access_to_server);
                    if (getActivity()!=null)
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());
                }).addOnPostExecuteListener((r)-> model.setLoadData(false))
                        .execute();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
