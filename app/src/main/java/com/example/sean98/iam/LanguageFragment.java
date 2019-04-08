package com.example.sean98.iam;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import static com.example.sean98.iam.MainActivity.BUNDLE_TAG;

public class LanguageFragment extends Fragment {

    private static final List<String> locale = Arrays.asList("en", "he");
    private static final List<String> languages = Arrays.asList("English", "עיברית");
    private static final int margin = 50;

    public static LanguageFragment newInstance() {
        return new LanguageFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language, container, false);
        LinearLayout ll = view.findViewById(R.id.language_container);
        for (int i=0;i<languages.size();i++) {
            TextView tv = new TextView(getContext());
            tv.setText(languages.get(i));
            tv.setTag(locale.get(i));
            tv.setTextSize(30);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            tv.setOnClickListener(this::onLocaleChosen);
            ll.addView(tv);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(margin, margin, margin, margin);
            tv.setLayoutParams(lp);
            ImageView divider = new ImageView(getContext());
            divider.setBackgroundColor(Color.BLACK);
            ll.addView(divider);
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
            divider.setLayoutParams(lp);
        }
        return view;
    }

    public void onLocaleChosen(View v) {
        Application app = getActivity().getApplication();
        if (app instanceof LocaleApplication)
            ((LocaleApplication) app).switchLocale(getActivity(), (String)v.getTag());

        Configuration conf = getResources().getConfiguration();
        conf.locale = LocaleApplication.applicationLocale;
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Resources resources = new Resources(getActivity().getAssets(), metrics, conf);

        new AlertDialog.Builder(getContext())
                .setMessage(resources.getString(R.string.translate_msg))
                .setIcon(R.drawable.ic_location_on_indigo_900_24dp)
                .setPositiveButton(resources.getString(R.string.ok), (dialogInterface, i) -> {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra(BUNDLE_TAG, getActivity().getIntent().getBundleExtra(BUNDLE_TAG));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                })
                .create()
                .show();
    }
}
