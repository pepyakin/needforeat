package me.pepyakin.needforeat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import me.pepyakin.needforeat.model.GeoPoint;

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
