package me.pepyakin.her;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

public class NotificationController {

    private static final String NOTIFICATION_ABOUT_TO_SHOW = BuildConfig
            .APPLICATION_ID + ".action.notification_about_to_show";

    private static final int NEW_MESSAGE_NOTIF_ID = 1;

    private final static NotificationSwallower swallower =
            new NotificationSwallower();

    public static void chatActivityStarted(Activity activity) {
        IntentFilter filter = new IntentFilter(NOTIFICATION_ABOUT_TO_SHOW);
        activity.registerReceiver(swallower, filter);

        getNotificationManager(activity).cancel(NEW_MESSAGE_NOTIF_ID);
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
                    displayNotification(context, message);
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

    private static void displayNotification(Context context, String message) {
        Intent intent = MainActivity.buildIntent(context);
        PendingIntent mainActivityPi = PendingIntent.getActivity(context, 1, intent, 0);

        // TODO: i18n
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setOnlyAlertOnce(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(mainActivityPi)
                .setContentTitle("New message!")
                .setTicker(message)
                .setContentText(message);

        builder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
        builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);

        // Because of real-time chat.
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager nm = getNotificationManager(context);
        nm.notify(NEW_MESSAGE_NOTIF_ID, builder.build());
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
    }

    static final class NotificationSwallower extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setResultCode(Activity.RESULT_CANCELED);
        }
    }
}
