package me.pepyakin.her;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;

import me.pepyakin.her.model.GeoPoint;
import rx.observers.TestSubscriber;

import static org.junit.Assert.*;

public class RxLocationManagerAdapterTest {

    private static final GeoPoint FIRST_POINT = GeoPoint.fromLatLong(1.0, 1.0);

    private MockLocationProvider mockLocationProvider;

    @Before
    public void setUp() throws Exception {
        mockLocationProvider = new MockLocationProvider();
    }

    @Test
    public void simple() throws Exception {
        TestSubscriber<GeoPoint> testSubscriber = new TestSubscriber<>();
        RxLocationManagerAdapter.singleMostAccurateLocation
                (mockLocationProvider).subscribe(testSubscriber);

        mockLocationProvider.notifyLocation(FIRST_POINT);

        testSubscriber.assertValue(FIRST_POINT);
    }

    private static class MockLocationProvider implements LocationProvider {

        private GeoPoint lastKnownLocation;
        private LocationReceived locationReceived;

        void notifyLocation(@NonNull GeoPoint location) {
            locationReceived.onLocationReceived(location);
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
