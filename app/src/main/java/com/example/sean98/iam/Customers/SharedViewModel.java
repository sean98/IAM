package com.example.sean98.iam.Customers;

import android.location.Location;

import com.example.sean98.iam.Dialogs.SortDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Models.Cards.Customer;
import Models.Cards.CustomerParams;
import Models.util.SapLocation;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private List<Customer> customers;
    private MutableLiveData<List<Customer>> filteredCustomers = new MutableLiveData<>();
    private String query = "";

    private MutableLiveData<Boolean> loadData = new MutableLiveData<>();
    private MutableLiveData<SapLocation> location = new MutableLiveData<>();
    private SortDialog.SortOptions lastSort;

    private MutableLiveData<CustomerParams> params = new MutableLiveData<>();

    public void setLocation(Location location) {
        this.location.setValue(new SapLocation(location));
        sortBy(lastSort);
    }

    public void setParams(CustomerParams params) {
        this.params.postValue(params);
    }

    public LiveData<CustomerParams> getParams() {
        return params;
    }

    public LiveData<SapLocation> getLocation() {
        return location;
    }

    public void setLoadData(boolean value) {
        loadData.postValue(value);
    }

    public LiveData<Boolean> getLoadData() {
        return loadData;
    }

    public LiveData<List<Customer>> getCustomers() {
        return filteredCustomers;
    }

    public void updateCustomers(List<Customer> customers) {
        this.customers = customers;
        sortBy(lastSort);
        //filter(query);
    }

    public void filter(CharSequence charSequence) {
        query = charSequence.toString().toLowerCase();
        if (query.isEmpty()) {
            filteredCustomers.postValue(customers);
        } else {
            List<Customer> filteredList = new ArrayList<>();
            for (Customer c : customers) {
                // name match condition. this might differ depending on your requirement
                // here we are looking for name or address match
                if (c.getBillingAddress().toString().toLowerCase().contains(query)
                        || c.getName().toLowerCase().contains(query)) {
                    filteredList.add(c);
                }
            }
            filteredCustomers.postValue(filteredList);
        }
    }

    public void sortBy(SortDialog.SortOptions options) {
        lastSort = options;
        if (lastSort!=null && customers!=null) {
            List<Customer> tmpList = new ArrayList<>(customers);
            switch (options) {
                case Alphabetical:
                    Collections.sort(tmpList, (c1, c2) -> {
                        String name1 = c1.getName();
                        String name2 = c2.getName();
                        if (name1==null) {
                            if (name2==null)
                                return 0;
                            return 1;
                        }
                        else if (name1==null) return -1;
                        return name1.compareTo(name2);
                    });
                    break;
                case City:
                    Collections.sort(tmpList, (c1, c2) -> {
                        String city1 = c1.getBillingAddress().getCity();
                        String city2 = c2.getBillingAddress().getCity();
                        if (city1==null) {
                            if (city2==null)
                                return 0;
                            return 1;
                        }
                        else if (city2==null) return -1;
                        return city1.compareTo(city2);
                    });
                    break;
                case Balance:
                    Collections.sort(tmpList, (c1, c2) -> -((Float) c1.getBalance())
                            .compareTo(c2.getBalance()));
                    break;
                case Distance:
                    if (location.getValue() != null) {
                        Collections.sort(tmpList, (c1, c2) -> {
                            SapLocation l1 = c1.getLocation();
                            SapLocation l2 = c2.getLocation();
                            if (l1==null) {
                                if (l2==null)
                                    return 0;
                                return 1;
                            }
                            else if (l2==null) return -1;
                            double distanceA = location.getValue().distanceTo(l1);
                            double distanceB = location.getValue().distanceTo(l2);
                            if (distanceA < distanceB) return -1;
                            if (distanceA > distanceB) return 1;
                            return 0;
                        });
                    }
                    break;
            }
            customers = tmpList;
            filter(query);
        }
    }
}
