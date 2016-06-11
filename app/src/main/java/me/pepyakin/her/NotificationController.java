package me.pepyakin.her;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.widget.Toast;

public class NotificationController {

    private static final String NOTIFICATION_ABOUT_TO_SHOW = BuildConfig
            .APPLICATION_ID + ".action.notification_about_to_show";

    private final static NotificationSwallower swallower =
            new NotificationSwallower();

    public static void chatActivityStarted(Activity activity) {
        IntentFilter filter = new IntentFilter(NOTIFICATION_ABOUT_TO_SHOW);
        activity.registerReceiver(swallower, filter);
    }

    public static void chatActivityStopped(Activity activity) {
        activity.unregisterReceiver(swallower);
    }

    public static void displayNotificationIfNeeded(
            @NonNull Context context,
            @NonNull final String message
    ) {
        Intent intent = new Intent(NOTIFICATION_ABOUT_TO_SHOW);
        intent.setPackage(context.getPackageName());

        BroadcastReceiver resultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == Activity.RESULT_OK) {
                    // Pretend to be notification
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            }
        };
        context.sendOrderedBroadcast(
                intent,
                null,
                resultReceiver,
                null,
                Activity.RESULT_OK,
                null,
                null);
    }

    static final class NotificationSwallower extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setResultCode(Activity.RESULT_CANCELED);
        }
    }
}
