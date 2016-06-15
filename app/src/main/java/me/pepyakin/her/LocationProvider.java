package me.pepyakin.her;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import me.pepyakin.her.model.GeoPoint;

interface LocationProvider {
    /**
     * @throws SecurityException
     */
    @NonNull
    LocationListener requestSingleLocation(@NonNull LocationReceived locationReceived);

    /**
     * @throws SecurityException
     */
    @Nullable
    GeoPoint getLastKnownLocation();

    interface LocationReceived {
        void onLocationReceived(@NonNull GeoPoint location);
    }

    interface LocationListener {
        void cancel();
    }
}
