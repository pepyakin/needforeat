package me.pepyakin.her;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import me.pepyakin.her.model.GeoPoint;
import rx.observers.TestSubscriber;

public class RxLocationProviderAdapterTest {

    private static final GeoPoint FIRST_POINT = GeoPoint.fromLatLong(1.0, 1.0);
    private static final GeoPoint SECOND_POINT = GeoPoint.fromLatLong(2.0, 2.0);

    private MockLocationProvider mockLocationProvider;

    @Before
    public void setUp() throws Exception {
        mockLocationProvider = new MockLocationProvider();
    }

    @Test
    public void simple() throws Exception {
        TestSubscriber<GeoPoint> testSubscriber = new TestSubscriber<>();
        RxLocationProviderAdapter
                .singleMostAccurateLocation(mockLocationProvider)
                .subscribe(testSubscriber);

        mockLocationProvider.notifyLocation(FIRST_POINT);

        testSubscriber.assertValue(FIRST_POINT);
    }

    @Test
    public void awaitLastKnownLocation() throws Exception {
        mockLocationProvider.notifyLocation(FIRST_POINT);

        TestSubscriber<GeoPoint> testSubscriber = new TestSubscriber<>();
        RxLocationProviderAdapter
                .singleMostAccurateLocation(mockLocationProvider)
                .take(1)
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(3, TimeUnit.SECONDS);
        testSubscriber.assertValue(FIRST_POINT);
    }

    @Test
    public void doesNotEmitLastKnownLocationIfOnlineAvailable() throws Exception {
        mockLocationProvider.notifyLocation(FIRST_POINT);

        TestSubscriber<GeoPoint> testSubscriber = new TestSubscriber<>();
        RxLocationProviderAdapter
                .singleMostAccurateLocation(mockLocationProvider)
                .timeout(5, TimeUnit.SECONDS)
                .subscribe(testSubscriber);

        mockLocationProvider.notifyLocation(SECOND_POINT);

        testSubscriber.awaitTerminalEvent(5, TimeUnit.SECONDS);
        testSubscriber.assertValues(SECOND_POINT);
    }

    private static class MockLocationProvider implements LocationProvider {

        private GeoPoint lastKnownLocation;
        private LocationReceived locationReceived;

        void notifyLocation(@NonNull GeoPoint location) {
            if (locationReceived != null) {
                locationReceived.onLocationReceived(location);
            }
            lastKnownLocation = location;
        }

        @NonNull
        @Override
        public LocationListener requestSingleLocation(
                @NonNull LocationReceived locationReceived) {
            this.locationReceived = locationReceived;
            return new LocationListener() {
                @Override
                public void cancel() {
                    MockLocationProvider.this.locationReceived = null;
                }
            };
        }

        @Nullable
        @Override
        public GeoPoint getLastKnownLocation() {
            return lastKnownLocation;
        }
    }
}
