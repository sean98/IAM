package com.example.sean98.iam.Customers;

import Models.Cards.Customer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CustomerViewVM extends ViewModel {

    private MutableLiveData<Customer> customer = new MutableLiveData<>();

    public void setCustomer(Customer customer) {
        this.customer.postValue(customer);
    }

    public LiveData<Customer> getCustomer() {
        return customer;
    }
}
