package me.pepyakin.her;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

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

    @VisibleForTesting
    @NonNull
    static Observable<GeoPoint> singleMostAccurateLocation(
            LocationProvider locationProvider) {
        Observable<GeoPoint> onlineLocation =
                Observable.create(new LocationOnSubscribe(locationProvider))
                        .publish()
                        .refCount();

        Observable<GeoPoint> prependLastKnownLocation =
                lastKnownLocation(locationProvider).concatWith(onlineLocation);

        return onlineLocation.timeout(new Func0<Observable<Object>>() {
            @Override
            public Observable<Object> call() {
                return Observable.timer(1, TimeUnit.SECONDS).cast(Object.class);
            }
        }, new Func1<GeoPoint, Observable<Object>>() {
            @Override
            public Observable<Object> call(GeoPoint location) {
                return Observable.never();
            }
        }, prependLastKnownLocation);
    }

    @NonNull
    private static Observable<GeoPoint> lastKnownLocation(
            final LocationProvider locationProvider) {
        return Observable.fromCallable(new Callable<GeoPoint>() {
            @Override
            public GeoPoint call() throws SecurityException {
                return locationProvider.getLastKnownLocation();
            }
        });
    }

    private static class LocationOnSubscribe
            implements Observable.OnSubscribe<GeoPoint> {
        @NonNull
        final LocationProvider locationProvider;

        public LocationOnSubscribe(@NonNull LocationProvider locationProvider) {
            this.locationProvider = locationProvider;
        }

        @Override
        public void call(final Subscriber<? super GeoPoint> subscriber) {
            LocationProvider.LocationReceived listener =
                    new LocationProvider.LocationReceived() {
                        @Override
                        public void onLocationReceived(@NonNull GeoPoint location) {
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
