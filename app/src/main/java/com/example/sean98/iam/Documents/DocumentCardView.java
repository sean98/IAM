package com.example.sean98.iam.Documents;

import android.content.Context;
import android.os.Bundle;

import Models.Documents.Document;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sean98.iam.R;

import java.text.SimpleDateFormat;


public class DocumentCardView extends Fragment {

    public static void Bind(View DocumentCardView, Document document){
        View view = DocumentCardView;
        TextView dateTextView = view.findViewById(R.id.dateTextView);
        TextView typeTextView = view.findViewById(R.id.typeTextView);
        TextView carNameTextView = view.findViewById(R.id.carNameTextView);
        TextView docSnTextView =view.findViewById(R.id.docSnTextView);
        TextView docTotalTextView = view.findViewById(R.id.docTotalTextView);
        AppCompatImageView isCloseIcon = view.findViewById(R.id.isCloseIcon);
        AppCompatImageView isCanceledIcon = view.findViewById(R.id.isCanceledIcon);

        dateTextView.setText( new SimpleDateFormat("dd-MM-yy").
                format(document.getDocDate()));
        typeTextView.setText(document.getType().toString());
        carNameTextView.setText(document.getCardName());
        docSnTextView.setText(document.getSn().intValue()+"");
        docTotalTextView.setText(document.getTotal()+document.getCurrency());
        isCloseIcon.setVisibility(document.isClosed()?View.VISIBLE:View.INVISIBLE);
        isCanceledIcon.setVisibility(document.isCanceled()?View.VISIBLE:View.INVISIBLE);

       // view.setOnClickListener();
    }

    private Document document;
    private void setDocument(Document Document){
        this.document = document;
    }

   // TextView dateTextView;
   // TextView typeTextView;
   // TextView carNameTextView;
   // TextView docSnTextView;
   // TextView docTotalTextView;
   // AppCompatImageView isCloseIcon;
   // AppCompatImageView isCanceledIcon;

    public DocumentCardView() {
        // Required empty public constructor
    }

    public static DocumentCardView newInstance() {
        DocumentCardView fragment = new DocumentCardView();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_document_card_view, container, false);
       // dateTextView = v.findViewById(R.id.dateTextView);
       // typeTextView = v.findViewById(R.id.typeTextView);
       // carNameTextView = v.findViewById(R.id.carNameTextView);
       // docSnTextView = v.findViewById(R.id.docSnTextView);
       // docTotalTextView = v.findViewById(R.id.docTotalTextView);
       // isCloseIcon = v.findViewById(R.id.isCloseIcon);
       // isCanceledIcon = v.findViewById(R.id.isCanceledIcon);
    }



    public interface onDocClickListener {
        void onDocClicked(Document document);

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
