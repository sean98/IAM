//TODO check if can be deleted
package com.example.sean98.iam.Customers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sean98.iam.R;

import Models.Cards.Customer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerRecordView extends Fragment {

    private static int privateIcon = R.drawable.ic_home_black_24dp,
            companyIcon = R.drawable.ic_location_city_black_24dp;
    private CircleImageView iconType;
    private TextView name, group, city;


    @Nullable
    @Override
    public View getView() {
        return view;
    }

    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return view;
    }

    public void bindWith(Customer c){
        if (c.getType()==Customer.Type.Private)
            iconType.setImageResource(privateIcon);
        else
            iconType.setImageResource(companyIcon);

        name.setText(c.getName());
        group.setText(c.getGroup());
        city.setText(c.getBillingAddress().getCity());
    }
}
