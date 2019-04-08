package com.example.sean98.iam;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LocaleApplication extends Application {
    public static final String LANG_KEY = "lang_tag";

    public static Context applicationContext;
    public static Locale applicationLocale;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        loadLocale(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadLocale(this);
    }

    public void loadLocale(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lang = sharedPref.getString("lang_tag", null);
        if (lang!=null) {
            Configuration config = context.getResources().getConfiguration();
            applicationLocale = new Locale(lang);
            Locale.setDefault(applicationLocale);
            config.setLocale(applicationLocale);
            config.setLayoutDirection(applicationLocale);
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
    }

    public void switchLocale(Context context, String lang) {
        SharedPreferences sharedPref;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPref.edit().putString(LANG_KEY, lang).commit();
        loadLocale(this);
    }
}
