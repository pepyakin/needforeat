package me.pepyakin.her;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;

import me.pepyakin.her.model.GeoPoint;
import me.pepyakin.her.util.AbsLocationListener;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.BooleanSubscription;

final class RxLocationManagerAdapter {

    private RxLocationManagerAdapter() {
    }

    @NonNull
    public static Observable<GeoPoint> singleMostAccurateLocation(
            @NonNull final Context context) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return Observable.create(new LocationOnSubscribe(locationManager))
                .map(new Func1<Location, GeoPoint>() {
                    @Override
                    public GeoPoint call(Location location) {
                        return GeoPoint.fromLocation(location);
                    }
                });
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
                subscriber.onError(e);
            }
        }
    }
}
