package com.example.sean98.iam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sean98.iam.Customers.CustomerEditFragment;
import com.example.sean98.iam.Customers.CustomerView;

import Databases.CachedDao;
import Databases.Exceptions.DatabaseException;
import Databases.ICustomerDao;
import Databases.util.DbTask;
import Models.Cards.Customer;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import permmisionModels.PermissionMethods;

public class CustomerActivity extends LocaleActivity {

    public static final String CUSTOMER_KEY = "CUSTOMER_KEY";
    public static final String BUNDLE_KEY = "BUNDLE_KEY";
    public static final String RESULT_KEY = "RESULT_KEY";

    private TextView nameTitleView;
    private TextView groupView;
    private TextView cityView;
    private CircleImageView typeImage;
    //private FloatingActionButton fab;

    //private CustomerViewVM model;
    private CustomerView customerView;
    private Customer customer;

    private boolean add = false;
    private boolean customerUpdated = false;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CUSTOMER_KEY, customer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        if (savedInstanceState==null) {
            customer = (Customer) getIntent().getBundleExtra(BUNDLE_KEY)
                    .getSerializable(CUSTOMER_KEY);
        }
        else
            customer = (Customer)savedInstanceState.getSerializable(CUSTOMER_KEY);

        customerView = CustomerView.newInstance(customer, true);
        customerView.setOnEditClickListener(this::onActionButtonClicked);

        nameTitleView = findViewById(R.id.name_title_text);
        groupView = findViewById(R.id.group_text);
        cityView = findViewById(R.id.city_text);
        typeImage = findViewById(R.id.type_image);
        //fab = findViewById(R.id.fab);

        showCustomer(customer);

        if (savedInstanceState==null) {
            if (customer!=null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, customerView)
                        .commit();
            }
            else {
                add = true;
                CustomerEditFragment customerEdit = CustomerEditFragment.newInstance(new Customer());
                customerEdit.setOnSaveClickListener(this::onActionButtonClicked);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, customerEdit)
                        .commit();
            }
        }
        else {
            Fragment activeFragment = getSupportFragmentManager()
                    .findFragmentById(R.id.fragmentContainer);
            if (activeFragment instanceof CustomerEditFragment)
                ((CustomerEditFragment)activeFragment).setOnSaveClickListener(this::onActionButtonClicked);
            else
                ((CustomerView)activeFragment).setOnEditClickListener(this::onActionButtonClicked);
        }
    }

    private void showCustomer(Customer customer) {
        if (customer==null) {
            nameTitleView.setText("");
            groupView.setText("");
            cityView.setText("");
            typeImage.setImageDrawable(null);
            return;
        }
        this.customer = customer;
        nameTitleView.setText(customer.getName());
        groupView.setText(customer.getGroup());
        cityView.setText(customer.getBillingAddress().getCity());
        if (customer.getType()== Customer.Type.Private)
            typeImage.setImageResource(R.drawable.ic_home_black_24dp);
        else
            typeImage.setImageResource(R.drawable.ic_location_city_black_24dp);
    }

    public void onActionButtonClicked(View view) {
        Fragment activeFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);
        if (activeFragment instanceof CustomerView) {
            CustomerEditFragment customerEdit = CustomerEditFragment.newInstance(customer);
            customerEdit.setOnSaveClickListener(this::onActionButtonClicked);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, customerEdit)
                    .addToBackStack(null)
                    .commit();
        }
        else {
            if (view != null) {
                Customer tmp = ((CustomerEditFragment) activeFragment).save();
                if (tmp != null)
                    updateCustomer(tmp);
                customerView = CustomerView.newInstance(customer, true);
                customerView.setOnEditClickListener(this::onActionButtonClicked);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment activeFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);
        if (activeFragment instanceof CustomerView) {
            Intent data = new Intent();
            data.putExtra(RESULT_KEY, customerUpdated);
            setResult(RESULT_OK, data);
        }
        super.onBackPressed();
    }



    public void updateCustomer(Customer customer) {
        if (customer==null)
            return;
        customerUpdated = true;
        showCustomer(customer);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.authenticating) + "...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        ICustomerDao db = new CachedDao();
        DbTask<Customer> task;
        if (add)
            task = db.addCustomer(customer);
        else
            task = db.updateCustomer(customer);
        task.addOnSuccessListener((c)->{
            runOnUiThread(() -> {
                Toast.makeText(this, getString(R.string.customer_updated), Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, customerView)
                        .commit();
            });
        }).addOnFailureListener(e->{
            String msg;
            if (e.getType() == DatabaseException.Type.TimeOut
                    && !PermissionMethods.isInternetAvailable())
                msg = getString(R.string.no_internet_access);
            else
                msg = getString(R.string.no_access_to_server);
            onActionButtonClicked(null);
            runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
            e.printStackTrace();
        }).addOnPostExecuteListener(r->runOnUiThread(progressDialog::dismiss))
        .execute();
    }
}
