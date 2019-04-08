package com.example.sean98.iam.Dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.sean98.iam.LocaleActivity;
import com.example.sean98.iam.LocaleApplication;
import com.example.sean98.iam.LoginActivity;
import com.example.sean98.iam.R;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SortDialog extends DialogFragment {

    public static final String SORT_OPTION_TAG = "SORT_OPTION_TAG";

    private OnSortOptionChoseListener onSortOptionChoseListener = null;

    public interface OnSortOptionChoseListener {
        void onSortOptionChose(SortOptions sortOptions);
    }

    public void setOnSortOptionChoseListener(OnSortOptionChoseListener onSortOptionChoseListener) {
        this.onSortOptionChoseListener = onSortOptionChoseListener;
    }

    public enum SortOptions {
        Alphabetical(LocaleApplication.applicationContext.getString(R.string.alphabetical)),
        Distance(LocaleApplication.applicationContext.getString(R.string.distance)),
        City(LocaleApplication.applicationContext.getString(R.string.city)),
        Balance(LocaleApplication.applicationContext.getString(R.string.balance));

        private String text;

        SortOptions(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private SharedPreferences sharedPref;

    public SortDialog() {
        //Empty constructor
    }

    public static SortDialog newInstance() {
        //
        return new SortDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable());
        View view = inflater.inflate(R.layout.dialog_sorting_option, container);
        RadioGroup radioGroup = view.findViewById(R.id.radio_group);

        view.findViewById(R.id.apply_button).setOnClickListener(btn -> {
            int id = radioGroup.getCheckedRadioButtonId();
            SortOptions tag = (SortOptions)view.findViewById(id).getTag();

            SharedPreferences.Editor prefsEditor = sharedPref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(tag);
            prefsEditor.putString(SORT_OPTION_TAG, json);
            prefsEditor.commit();

            if (onSortOptionChoseListener!=null)
                onSortOptionChoseListener.onSortOptionChose(tag);

            this.dismiss();
        });

        view.findViewById(R.id.cancel_button).setOnClickListener(btn -> this.dismiss());

        createElement(radioGroup, SortOptions.Alphabetical,
                SortOptions.Distance,
                SortOptions.City,
                SortOptions.Balance);

        return view;
    }

    private void createElement(RadioGroup container, SortOptions... elements) {
        Gson gson = new Gson();
        int id = 0;
        RadioButton checkedrb = null;
        String json = sharedPref.getString(SORT_OPTION_TAG, gson.toJson(SortOptions.Alphabetical));
        SortOptions tag = gson.fromJson(json, SortOptions.class);
        for (SortOptions e : elements) {
            RadioButton rb = new RadioButton(getContext());
            rb.setText(e.toString().trim());
            if (e==tag)
                checkedrb = rb;
            rb.setTag(e);
            container.addView(rb);
        }
        checkedrb.setChecked(true);

    }
}
