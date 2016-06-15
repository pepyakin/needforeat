package me.pepyakin.her.model;

import android.location.Location;
import android.support.annotation.NonNull;

public class GeoPoint {
    public final double lat;
    public final double lon;

    GeoPoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public static GeoPoint fromLocation(@NonNull Location location) {
        return new GeoPoint(
                location.getLatitude(),
                location.getLongitude());
    }

    @Override
    public String toString() {
        return "GeoPoint{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
