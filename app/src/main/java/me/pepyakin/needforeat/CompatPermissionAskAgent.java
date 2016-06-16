package me.pepyakin.needforeat;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import me.pepyakin.needforeat.util.Preconditions;

final class CompatPermissionAskAgent implements PermissionAskAgent {

    private final Activity activity;
    private final int requestCode;

    @Nullable
    private Listener listener;

    CompatPermissionAskAgent(Activity activity, int requestCode) {
        this.activity = activity;
        this.requestCode = requestCode;
    }

    @Override
    public void askPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(
                activity, permissions, requestCode);
    }

    @Override
    public boolean isGranted(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    public void reportResponse(
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        Preconditions.check(permissions.length == grantResults.length);
        if (listener == null) return;

        PermissionResponse[] permissionResponses = new
                PermissionResponse[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            PermissionResponse response = new PermissionResponse(
                    permissions[i],
                    granted);
            permissionResponses[i] = response;
        }

        listener.onPermissionResponse(permissionResponses);
    }
}
