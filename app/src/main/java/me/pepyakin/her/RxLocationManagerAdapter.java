package me.pepyakin.her;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import me.pepyakin.her.model.GeoPoint;
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
        LocationProvider locationProvider = AospLocationProvider.create(context);
        return singleMostAccurateLocation(locationProvider);
    }

    @NonNull
    private static Observable<GeoPoint> singleMostAccurateLocation(
            LocationProvider locationProvider) {
        Observable<Location> onlineLocation =
                Observable.create(new LocationOnSubscribe(locationProvider))
                        .publish()
                        .refCount();

        Observable<Location> prependLastKnownLocation =
                lastKnownLocation(locationProvider).concatWith(onlineLocation);

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
    private static Observable<Location> lastKnownLocation(
            final LocationProvider locationProvider) {
        return Observable.fromCallable(new Callable<Location>() {
            @Override
            public Location call() throws SecurityException {
                return locationProvider.getLastKnownLocation();
            }
        });
    }

    private static class LocationOnSubscribe implements Observable.OnSubscribe<Location> {
        @NonNull
        final LocationProvider locationProvider;

        public LocationOnSubscribe(@NonNull LocationProvider locationProvider) {
            this.locationProvider = locationProvider;
        }

        @Override
        public void call(final Subscriber<? super Location> subscriber) {
            LocationProvider.LocationReceived listener =
                    new LocationProvider.LocationReceived() {
                        @Override
                        public void onLocationReceived(@NonNull Location location) {
                            subscriber.onNext(location);
                        }
                    };
            final LocationProvider.LocationListener locationListener
                    = locationProvider.requestSingleLocation(listener);

            subscriber.add(BooleanSubscription.create(new Action0() {
                @Override
                public void call() {
                    locationListener.cancel();
                }
            }));

            try {
                locationProvider.requestSingleLocation(listener);
            } catch (SecurityException e) {
                subscriber.onError(e);
            }
        }
    }
}
