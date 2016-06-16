package me.pepyakin.needforeat;

import android.support.annotation.Nullable;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class PermissionRequesterTest {

    private static final String PERMISSION = "some_permission";

    @Test
    public void askPermission() {
        final String[] permissions = {PERMISSION};

        MockPermissionAskAgent mockPermissionAskAgent =
                new MockPermissionAskAgent();
        PermissionRequester permissionRequester =
                new PermissionRequester(mockPermissionAskAgent);

        Observable<Boolean> ensurePermissions =
                permissionRequester.ensurePermissions(permissions);
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        ensurePermissions.subscribe(testSubscriber);

        mockPermissionAskAgent.grant(permissions);

        testSubscriber.assertValue(true);
    }

    final class MockPermissionAskAgent implements PermissionAskAgent {

        private boolean isGranted;

        @Nullable
        private Listener listener;

        @Override
        public void askPermissions(String[] permissions) {
        }

        @Override
        public boolean isGranted(String permission) {
            return isGranted;
        }

        @Override
        public void setListener(@Nullable Listener listener) {
            this.listener = listener;
        }

        void grant(String[] permissions) {
            assert this.listener != null;

            PermissionResponse[] permissionResponses = new
                    PermissionResponse[permissions.length];
            for (int i = 0; i < permissions.length; i++) {
                permissionResponses[i] = new PermissionResponse(
                        permissions[i], true);
            }
            this.listener.onPermissionResponse(permissionResponses);
        }

        void revoke(String[] permissions) {
            assert this.listener != null;

            PermissionResponse[] permissionResponses = new
                    PermissionResponse[permissions.length];
            for (int i = 0; i < permissions.length; i++) {
                permissionResponses[i] = new PermissionResponse(
                        permissions[i], false);
            }
            this.listener.onPermissionResponse(permissionResponses);
        }
    }
}
