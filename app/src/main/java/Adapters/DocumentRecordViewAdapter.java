package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sean98.iam.R;
import com.example.sean98.iam.Documents.DocumentCardView;

import java.util.Collections;
import java.util.List;

import Models.Documents.Document;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DocumentRecordViewAdapter extends RecyclerView.Adapter
        <DocumentRecordViewAdapter.DocumentRecordViewHolder>  {

    private List<Document> documentList;

    private OnDocumentClickedListener onDocumentClickedListener;
    public void setOnDocumentClickedListener(OnDocumentClickedListener listener){
        this.onDocumentClickedListener = listener;
    }

    public interface OnDocumentClickedListener{
        void onDocumentClicked(View view,Document document);
    }

    public DocumentRecordViewAdapter(){}
    public DocumentRecordViewAdapter(List<Document> documentList){
        updateDataSet(documentList);
    }


    public void updateDataSet(List<Document> documentList) {
        Collections.sort(documentList,(o1, o2)-> -o1.getDocDate().compareTo(o2.getDocDate()));
        this.documentList = documentList;
    }


    @NonNull
    @Override
    public DocumentRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_document_card_view, parent,false);
        return new DocumentRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentRecordViewAdapter.DocumentRecordViewHolder holder, int position) {
        Document doc = documentList.get(position);
        DocumentCardView.Bind(holder.getItemView(),doc);
        if(onDocumentClickedListener!=null){
            holder.getItemView().setOnClickListener((v ->
                    onDocumentClickedListener.onDocumentClicked(holder.getItemView(),doc)));

        }
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public static class DocumentRecordViewHolder extends RecyclerView.ViewHolder{
        public View getItemView() {
            return itemView;
        }

        View itemView;
        public DocumentRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

}
