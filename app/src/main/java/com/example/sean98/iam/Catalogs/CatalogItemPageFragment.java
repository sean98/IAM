package com.example.sean98.iam.Catalogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sean98.iam.R;

import java.text.DecimalFormat;

import Models.Company.Item;
import Models.Company.SystemVariables;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class CatalogItemPageFragment extends Fragment {

    public static final String ITEM_KEY = "ITEM_KEY";

    private ImageView image;
    private TextView code;
    private TextView price;
    private TextView description;
    private TextView details;
    private TextView comments;
    private ViewGroup viewGroup;

    private Item item;

    public static CatalogItemPageFragment newInstance(Item item) {
        Bundle args = new Bundle();
        args.putSerializable(ITEM_KEY, item);
        CatalogItemPageFragment fragment = new CatalogItemPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null)
            item = (Item) getArguments().getSerializable(ITEM_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog_item_page, container, false);
        image = view.findViewById(R.id.item_image);
        code = view.findViewById(R.id.item_code);
        price = view.findViewById(R.id.item_price);
        description = view.findViewById(R.id.item_description);
        details = view.findViewById(R.id.item_details);
        comments = view.findViewById(R.id.item_comments);
        image.setOnTouchListener((view1, motionEvent) -> true);
        showItem();
        return view;
    }

    private void showItem() {
        if (item==null)
            return;
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        String codeStr = getString(R.string.code) + ": "  + item.getCode();
        String priceStr = getString(R.string.price) + ": " + formatter.format(item.getDefaultPrice()) + " "
                + (item.getDefaultCurrency()!=null?item.getDefaultCurrency(): SystemVariables.GetSystemCurrency());
        String descriptionSir = getString(R.string.description) + ": " + item.getDescription();
        String detailsStr = getString(R.string.details) + ": " + (item.getDetails()!=null?item.getDetails():"");
        String commentsStr = getString(R.string.comments) + ": " + (item.getComments()!=null?item.getComments():"");

        code.setText(codeStr);
        price.setText(priceStr);
        description.setText(descriptionSir);
        details.setText(detailsStr);
        comments.setText(commentsStr);

        CircularProgressDrawable cpb = new CircularProgressDrawable(getContext());
        cpb.setStrokeWidth(5);
        cpb.setCenterRadius(30);
        cpb.start();

        Glide.with(getContext())
                .load(item.getPicPath())
                .centerCrop()
                .placeholder(cpb)
                .error(android.R.drawable.stat_notify_error)
                .into(image);
    }
}
