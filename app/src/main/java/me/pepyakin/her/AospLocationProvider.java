package me.pepyakin.her;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import me.pepyakin.her.util.AbsLocationListener;

final class AospLocationProvider implements LocationProvider {

    private final LocationManager locationManager;

    AospLocationProvider(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    static LocationProvider create(Context context) {
        final LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return new AospLocationProvider(locationManager);
    }

    @Override
    public LocationListener requestSingleLocation(
            @NonNull final LocationReceived locationReceived) {
        final AbsLocationListener listener = new AbsLocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationReceived.onLocationReceived(location);
            }
        };

        // Allowed to throw SecurityException
        //noinspection MissingPermission
        locationManager.requestSingleUpdate(getCriteria(), listener, null);

        return new LocationListener() {
            @Override
            public void cancel() {
                try {
                    locationManager.removeUpdates(listener);
                } catch (SecurityException e) {
                    // Can't do much about this. Just swallow it.
                }
            }
        };
    }

    @Nullable
    @Override
    public Location getLastKnownLocation() {
        String bestProvider = locationManager.getBestProvider(
                getCriteria(),
                        /* enabledOnly */ true);

        // Thrown exception will be bubbled up to onError.
        //noinspection MissingPermission
        return locationManager.getLastKnownLocation(bestProvider);
    }

    private static Criteria getCriteria() {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        return criteria;
    }
}
