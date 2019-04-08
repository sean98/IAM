package com.example.sean98.iam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import Databases.CachedDao;
import Databases.Exceptions.DatabaseException;
import Databases.ICompanyVariablesDao;
import Models.Company.SystemVariables;
import Models.Employees.Employee;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import permmisionModels.PermissionMethods;

public class LoginFragment extends Fragment {
    public static final String SN_TAG = "SN_TAG";

    private TextInputEditText serialNumber;
    private SharedPreferences sharedPref;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        serialNumber = view.findViewById(R.id.serial_number);

        //Employee user = sharedPref.
        int sn = sharedPref.getInt(SN_TAG, -1);
        if (sn != -1)
            checkSN(sn, true);

        view.findViewById(R.id.login_btn).setOnClickListener(btn ->
                checkSN(Integer.valueOf(serialNumber.getText().toString()), true));

        view.findViewById(R.id.no_account_text_view).setOnClickListener(tv -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","lior.itzhak@gmail.com, sean98goldfarb@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.sign_up));
            emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sign_up_msg));
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });
        return view;
    }

    public void checkSN(int sn, boolean tryLocal) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.authenticating) + "...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        ICompanyVariablesDao db = new CachedDao(getContext());

        db.getEmployees().addOnSuccessListener(employees->{
            for (Employee e : employees) {
                if (sn == e.getSn()) {
                    sharedPref.edit()
                            .putInt(SN_TAG, sn)
                            .apply();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(MainActivity.EMPLOYEE_TAG, e);
                    intent.putExtra(MainActivity.BUNDLE_TAG, bundle);
                    SystemVariables.ActiveUser = e;
                    startActivity(intent);
                    getActivity().finish();
                    return;
                }
            }

            ((CachedDao) db).forceOnline().getEmployees().addOnSuccessListener(employees2-> {
                for (Employee e : employees2) {
                    if (sn == e.getSn()) {
                        sharedPref.edit()
                                .putInt(SN_TAG, sn)
                                .apply();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(MainActivity.EMPLOYEE_TAG, e);
                        intent.putExtra(MainActivity.BUNDLE_TAG, bundle);
                        SystemVariables.ActiveUser = e;
                        startActivity(intent);
                        getActivity().finish();
                        return;
                    }
                }
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), R.string.serial_number_incorrect,
                                Toast.LENGTH_SHORT).show());
            }).addOnFailureListener(exception -> {
                String msg;
                if (exception.getType() == DatabaseException.Type.TimeOut
                        && !PermissionMethods.isInternetAvailable())
                    msg = getString(R.string.no_internet_access);
                else
                    msg = getString(R.string.no_access_to_server);
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());
            }).execute();
        }).addOnFailureListener(e->{
            String msg;
            if (e.getType() == DatabaseException.Type.TimeOut
                    && !PermissionMethods.isInternetAvailable())
                msg = getString(R.string.no_internet_access);
            else
                msg = getString(R.string.no_access_to_server);
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());                e.printStackTrace();

        }).addOnPostExecuteListener(r-> {
            if( getActivity()!=null)
                    getActivity().runOnUiThread(() -> progressDialog.dismiss());
                }).execute();

    }


}
