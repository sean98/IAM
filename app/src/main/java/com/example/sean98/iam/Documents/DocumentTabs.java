package com.example.sean98.iam.Documents;

import android.os.Bundle;

import Models.Cards.Customer;
import Models.Documents.Document;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sean98.iam.R;
import com.google.android.material.tabs.TabLayout;

import java.util.Arrays;
import java.util.List;


public class DocumentTabs extends Fragment {
    private static final String CUSTOMER_KEY = "CUSTOMER_KEY";


    private TabLayout tabLayout;

    private ViewPager documentsViewPager;
    private Customer customer;
    ViewPagerAdapter viewPagerAdapter;

    public DocumentTabs() {
        // Required empty public constructor
    }


    public static DocumentTabs newInstance(Customer customer) {
        DocumentTabs fragment = new DocumentTabs();
        Bundle args = new Bundle();
        args.putSerializable(CUSTOMER_KEY, customer);
        fragment.setArguments(args);
        return fragment;
    }
    List<Fragment> pages;
    List<String> titles;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customer = (Customer) getArguments().getSerializable(CUSTOMER_KEY);
            pages = Arrays.asList(
                    DocumentsListView.newInstance(customer, Document.Type.RevocationInvoice),
                    DocumentsListView.newInstance(customer, Document.Type.Invoice),
                    DocumentsListView.newInstance(customer, Document.Type.Order),
                    DocumentsListView.newInstance(customer, Document.Type.SaleQuotation)
            );

            titles = Arrays.asList(
                    Document.Type.RevocationInvoice.toString(),
                    Document.Type.Invoice.toString(),
                    Document.Type.Order.toString(),
                    Document.Type.SaleQuotation.toString()
            );
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return  inflater.inflate(R.layout.fragment_document_tabs, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.docsTabLayout);
        documentsViewPager = view.findViewById(R.id.documentsViewPager);
        tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        tabLayout.setupWithViewPager(documentsViewPager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.setPages(pages,titles);
        documentsViewPager.setOffscreenPageLimit(titles.size());
        documentsViewPager.setAdapter(viewPagerAdapter);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        List<Fragment> mpages;
        List<String> mtitles;
        public void setPages(List<Fragment> pages, List<String> titles){
           this.mpages = pages;
           this.mtitles = titles;
        }
        FragmentManager fragmentManager;
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            fragmentManager = fm;



        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragmentAtPosition =  this.mpages.get(position);
            return  fragmentAtPosition;
        }

        @Override
        public int getCount() {
            return this.mpages.size();
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return this.mtitles.get(position);
        }




    }
}
