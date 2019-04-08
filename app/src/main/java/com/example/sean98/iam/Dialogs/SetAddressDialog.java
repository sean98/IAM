package com.example.sean98.iam.Dialogs;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sean98.iam.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import Models.util.Address;
import androidx.fragment.app.DialogFragment;
import permmisionModels.PermissionMethods;


public class SetAddressDialog extends DialogFragment implements View.OnClickListener {

    final static String ADDRESS_KEY = "ADDRESS_KEY";

    private Address address;

    private TextInputLayout addressNameTextField;
    private TextInputLayout cityTextField;
    private TextInputLayout streetTextField;
    private TextInputLayout streetNoTextField;
    private TextInputLayout apartmentTextField;
    private TextInputLayout blockTextField;
    private TextInputLayout zipCodeTextField;
    private MaterialButton commitButton;
    private FloatingActionButton getLocationButton;


    private OnAddressSetListener onAddressSetListener;

    // Required empty public constructor
    public SetAddressDialog() { }


    public static SetAddressDialog newInstance(Address address) {
        SetAddressDialog fragment = new SetAddressDialog();
        Bundle args = new Bundle();
        args.putSerializable(ADDRESS_KEY,address);
        fragment.setArguments(args);
        return fragment;
    }
    public static SetAddressDialog newInstance() {
        SetAddressDialog fragment = new SetAddressDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args.containsKey(ADDRESS_KEY))
            address = (Address) args.getSerializable(ADDRESS_KEY);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_set_address_dialog, container,
                false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable());
        addressNameTextField = v.findViewById(R.id.addressNameTextField);
        cityTextField= v.findViewById(R.id.cityTextField);
        streetTextField= v.findViewById(R.id.streetTextField);
        streetNoTextField= v.findViewById(R.id.streetNoTextField);
        apartmentTextField= v.findViewById(R.id.apartmentTextField);
        blockTextField= v.findViewById(R.id.blockTextField);
        zipCodeTextField= v.findViewById(R.id.zipCodeTextField);
        commitButton = v.findViewById(R.id.commitButton);
        getLocationButton = v.findViewById(R.id.fixLocationButton);
        commitButton.setOnClickListener(this);
        if(address !=null && address.getName()!=null){
            addressNameTextField.getEditText().setText(address.getName());
            addressNameTextField.setEnabled(false);
            addressNameTextField.setHelperTextEnabled(false);
            cityTextField.getEditText().setText(address.getCity());
            streetTextField.getEditText().setText(address.getStreet());
            streetNoTextField.getEditText().setText(address.getStreetNum());
            apartmentTextField.getEditText().setText(address.getApartment());
            blockTextField.getEditText().setText(address.getBlock());
            zipCodeTextField.getEditText().setText(address.getZipCode());
        }

        getLocationButton.setOnClickListener((view)->
            PermissionMethods.updateLastLocation(getActivity(), getContext(),
                    R.string.location_request_msg2, null, loc -> {
                        Geocoder geocoder;
                        List<android.location.Address> addresses;
                        geocoder = new Geocoder(getContext(), Locale.getDefault());

                        try {
                            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            if (addresses.size()>0) {
                                cityTextField.getEditText().setText(addresses.get(0).getLocality());
                                zipCodeTextField.getEditText().setText(addresses.get(0).getPostalCode());
                                String[] tmpArr = addresses.get(0).getAddressLine(0).split(",");
                                if (tmpArr.length >= 3) {
                                    String tmp = tmpArr[0];
                                    tmpArr = tmp.split(" ");
                                    streetNoTextField.getEditText().setText(tmpArr[tmpArr.length - 1]);
                                    tmp = "";
                                    for (int i = 0; i < tmpArr.length - 1; i++)
                                        tmp += tmpArr[i] + " ";
                                    streetTextField.getEditText().setText(tmp.trim());
                                }
                            }
                        } catch (IOException e) {
                            getActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                                    R.string.cant_get_location, Toast.LENGTH_SHORT).show());
                        }
                    }));

        return v;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setOnAddressSetListener(OnAddressSetListener onAddressSetListener) {
        this.onAddressSetListener = onAddressSetListener;
    }

    @Override
    public void onClick(View v) {
        if (checkAddressFields()){
            if(onAddressSetListener!=null) {

                String country = address!=null?address.getCountry():"IL";
                //TODO update location of the address from MAP if available
                Address address = new Address(
                        addressNameTextField.getEditText().getText().toString(),
                        country,
                        cityTextField.getEditText().getText().toString(),
                        blockTextField.getEditText().getText().toString(),
                        streetTextField.getEditText().getText().toString(),
                        streetNoTextField.getEditText().getText().toString(),
                        apartmentTextField.getEditText().getText().toString(),
                        zipCodeTextField.getEditText().getText().toString()
                , null);
                onAddressSetListener.onAddressSet(address);
            }
            this.dismiss();
        }
    }

    private boolean checkAddressFields(){
        boolean isValid = true;
        String field = addressNameTextField.getEditText().getText().toString();
        if(field.trim().isEmpty()){
            addressNameTextField.setError(getContext().getString(R.string.error_address_name_empty));
            isValid = false;
        }
        field = cityTextField.getEditText().getText().toString();
        if(field.trim().isEmpty()){
            cityTextField.setError(getContext().getString(R.string.error_empty_field));
            isValid = false;
        }
        field = streetTextField.getEditText().getText().toString();
        if(field.trim().isEmpty()){
            streetTextField.setError(getContext().getString(R.string.error_empty_field));
            isValid = false;
        }
        field = streetNoTextField.getEditText().getText().toString();
        if(field.trim().isEmpty()){
            streetNoTextField.setError(getContext().getString(R.string.error_empty_field));
            isValid = false;
        }

        return isValid;
    }

    public interface OnAddressSetListener {
        void onAddressSet(Address address);
    }

}
