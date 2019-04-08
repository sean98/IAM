package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sean98.iam.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import Models.Cards.Customer;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerViewAdapter
        extends RecyclerView.Adapter<CustomerViewAdapter.CustomerViewHolder> {

    private List<Customer> customersList;
    public OnCustomerViewClickListener onCustomerViewClickListener;

    public interface OnCustomerViewClickListener {
        void onCustomerViewClick(View view, Customer customer);
    }

    public CustomerViewAdapter() {
        updateDataSet(new ArrayList<>());
    }

    public void updateDataSet(List<Customer> customersList) {
        this.customersList = customersList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_customer_record_view, parent,false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer c =  customersList.get(position);
        holder.bindWith(c);
        holder.parentLayout.setOnClickListener((view) -> {
            if (onCustomerViewClickListener != null)
                onCustomerViewClickListener.onCustomerViewClick(holder.getItemView(), c);
        });
    }

    @Override
    public int getItemCount() {
        //return customersListFiltered.size();
        return customersList.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        private static int privateIcon = R.drawable.ic_home_black_24dp,
                companyIcon = R.drawable.ic_location_city_black_24dp;

        private CircleImageView iconType;
        private TextView name, group, city;
        private MaterialCardView parentLayout;
        private View itemView;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            parentLayout =itemView.findViewById(R.id.cardView);
            iconType = itemView.findViewById(R.id.type_image);
            name = itemView.findViewById(R.id.name_title_text);
            group = itemView.findViewById(R.id.group_text);
            city = itemView.findViewById(R.id.city_text);
        }

        public View getItemView() {
            return itemView;
        }

        public void bindWith(Customer c) {
            if (c.getType()==Customer.Type.Private)
                iconType.setImageResource(privateIcon);
            else
                iconType.setImageResource(companyIcon);
            name.setText(c.getName());
            group.setText(c.getGroup());
            city.setText(c.getBillingAddress().getCity());
        }
    }
}
