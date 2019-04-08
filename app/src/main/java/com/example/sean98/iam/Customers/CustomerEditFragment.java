package com.example.sean98.iam.Customers;

import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sean98.iam.Dialogs.SetAddressDialog;
import com.example.sean98.iam.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Models.Cards.Customer;
import Models.Company.SalesMan;
import Models.Company.SystemVariables;
import Models.util.Address;
import Models.util.SapLocation;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CustomerEditFragment extends Fragment {

    private static final String CUSTOMER_KEY = "CUSTOMER_KEY";

    private TextView cellView;
    private TextView phone1_View;
    private TextView phone2_View;
    private TextView fax_View;
    private TextView emailView;
    private TextView billingAddressView;
    private TextView shippingAddressView;
    private TextInputEditText cidView;
    private TextInputEditText licTradNumView;
    private TextInputEditText nameView;
    private TextInputEditText commentsTextView;
    private FloatingActionButton save_fab;
    private Spinner salesmenSpinner;
    private Spinner groupSpinner;
    private Spinner typeSpinner;

    private List<SalesMan> salesmen;
    private List<String> groups;
    private List<Customer.Type> types;
    private Customer customer;

    private OnSaveClickListener onSaveClickListener;

    public interface OnSaveClickListener {
        void OnSaveClick(View view);
    }

    public void setOnSaveClickListener(OnSaveClickListener onSaveClickListener) {
        this.onSaveClickListener = onSaveClickListener;
    }

    public CustomerEditFragment() {
    }

    public static CustomerEditFragment newInstance(Customer customer) {
        CustomerEditFragment fragment = new CustomerEditFragment();
        Bundle args = new Bundle();
        args.putSerializable(CUSTOMER_KEY,customer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CUSTOMER_KEY, customer);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState==null)
            customer = (Customer)getArguments().getSerializable(CUSTOMER_KEY);
        else
            customer = (Customer)savedInstanceState.getSerializable(CUSTOMER_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_customer_edit_mode, container, false);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction()==KeyEvent.ACTION_UP) {
                new AlertDialog.Builder(getContext())
                        .setMessage(R.string.leave_without_save)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, (dialogInterface, i1) -> {
                            dialogInterface.dismiss();
                            getActivity().onBackPressed();
                        }).setNegativeButton(android.R.string.no, (dialogInterface, i1) -> dialogInterface.dismiss())
                        .create().show();
                return true;
            }
            return false;
        });
        cellView = v.findViewById(R.id.cell_text);
        phone1_View =  v.findViewById(R.id.phone1_text);
        phone2_View =  v.findViewById(R.id.phone2_text);
        fax_View =  v.findViewById(R.id.fax_text);
        emailView = v. findViewById(R.id.email_text);
        billingAddressView =  v.findViewById(R.id.billing_address_text);
        shippingAddressView =  v.findViewById(R.id.shipping_address_text);
        cidView =  v.findViewById(R.id.code_text);
        licTradNumView =  v.findViewById(R.id.licTradNum_text);
        nameView =  v.findViewById(R.id.name_text);
        commentsTextView = v.findViewById(R.id.comments_text);
        save_fab = v.findViewById(R.id.save_fab);
        save_fab.setOnClickListener(view -> {
            if (validicityCheck()) {
                //Todo check data validicty
                new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.check_before_save))
                        .setPositiveButton(R.string.ok, (dialogInterface, i) ->
                                onSaveClickListener.OnSaveClick(save_fab))
                        .setNegativeButton(R.string.cancel, null)
                        .create().show();
            }
        });


        billingAddressView.setOnClickListener(this::addressClicked);
        shippingAddressView.setOnClickListener(this::addressClicked);
        salesmenSpinner = v.findViewById(R.id.salesman_spinner);
        salesmen = new ArrayList<>(SystemVariables.GetSalesmen());
        ArrayList<String> salesmenNames = new ArrayList<>();
        salesmenNames.add(getString(R.string.salesman));
        for (SalesMan sm : salesmen)
            salesmenNames.add(sm.getName());
        ArrayAdapter<String> salesMenAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, salesmenNames);
        salesMenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        salesmenSpinner.setAdapter(salesMenAdapter);
        salesmenSpinner.setSelection(0);

        groupSpinner = v.findViewById(R.id.group_spinner);
        groups = new ArrayList<>(SystemVariables.getCardsGroups());
        List<String> groupsName = new ArrayList<>();
        groupsName.add(getString(R.string.group));
        for (String g : groups)
            groupsName.add(g);
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, groupsName);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(groupAdapter);
        groupSpinner.setSelection(0);

        typeSpinner = v.findViewById(R.id.type_spinner);
        types = Arrays.asList(Customer.Type.Company, Customer.Type.Private);
        List<String> typesName = new ArrayList<>();
        typesName.add(getString(R.string.type));
        for (Customer.Type t : types)
            typesName.add(t.toString());
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, typesName);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setSelection(0);

        showCustomer(customer);//(Customer)getArguments().getSerializable(CUSTOMER_KEY));

        return v;
    }


    public void showCustomer(Customer customer) {
        this.customer = customer;
        nameView.setText(customer.getName());
        commentsTextView.setText(customer.getComments());
        licTradNumView.setText(customer.getLicTradNum());
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
        salesmenSpinner.setSelection(1+salesmen.indexOf(customer.getSalesman()));
        groupSpinner.setSelection(1+groups.indexOf(customer.getGroup()));
        typeSpinner.setSelection(1+types.indexOf(customer.getType()));
    }


    private void addressClicked(View view) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);

        Address address = (Address)view.getTag();

        // Create and show the dialog.
        SetAddressDialog dialog;
        if (address!=null)
            dialog= SetAddressDialog.newInstance(address);
        else
            dialog = SetAddressDialog.newInstance();
        dialog.setOnAddressSetListener((newAddress)-> {
            view.setTag(newAddress);
            ((TextView)view).setText(newAddress.toString());
            try {
                Geocoder coder = new Geocoder(getContext());
                String adrStr = newAddress.toStringFormat("C, c, s n");
                List<android.location.Address> addressList = coder.getFromLocationName(adrStr, 1);
                if (addressList != null && addressList.size() > 0) {
                    double lat = addressList.get(0).getLatitude();
                    double lng = addressList.get(0).getLongitude();
                    customer.setLocation(new SapLocation(lat, lng));
                    if (view == billingAddressView)
                        ((Address) view.getTag()).setLocation(new SapLocation(lat, lng));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } // end catch
        });
        dialog.show(ft, "dialog");
    }

    public Customer save() {
        customer.setCellular(cellView.getEditableText().toString());
        customer.setPhone1(phone1_View.getEditableText().toString());
        customer.setPhone2(phone2_View.getEditableText().toString());
        customer.setFax(fax_View.getEditableText().toString());
        customer.setEmail(emailView.getEditableText().toString());
        customer.setBillingAddress((Address) billingAddressView.getTag());
        customer.setShippingAddress((Address) shippingAddressView.getTag());
        customer.setLicTradNum(licTradNumView.getEditableText().toString());
        customer.setName(nameView.getEditableText().toString());
        customer.setComments(commentsTextView.getText().toString());
        customer.setSalesman(salesmen.get(salesmenSpinner.getSelectedItemPosition()-1));
        customer.setGroup(groups.get(groupSpinner.getSelectedItemPosition()-1));
        customer.setType(types.get(typeSpinner.getSelectedItemPosition()-1));
        return  customer;
    }

    private boolean validicityCheck() {
        boolean check = true;
        Address billing = (Address)billingAddressView.getTag();
        if (billing==null || billing.equals(new Address())) {
            check = false;
            billingAddressView.setError(getContext().getString(R.string.error_address_name_empty));
        }
        if (salesmenSpinner.getSelectedItemPosition()==0) {
            check=false;
            ((TextView)salesmenSpinner.getSelectedView()).setTextColor(Color.RED);
        }
        if (groupSpinner.getSelectedItemPosition()==0) {
            check=false;
            ((TextView)groupSpinner.getSelectedView()).setTextColor(Color.RED);
        }
        if (typeSpinner.getSelectedItemPosition()==0) {
            check=false;
            ((TextView)typeSpinner.getSelectedView()).setTextColor(Color.RED);
        }
        if (licTradNumView.getEditableText().toString().trim().equals("")) {
            check=false;
            licTradNumView.setError(getContext().getString(R.string.error_address_name_empty));
        }
        if (cellView.getEditableText().toString().equals("")
                && phone1_View.getEditableText().toString().equals("")
                && phone2_View.getEditableText().toString().equals("")) {
            check=false;
            Toast.makeText(getActivity(), getString(R.string.at_least_one_phone), Toast.LENGTH_LONG).show();
        }
        else if(!check)
            Toast.makeText(getActivity(), getString(R.string.fields_not_valid), Toast.LENGTH_LONG).show();

        return check;
    }
}
