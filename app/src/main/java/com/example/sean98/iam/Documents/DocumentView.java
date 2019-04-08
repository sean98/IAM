package com.example.sean98.iam.Documents;

import android.os.Bundle;

import Databases.Exceptions.DatabaseException;
import Databases.SAP.SapMSSQL;
import Models.Documents.Document;
import Models.Documents.Item;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import permmisionModels.PermissionMethods;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sean98.iam.R;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.List;


public class DocumentView extends Fragment {
    public static final String DOCUMENT_KEY= "DOCUMENT_KEY";

    private LinearLayout itemListView;
    private List<Item> itemList;

    public static void Bind(View documentView, Document document){
        View v = documentView;
        AppCompatImageView isCloseIcon = v.findViewById(R.id. isCloseIcon);
        AppCompatImageView isCanceledIcon = v.findViewById(R.id. isCanceledIcon);
        TextInputEditText cardCodeTextField = v.findViewById(R.id.cardCodeTextField);
        TextInputEditText cardNameTextField = v.findViewById(R.id.cardNameTextField);
        TextInputEditText docSnField = v.findViewById(R.id.docSnField);
        TextInputEditText docDateField = v.findViewById(R.id.docDueDateField);
        TextInputEditText docValidDateField = v.findViewById(R.id.docValidDateField);
        TextInputEditText docTotalBeforeDCField = v.findViewById(R.id.docTotalBeforeDCField);
        TextInputEditText docDCField = v.findViewById(R.id.docDCField);
        TextInputEditText docVATTextField = v.findViewById(R.id.docVATTestField);
        TextInputEditText docTotalTextField = v.findViewById(R.id.docTotalTestField);
        TextInputEditText docSalesmanField = v.findViewById(R.id.docSalesmanField);
        TextInputEditText docCommentsTextField = v.findViewById(R.id.docCommentsTextField);
        TextInputEditText docTotalPaidSum = v.findViewById(R.id.docTotalPaidSum);
        TextInputEditText docTotalNeedToPay = v.findViewById(R.id.docTotalNeedToPay);


        ((TextView)v.findViewById(R.id.docTypeTextView)).setText(document.getType().toString());
        isCloseIcon.setVisibility(document.isClosed()?View.VISIBLE:View.GONE);
        isCanceledIcon.setVisibility(document.isCanceled()?View.VISIBLE:View.GONE);
        cardCodeTextField.setText(document.getCustomer().getCid());
        cardNameTextField.setText(document.getCardName());
        docSnField.setText(document.getSn()+"");
        docDateField.setText(new SimpleDateFormat("dd-MM-yy").
                format(document.getDocDate()));
        docValidDateField.setText(new SimpleDateFormat("dd-MM-yy").
                format(document.getDocDueDate()));//TODO check date name
        docTotalBeforeDCField.setText((document.getTotal()-document.getVatSum()+document.getDiscountSum())+document.getCurrency());
        docDCField.setText(document.getDiscountSum()+document.getCurrency());
        docVATTextField.setText(document.getVatSum()+document.getCurrency());
        docTotalTextField.setText(document.getTotal()+ document.getCurrency());
        docTotalNeedToPay.setText((document.getTotal()-document.getPaidSum())+document.getCurrency());
        docSalesmanField.setText(document.getSalesMan().getName());
        docTotalPaidSum.setText(document.getPaidSum()+document.getCurrency());

        docCommentsTextField.setText(document.getComments());
    }

    private Document document;
    public DocumentView() {
        // Required empty public constructor
    }

    public static DocumentView newInstance(Document document) {
        DocumentView fragment = new DocumentView();
        Bundle args = new Bundle();
        args.putSerializable(DOCUMENT_KEY,document);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.document =(Document) getArguments().getSerializable(DOCUMENT_KEY);
            onItemsLoadedListener = (items)->{
                if(itemListView!= null &&  getActivity()!=null){
                    getActivity().runOnUiThread(()->{
                        itemListView.removeAllViews();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        if(itemList != null){
                            for(Item item : itemList){
                                ItemRecordView itemRecordView = ItemRecordView.newInstance(item);
                                ft.add(R.id.item_records_list,itemRecordView, item.getNumber()+"");
                            }
                            if(itemList.size()>0)
                                ft.commit();
                        }
                    });
                }
            };
            //updateItemsList();
            SapMSSQL sapMSSQLTry = new SapMSSQL();

            sapMSSQLTry.getDocumentItems(document).addOnSuccessListener((items)-> {
                itemList = items;
                if (onItemsLoadedListener != null)
                    onItemsLoadedListener.onItemsLoaded(itemList);
            }).addOnFailureListener((de)-> {
                String msg;
                if (de.getType() == DatabaseException.Type.TimeOut
                        && PermissionMethods.isInternetAvailable())
                    msg = getString(R.string.no_internet_access);
                else
                    msg = getString(R.string.no_access_to_server);
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                        msg, Toast.LENGTH_SHORT).show());
            }).execute();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemListView = view.findViewById(R.id.item_records_list);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(itemList != null){
            itemListView.removeAllViews();
            for(Item item : itemList){
                ItemRecordView itemRecordView = ItemRecordView.newInstance(item);
                ft.add(R.id.item_records_list,itemRecordView, item.getNumber()+"");
            }
            if(itemList.size()>0)
                ft.commit();
        }
        else{
            //create loading progress bar
            ProgressBar loadingItemsBar = new ProgressBar(getContext(),
                    null, android.R.attr.progressBarStyleSmall);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100,100);
            params.setMargins(10,10,10,10);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            loadingItemsBar.setLayoutParams(params);
            itemListView.addView(loadingItemsBar);
        }
        Bind(view,document);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_document_view, container, false);
    }





    private OnItemsLoadedListener onItemsLoadedListener;

    interface OnItemsLoadedListener{
        void onItemsLoaded(List<Item> itemList);
    }






}
