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

    public static GeoPoint fromLatLong(double lat, double lon) {
        return new GeoPoint(lat, lon);
    }

    public static GeoPoint fromLocation(@NonNull Location location) {
        return new GeoPoint(
                location.getLatitude(),
                location.getLongitude());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoPoint geoPoint = (GeoPoint) o;

        if (Double.compare(geoPoint.lat, lat) != 0) return false;
        return Double.compare(geoPoint.lon, lon) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "GeoPoint{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
