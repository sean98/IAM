package com.example.sean98.iam.Documents;

import android.os.Bundle;

import Adapters.DocumentRecordViewAdapter;
import Databases.IDocumentsDao;
import Databases.util.DbTask;
import Databases.Exceptions.DatabaseException;
import Databases.SAP.SapMSSQL;
import Models.Cards.Customer;
import Models.Documents.Document;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import permmisionModels.PermissionMethods;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sean98.iam.R;

import java.util.List;


public class DocumentsListView extends Fragment {

    private static final String CUSTOMER_KEY = "CUSTOMER_KEY";
    public static final  String TYPE_KEY = "TYPE_KEY";
    public DocumentsListView() {
        // Required empty public constructor
    }

    private RecyclerView listView;
    private SwipeRefreshLayout refreshLayout;
    private List<Document> documentList ;

    private Document.Type type;
    private Customer customer;
    private DocumentRecordViewAdapter documentRecordViewAdapter;



    public static DocumentsListView newInstance(Customer customer,Document.Type type) {
        DocumentsListView fragment = new DocumentsListView();
        Bundle args = new Bundle();
        args.putSerializable(TYPE_KEY,type);
        args.putSerializable(CUSTOMER_KEY,customer);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = (Document.Type)getArguments().getSerializable(TYPE_KEY);
            customer = (Customer) getArguments().getSerializable(CUSTOMER_KEY);
            updateRefreshDocumentsList();
        }
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_documents_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout = view.findViewById(R.id.docs_refreshLayout);
        listView = view.findViewById(R.id.docs_records_list);

        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        listView.setLayoutManager(lm);
        refreshLayout.setRefreshing(true);
        refreshLayout.setOnRefreshListener(this::updateRefreshDocumentsList);
        if(documentList!=null){
            listView.setAdapter(documentRecordViewAdapter);
            refreshLayout.setRefreshing(false);
            documentRecordViewAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public void updateRefreshDocumentsList() {

        if (refreshLayout != null && getActivity() != null)
            getActivity().runOnUiThread(() -> refreshLayout.setRefreshing(true));
        IDocumentsDao db = new SapMSSQL();
        DbTask<List<Document>> task;
        switch (type) {
            case Invoice:
                task = db.getInvoices(customer);
                break;
            case Order:
                task = db.getOrders(customer);
                break;
            case SaleQuotation:
                task = db.getOffers(customer);
                break;
            default:
                RevocationInvoice:
                task = db.getRevokedInvoices(customer);
                break;
        }
        task.addOnSuccessListener(documentList -> {
            this.documentList = documentList;

            if(documentRecordViewAdapter == null) {
                documentRecordViewAdapter = new DocumentRecordViewAdapter(documentList);
                documentRecordViewAdapter.setOnDocumentClickedListener((v,doc)->onDocumentClicked(doc));
            }
            else
                documentRecordViewAdapter.updateDataSet(documentList);
            if (getActivity()!= null  )
                getActivity().runOnUiThread(() -> {
                    if(listView != null && listView.getAdapter() == null)
                        listView.setAdapter(documentRecordViewAdapter);
                    documentRecordViewAdapter.notifyDataSetChanged();
                });


        }).addOnFailureListener(de -> {
            String msg;
            if (de.getType() == DatabaseException.Type.TimeOut
                    && PermissionMethods.isInternetAvailable())
                msg = getString(R.string.no_internet_access);
            else
                msg = getString(R.string.no_access_to_server);
            if (getActivity() != null)
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                        msg, Toast.LENGTH_SHORT).show());
        }).addOnPostExecuteListener(r -> {
            if(getActivity()!= null)
                getActivity().runOnUiThread(() ->
                refreshLayout.setRefreshing(false));})

        .execute();
    }


    private void onDocumentClicked(Document document){
        if (document!= null && getActivity()!= null){
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentLayout,DocumentView.newInstance(document));
            ft.addToBackStack(null);
            ft.commit();
        }
    }
}
