package com.example.sean98.iam.Customers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sean98.iam.Calenadar.Events.CreateEventFragment;
import com.example.sean98.iam.Customers.Finance.FinanceRecordListView;
import com.example.sean98.iam.Documents.DocumentTabs;
import com.example.sean98.iam.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import Models.Cards.Customer;
import Models.Company.SystemVariables;
import Models.util.Address;
import androidx.fragment.app.Fragment;


public class CustomerView extends Fragment {

    private static final String CUSTOMER_KEY = "CUSTOMER_KEY";
    private static final String EDITABLE_KEY = "EDITABLE_KEY";

    private TextView cellView;
    private TextView phone1_View;
    private TextView phone2_View;
    private TextView fax_View;
    private TextView emailView;
    private TextView billingAddressView;
    private TextView shippingAddressView;
    private TextInputEditText cidView;
    private TextInputEditText licTradNumView;
    private TextInputEditText balanceView;
    private TextInputEditText salesmanView;
    private TextInputEditText nameView;
    private TextInputEditText commentsTextView;

    private boolean editable = false;
    private Customer customer;

    private OnEditClickListener onEditClickListener;

    public interface OnEditClickListener {
        void OnEditClick(View view);
    }

    public CustomerView() {
        // Required empty public constructor
    }

    public static CustomerView newInstance(Customer customer, boolean editable) {
        CustomerView fragment = new CustomerView();
        Bundle args = new Bundle();
        args.putSerializable(CUSTOMER_KEY,customer);
        args.putBoolean(EDITABLE_KEY, editable);
        fragment.setArguments(args);
        return fragment;
    }

    public static CustomerView newInstance(Customer customer) {
        return newInstance(customer, false);
    }

    public void setOnEditClickListener(OnEditClickListener onEditClickListener) {
        this.onEditClickListener = onEditClickListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customer = (Customer) getArguments().getSerializable(CUSTOMER_KEY);
            editable = getArguments().getBoolean(EDITABLE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_customer_view, container, false);
        cellView = v.findViewById(R.id.cell_text);
        phone1_View =  v.findViewById(R.id.phone1_text);
        phone2_View =  v.findViewById(R.id.phone2_text);
        fax_View =  v.findViewById(R.id.fax_text);
        emailView = v. findViewById(R.id.email_text);
        billingAddressView =  v.findViewById(R.id.billing_address_text);
        shippingAddressView =  v.findViewById(R.id.shipping_address_text);
        cidView =  v.findViewById(R.id.code_text);
        licTradNumView =  v.findViewById(R.id.licTradNum_text);
        balanceView =  v.findViewById(R.id.balance_text);
        salesmanView =  v.findViewById(R.id.salesman_text);
        nameView =  v.findViewById(R.id.name_text);
        commentsTextView = v.findViewById(R.id.comments_text);


        MaterialButton viewFinanceHistoryButton = v.findViewById(R.id.view_finance_history_button);
        FloatingActionButton edit_fab = v.findViewById(R.id.edit_fab);


        (v.findViewById(R.id.addEventButton)).setOnClickListener(b-> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentLayout, CreateEventFragment.newInstance(customer))
                    .addToBackStack(null)
                    .commit();

        });

        (v.findViewById(R.id.showDocumentButton)).setOnClickListener((b)->
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentLayout,DocumentTabs.newInstance(customer))
                    .addToBackStack(null)
                    .commit());


        if (editable) {
            edit_fab.show();
            edit_fab.setOnClickListener(view -> {
                if (onEditClickListener!=null)
                    onEditClickListener.OnEditClick(view);
            });
        }
        else
            edit_fab.hide();

        showCustomer(customer);
        cellView.setOnClickListener(this::phoneClicked);
        phone1_View.setOnClickListener(this::phoneClicked);
        phone2_View.setOnClickListener(this::phoneClicked);
        fax_View.setOnClickListener(this::phoneClicked);
        emailView.setOnClickListener(this::emailClicked);
        billingAddressView.setOnClickListener(this::addressClicked);
        shippingAddressView.setOnClickListener(this::addressClicked);
        viewFinanceHistoryButton.setOnClickListener((view)->
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentLayout, FinanceRecordListView.newInstance(customer))
                    .addToBackStack(null)
                    .commit());

        return v;
    }




    public void phoneClicked(View view){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + ((TextView)view).getText().toString() ));
        startActivity(intent);
    }

    public void emailClicked(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", ((TextView)view).getText().toString(), null));
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void showCustomer(Customer customer) {
        nameView.setText(customer.getName());
        commentsTextView.setText(customer.getComments());
        licTradNumView.setText(customer.getLicTradNum());
        balanceView.setText(customer.getBalance()+" "+ SystemVariables.GetSystemCurrency());
        salesmanView.setText(customer.getSalesman().getName());
        cellView.setText(customer.getCellular());
        phone1_View.setText(customer.getPhone1()!=null?customer.getPhone1().trim():null);
        phone2_View.setText(customer.getPhone2()!=null?customer.getPhone2().trim():null);
        fax_View.setText(customer.getFax()!=null?customer.getFax().trim():null);
        emailView.setText(customer.getEmail()!=null?customer.getEmail().trim():null);
        billingAddressView.setText(customer.getBillingAddress().toString());
        billingAddressView.setTag(customer.getBillingAddress());
        shippingAddressView.setText(customer.getShippingAddress().toString());
        shippingAddressView.setTag(customer.getShippingAddress());
        cidView.setText(customer.getCid());

        if (cellView.getText().toString().trim().isEmpty())
            cellView.setEnabled(false);
        if (phone1_View.getText().toString().trim().isEmpty())
            phone1_View.setEnabled(false);
        if (phone2_View.getText().toString().trim().isEmpty())
            phone2_View.setEnabled(false);
        if (fax_View.getText().toString().trim().isEmpty())
            fax_View.setEnabled(false);
        if (emailView.getText().toString().trim().isEmpty())
            emailView.setEnabled(false);

        Address tmp = customer.getBillingAddress();
        if (tmp==null || tmp.equals(new Address()))
            billingAddressView.setEnabled(false);
        tmp = customer.getBillingAddress();
        if (tmp==null || tmp.equals(new Address()))
            shippingAddressView.setEnabled(false);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void addressClicked(View view) {
        String address = null;
        if (view == billingAddressView)
            address = customer.getBillingAddress().toStringFormat("c , s n");
        else if (view == shippingAddressView)
            address = customer.getShippingAddress().toStringFormat("c , s n");
        if (address != null && !address.trim().isEmpty()) {
            Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address));
            startActivity(geoIntent);
        }
    }
}
