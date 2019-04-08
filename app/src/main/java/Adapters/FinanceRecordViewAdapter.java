package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sean98.iam.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Models.Documents.FinanceRecord;
import Models.Company.SystemVariables;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FinanceRecordViewAdapter   extends RecyclerView.Adapter<FinanceRecordViewAdapter.FinanceRecordViewHolder> {
    private List<FinanceRecord> financeRecordList;
    private List<FinanceRecord> financeRecordListFiltered;
    public OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClicked(View view, FinanceRecord financeRecord);
    }


    public FinanceRecordViewAdapter(List<FinanceRecord> financeRecordList){
        updateDataSet(financeRecordList);
    }
    public FinanceRecordViewAdapter(){
        updateDataSet(new ArrayList<>());
    }


    public void updateDataSet(List<FinanceRecord> financeRecordList) {
        Collections.sort(financeRecordList,(o1,o2)-> -o1.getRefDate().compareTo(o2.getRefDate()));
        this.financeRecordList = financeRecordListFiltered = financeRecordList;
    }
    @NonNull
    @Override
    public FinanceRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_finance_record_view, parent,false);
        return new FinanceRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FinanceRecordViewAdapter.FinanceRecordViewHolder holder, int position) {

        FinanceRecord fr = financeRecordListFiltered.get(position);
        holder.bindWith(fr);
        if(onItemClickListener!=null){
            holder.getItemView().setOnClickListener((v ->
                    onItemClickListener.onItemClicked(holder.getItemView(),fr)));

        }

    }

    @Override
    public int getItemCount() {
        return financeRecordListFiltered.size();
    }



    public static class FinanceRecordViewHolder extends RecyclerView.ViewHolder{
        private View itemView;
        private TextView date_text;
        private TextView type_text;
        private TextView ref1_text;
        private TextView ref2_text;
        private TextView debt_text;
        private TextView balance_text;
        public FinanceRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            date_text = itemView.findViewById(R.id.date_text);
            type_text = itemView.findViewById(R.id.type_text);
            ref1_text = itemView.findViewById(R.id.ref1_text);
            ref2_text = itemView.findViewById(R.id.ref2_text);
            debt_text = itemView.findViewById(R.id.debt_text);
            balance_text = itemView.findViewById(R.id.balance_text);

        }

        public View getItemView() {
            return itemView;
        }

        public void bindWith(FinanceRecord financeRecord) {
            date_text.setText(new SimpleDateFormat("dd-MM-yy").
                    format(financeRecord.getRefDate()));
            type_text.setText(financeRecord.getType().toString());
            ref1_text.setText(financeRecord.getBaseDocSN());
            ref2_text.setText(financeRecord.getBase2DocSN());
            debt_text.setText(financeRecord.getDebt()+" "+ SystemVariables.GetSystemCurrency());
            balance_text.setText(financeRecord.getBalanceDue()+" "+SystemVariables.GetSystemCurrency());

        }
    }
}
