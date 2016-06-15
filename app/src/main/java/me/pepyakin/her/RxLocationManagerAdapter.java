package me.pepyakin.her;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import me.pepyakin.her.model.GeoPoint;
import me.pepyakin.her.util.AbsLocationListener;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subscriptions.BooleanSubscription;

final class RxLocationManagerAdapter {

    private RxLocationManagerAdapter() {
    }

    @NonNull
    public static Observable<GeoPoint> singleMostAccurateLocation(
            @NonNull final Context context) {
        final LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return singleMostAccurateLocation(locationManager);
    }

    @NonNull
    private static Observable<GeoPoint> singleMostAccurateLocation(LocationManager locationManager) {
        Observable<Location> onlineLocation =
                Observable.create(new LocationOnSubscribe(locationManager))
                        .publish()
                        .refCount();

        Observable<Location> prependLastKnownLocation =
                lastKnownLocation(locationManager).concatWith(onlineLocation);

        Observable<Location> lastKnownLocationDeadline =
                onlineLocation.timeout(new Func0<Observable<Object>>() {
                    @Override
                    public Observable<Object> call() {
                        return Observable.timer(1, TimeUnit.SECONDS).cast(Object.class);
                    }
                }, new Func1<Location, Observable<Object>>() {
                    @Override
                    public Observable<Object> call(Location location) {
                        return Observable.never();
                    }
                }, prependLastKnownLocation);

        return lastKnownLocationDeadline.map(new Func1<Location, GeoPoint>() {
            @Override
            public GeoPoint call(Location location) {
                return GeoPoint.fromLocation(location);
            }
        });
    }

    @NonNull
    private static Observable<Location> lastKnownLocation(final LocationManager locationManager) {
        return Observable.fromCallable(new Callable<Location>() {
            @Override
            public Location call() throws Exception {
                String bestProvider = locationManager.getBestProvider(
                        getCriteria(),
                        /* enabledOnly */ true);

                // Thrown exception will be bubbled up to onError.
                //noinspection MissingPermission
                return locationManager.getLastKnownLocation(bestProvider);
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
            Criteria criteria = getCriteria();

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

    private static Criteria getCriteria() {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        return criteria;
    }
}
