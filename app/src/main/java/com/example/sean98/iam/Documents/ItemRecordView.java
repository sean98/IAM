package com.example.sean98.iam.Documents;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sean98.iam.R;
import com.google.android.material.textfield.TextInputEditText;

import Models.Documents.Item;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ItemRecordView extends Fragment {
    public static final String ITEM_KEY = "ITEM_KEY";
    private Item item;

    TextView numTextField;
    TextInputEditText descriptionTextField;
    TextInputEditText totalTextField;
    TextInputEditText heightTextField;
    TextInputEditText widthTextField;
    TextInputEditText lengthTextField;
    TextInputEditText unitTextField;
    TextInputEditText quantityTextField;
    TextInputEditText priceQuantityTextField;
    TextInputEditText detailsTextField;
    TextInputEditText commentsTextField;

    public static void Bind(View itemView , Item item){
        TextView numTextField = itemView.findViewById(R.id.numTextField);
        TextInputEditText descriptionTextField = itemView.findViewById(R.id.descriptionTextField);
        TextInputEditText totalTextField = itemView.findViewById(R.id.totalTextField);
        TextInputEditText heightTextField = itemView.findViewById(R.id.heightTextField);
        TextInputEditText widthTextField = itemView.findViewById(R.id.widthTextField);
        TextInputEditText lengthTextField = itemView.findViewById(R.id.lengthTextField);
        TextInputEditText unitTextField = itemView.findViewById(R.id.unitTextField);
        TextInputEditText quantityTextField = itemView.findViewById(R.id.quantityTextField);
        TextInputEditText priceQuantityTextField = itemView.findViewById(R.id.priceQuantityTextField);
        TextInputEditText detailsTextField = itemView.findViewById(R.id.detailsTextField);
        TextInputEditText commentsTextField = itemView.findViewById(R.id.commentsTextField);
        numTextField.setText(item.getNumber()+"");
        descriptionTextField.setText(item.getDescription());
        totalTextField.setText(item.getTotalPrice()+ item.getCurrency());
        heightTextField.setText(item.getHeight()+"");
        widthTextField.setText(item.getWidth()+"");
        lengthTextField.setText(item.getLength()+"");
        unitTextField.setText(item.getUnits()+"");
        quantityTextField.setText(item.getQuantity()+"");
        detailsTextField.setText(item.getDetails());
        commentsTextField.setText(item.getComments());
        priceQuantityTextField.setText(item.getPrice()+ item.getCurrency());

        if(item.getComments() == null || item.getComments().isEmpty())
            itemView.findViewById(R.id.commentsLayout).setVisibility(View.GONE);
        if(item.getDetails() == null || item.getDetails().isEmpty())
            itemView.findViewById(R.id.detailsLayout).setVisibility(View.GONE);
        if(item.getHeight()==0 && item.getWidth()==0 && item.getLength()==0 )
            itemView.findViewById(R.id.measuresLayout).setVisibility(View.GONE);

    }


    public ItemRecordView() {
        // Required empty public constructor
    }


    public static ItemRecordView newInstance(Item item) {
        ItemRecordView fragment = new ItemRecordView();
        Bundle args = new Bundle();
        args.putSerializable(ITEM_KEY, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        item = (Item)getArguments().getSerializable(ITEM_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_record_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        numTextField = view.findViewById(R.id.numTextField);
        descriptionTextField = view.findViewById(R.id.descriptionTextField);
        totalTextField = view.findViewById(R.id.totalTextField);
        heightTextField = view.findViewById(R.id.heightTextField);
        widthTextField = view.findViewById(R.id.widthTextField);
        lengthTextField = view.findViewById(R.id.lengthTextField);
        unitTextField = view.findViewById(R.id.unitTextField);
        quantityTextField = view.findViewById(R.id.quantityTextField);
        priceQuantityTextField = view.findViewById(R.id.priceQuantityTextField);
        detailsTextField = view.findViewById(R.id.detailsTextField);
        commentsTextField = view.findViewById(R.id.commentsTextField);
        Bind(view, item);

    }
}
