package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sean98.iam.R;

import java.util.ArrayList;
import java.util.List;

import Models.Company.Item;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class CatalogItemAdapter extends RecyclerView.Adapter<CatalogItemAdapter.CatalogItemVH> {

    List<Item> catalogItems;

    private OnItemChosenListener onItemChosenListener;

    public interface OnItemChosenListener {
        void onItemChosen(Item item);
    }

    public void setOnItemChosenListener(OnItemChosenListener onItemChosenListener) {
        this.onItemChosenListener = onItemChosenListener;
    }

    public CatalogItemAdapter() {
        catalogItems = new ArrayList<>();
    }

    public void updateDataSet(List<Item> catalogItems) {
        this.catalogItems = catalogItems;
    }

    @NonNull
    @Override
    public CatalogItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_catalog_item, parent, false);
        return new CatalogItemVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogItemVH holder, int position) {
        holder.bindWith(catalogItems.get(position));
        holder.itemView.setOnClickListener(view -> {
            if (onItemChosenListener!=null)
                onItemChosenListener.onItemChosen(catalogItems.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return catalogItems.size();
    }

    static class CatalogItemVH extends RecyclerView.ViewHolder {

        private View itemView;
        private ImageView image;
        private TextView tile;
        private CatalogItemVH(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            image = itemView.findViewById(R.id.item_image);
            tile = itemView.findViewById(R.id.item_title);
        }

        private void bindWith(Item catalogItem) {
            CircularProgressDrawable cpb = new CircularProgressDrawable(itemView.getContext());
            cpb.setStrokeWidth(5);
            cpb.setCenterRadius(30);
            cpb.start();
            tile.setText(catalogItem.getDescription());
            Glide.with(itemView.getContext())
                    .load(catalogItem.getPicPath())
                    .centerCrop()
                    .placeholder(cpb)
                    .error(android.R.drawable.stat_notify_error)
                    .into(image);
        }
    }
}
