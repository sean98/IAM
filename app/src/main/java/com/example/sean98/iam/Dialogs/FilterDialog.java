package com.example.sean98.iam.Dialogs;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.sean98.iam.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Models.Cards.Customer;
import Models.Cards.CustomerParams;
import Models.Company.SalesMan;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FilterDialog extends DialogFragment {

    private static final String GROUPS_TAG = "GROUPS_TAG";
    private static final String CUSTOMER_PARAMS_TAG = "CUSTOMER_PARAMS_TAG";
    private static final String SALES_MEN_TAG = "SALES_MEN_TAG";
    private OnApplyFiltersListener onApplyFiltersListener = null;

    public interface OnApplyFiltersListener {
        void onApplyFilters(CustomerParams customerParams);
    }

    public void setOnApplyFiltersListener(OnApplyFiltersListener onApplyFiltersListener) {
        this.onApplyFiltersListener = onApplyFiltersListener;
    }

    private List<String> groups;
    private List<SalesMan> salesMEN;
    private CustomerParams customerParams;

    public FilterDialog(){
        //Empty Constructor for fragment
    }

    public static FilterDialog newInstance(CustomerParams customerParams, List<String> groupList,
                                           List<SalesMan> salesMEN) {
        FilterDialog filterDialog = new FilterDialog();


        Bundle args = new Bundle();
        if (salesMEN!=null)
            args.putSerializable(SALES_MEN_TAG, new ArrayList<>(salesMEN));
        if (groupList!=null)
            args.putStringArrayList(GROUPS_TAG, new ArrayList<>(groupList));
        //args.putSerializable(CUSTOMER_PARAMS_TAG, customerParams);

        //CustomerParams tmp = new CustomerParams(customerParams);
        // TODO solve copy of params to allow cancel operation without affecting search
        // also resolve checkbox search logic
        args.putSerializable(CUSTOMER_PARAMS_TAG, new CustomerParams(customerParams));

        filterDialog.setArguments(args);

        return filterDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        groups = args.getStringArrayList(GROUPS_TAG);
        salesMEN = (ArrayList<SalesMan>)args.getSerializable(SALES_MEN_TAG);
        customerParams = (CustomerParams)args.getSerializable(CUSTOMER_PARAMS_TAG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       getDialog().getWindow().setBackgroundDrawable(new ColorDrawable());
       View view = inflater.inflate(R.layout.dilaog_filter, container);

        view.findViewById(R.id.apply_button).setOnClickListener(btn -> {
            if (onApplyFiltersListener!=null)
                onApplyFiltersListener.onApplyFilters(customerParams);
            this.dismiss();
        });

        view.findViewById(R.id.cancel_button).setOnClickListener(btn -> this.dismiss());

        GridLayout groupCheckBoxContainer = view.findViewById(R.id.group_checkbox_container);
        createElemnt(String.class, groups, customerParams.groups, groupCheckBoxContainer);

        GridLayout salesmanCheckBoxContainer = view.findViewById(R.id.salesman_checkbox_container);
        createElemnt(SalesMan.class, salesMEN, customerParams.salesmen, salesmanCheckBoxContainer);

        GridLayout statusGridContainer = view.findViewById(R.id.status_checkbox_container);
        createElemnt(Customer.Status.class,
                Arrays.asList(Customer.Status.Valid, Customer.Status.Frozen),
                customerParams.statuses, statusGridContainer);

        GridLayout typeGridContainer = view.findViewById(R.id.type_checkbox_container);
        createElemnt(Customer.Type.class,
                Arrays.asList(Customer.Type.Private, Customer.Type.Company),
                customerParams.types, typeGridContainer);
        return view;
    }

    private <E> void createElemnt(Class<E> eClass, List<E> elements, List<E> elementsGroup, GridLayout container) {
        if (elements==null) {
            TextView tv = new TextView(getContext());
            tv.setText(R.string.information_not_available);
            container.addView(tv);
            return;
        }
        for (E e : elements) {
            CheckBox cb = new CheckBox(getContext());
            cb.setText(e.toString().trim());
            cb.setTag(e);
            if (elementsGroup.contains(e))
                cb.setChecked(true);
            cb.setOnCheckedChangeListener((button, checked) -> {
                if (checked)
                    elementsGroup.add(eClass.cast(button.getTag()));
                else
                    elementsGroup.remove(eClass.cast(button.getTag()));
            });
            container.addView(cb);
            ((GridLayout.LayoutParams) cb.getLayoutParams()).columnSpec =
                    GridLayout.spec(GridLayout.UNDEFINED, 1f);
        }
    }

}
