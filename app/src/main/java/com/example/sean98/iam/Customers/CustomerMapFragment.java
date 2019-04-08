package com.example.sean98.iam.Customers;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sean98.iam.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.circularreveal.coordinatorlayout.CircularRevealCoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;


import Models.Cards.Customer;
import Models.util.SapLocation;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import permmisionModels.PermissionMethods;

public class CustomerMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SharedViewModel model;
    private CircularRevealCoordinatorLayout screen;
//    private CoordinatorLayout screen;
    public CustomerScreenManger.OnCustomerClickedListener onCustomerClickedListener;

    public CustomerMapFragment() {
        //Empty Constructor
    }

    public static CustomerMapFragment getInstance() {
        return new CustomerMapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if(getActivity()!=null) {
            SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map));
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        screen = view.findViewById(R.id.screen1);
        Snackbar snackbar = Snackbar.make(view.findViewById(R.id.screen1),
                R.string.downloading_data,
                Snackbar.LENGTH_INDEFINITE);
        model.getLoadData().observe(this, value -> {
            if (value)
                snackbar.show();
            else
                snackbar.dismiss();
        });
        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        model.getCustomers().observe(this, customers -> {
            mMap.clear();
            for (Customer c : customers) {
                if (c.getCid().equals("21752")) {
                    Log.i("MAP LOCATION", "onMapReady: " + c.getLocation());
                }
                if (c.getLocation()==null)
                    continue;
                SapLocation loc = c.getLocation();
                LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(c.getName())).setTag(c);
            }
        });
        model.getLocation().observe(this, l -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(
                    new LatLng(l.getLatitude(), l.getLongitude())));
        });
        mMap.setOnInfoWindowClickListener(marker -> {
            if (onCustomerClickedListener!=null)
                onCustomerClickedListener.onCustomerClicked(null, (Customer)marker.getTag());
        });

        PermissionMethods.updateLastLocation(getActivity(), getContext(),
                    R.string.location_request_msg1, screen, location -> {
                    LatLng latLng =new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    mMap.setMyLocationEnabled(true);
        });
    }
}
