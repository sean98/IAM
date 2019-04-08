package permmisionModels;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import com.example.sean98.iam.R;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.net.InetAddress;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public abstract class PermissionMethods {

    @SuppressLint("MissingPermission")//TODO explain Suppress
    public static void updateLastLocation(Activity activity, Context context, int rational,
                                          CoordinatorLayout screen,
                                          OnSuccessListener<android.location.Location> listener) {
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            LocationServices.getFusedLocationProviderClient(activity)
                                    .getLastLocation()
                                    .addOnSuccessListener(activity, listener);
                        }

                        if (report.isAnyPermissionPermanentlyDenied())
                            activity.runOnUiThread(() -> {
                                    if (screen!=null) {
                                        Snackbar.make(screen, R.string.no_location, Snackbar.LENGTH_LONG)
                                                .setAction(R.string.activate, view -> {
                                                    Intent intent = new Intent(Settings
                                                            .ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", activity
                                                            .getPackageName(), null);
                                                    intent.setData(uri);
                                                    activity.startActivityForResult(intent, 101);
                                                })
                                                .setActionTextColor(Color.YELLOW)
                                                .show();
                                    }
                                    else {
                                        Toast.makeText(context, "app permission denied", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                            });
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        activity.runOnUiThread(() ->
                                new AlertDialog.Builder(context)
                                        .setTitle(R.string.location_request_title)
                                        .setMessage(rational)
                                        .setIcon(R.drawable.ic_location_on_indigo_900_24dp)
                                        .setPositiveButton(R.string.ok, (dialogInterface, i) ->
                                                token.continuePermissionRequest())
                                        .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                                            token.cancelPermissionRequest();
                                        })
                                        .create()
                                        .show());
                    }
                })
                .onSameThread()
                .check();
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}
