package com.example.sean98.iam;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import Databases.CachedDao;
import Databases.ICompanyVariablesDao;


public class LoginActivity extends LocaleActivity {
    public static final String BUNDLE_TAG = "BUNDLE_TAG";

    private SharedPreferences sharedPref;
    public static Context applicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( CachedDao.ApplicationContext == null)
            CachedDao.ApplicationContext = getApplicationContext();
        setContentView(R.layout.activity_login);



        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        applicationContext = getApplicationContext();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_fragment, LoginFragment.newInstance())
                .commit();
    }
}
