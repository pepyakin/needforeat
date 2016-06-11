package me.pepyakin.her;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.BooleanSubscription;

public final class RxLocationManagerAdapter {

    private RxLocationManagerAdapter() {
    }

    @NonNull
    public static Observable<Location> deviceLocation(@NonNull final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return Observable.create(new LocationOnSubscribe(locationManager));
    }

    private static class LocationOnSubscribe implements Observable.OnSubscribe<Location> {
        @NonNull
        final LocationManager locationManager;

        public LocationOnSubscribe(@NonNull LocationManager locationManager) {
            this.locationManager = locationManager;
        }

        @Override
        public void call(final Subscriber<? super Location> subscriber) {
            Criteria criteria = new Criteria();

            final AbsLocationListener listener = new AbsLocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    subscriber.onNext(location);
                }
            };

            subscriber.add(BooleanSubscription.create(new Action0() {
                @Override
                public void call() {
                    try {
                        locationManager.removeUpdates(listener);
                    } catch (SecurityException e) {
                        // Can't do much about this. Just swallow it.
                    }
                }
            }));

            try {
                locationManager.requestSingleUpdate(criteria, listener, null);
            } catch (SecurityException e) {
                // TODO: rethrow as onError
            }
        }
    }
}


