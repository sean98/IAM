package Models.util;

import android.location.Location;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class SapLocation implements Serializable {
    double latitude, longitude;

    public SapLocation() {
    }

    public SapLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    public SapLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double distanceTo(SapLocation location) {
        double pk = (float) (180.f/Math.PI);

        double a1 = this.latitude / pk;
        double a2 = this.longitude / pk;
        double b1 = location.latitude / pk;
        double b2 = location.longitude / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @NonNull
    @Override
    public String toString() {
        return "(" + latitude + ", " + longitude + ")";
    }
}
