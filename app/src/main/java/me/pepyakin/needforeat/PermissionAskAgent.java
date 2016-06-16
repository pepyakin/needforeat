package me.pepyakin.needforeat;

import android.support.annotation.Nullable;

public interface PermissionAskAgent {
    void askPermissions(String[] permissions);
    boolean isGranted(String permission);
    void setListener(@Nullable Listener listener);

    interface Listener {
        void onPermissionResponse(PermissionResponse[] permissionResponses);
    }

    final class PermissionResponse {
        public final String name;
        public final boolean granted;

        PermissionResponse(String name, boolean granted) {
            this.name = name;
            this.granted = granted;
        }
    }
}
