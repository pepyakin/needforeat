package me.pepyakin.her;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

interface LocationProvider {
    /**
     * @throws SecurityException
     */
    LocationListener requestSingleLocation(@NonNull LocationReceived locationReceived);

    /**
     * @throws SecurityException
     */
    @Nullable
    Location getLastKnownLocation();

    interface LocationReceived {
        void onLocationReceived(@NonNull Location location);
    }

    interface LocationListener {
        void cancel();
    }
}
