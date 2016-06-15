package me.pepyakin.her;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

        UnreadNotificationCounter.get(activity)
                .resetUnreadNotificationCounter();
        NotificationHandler.get(activity)
                .hideNotification();
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

    private static void displayNotification(
            Context context,
            @NonNull String message
    ) {
        int unreadNotificationCount = UnreadNotificationCounter.get(context)
                .getAndIncrementUnreadNotificationCount();
        NotificationHandler.get(context)
                .displayNotification(message, unreadNotificationCount);
    }

    static class NotificationHandler {
        private final NotificationManager nm;
        private final Context context;

        NotificationHandler(Context context, NotificationManager nm) {
            this.nm = nm;
            this.context = context;
        }

        public static NotificationHandler get(Context context) {
            return new NotificationHandler(
                    context, getNotificationManager(context));
        }

        public void displayNotification(String message, int totalCount) {
            Intent intent = MainActivity.buildIntent(context);
            PendingIntent mainActivityPi = PendingIntent.getActivity(context, 1, intent, 0);

            // TODO: Consider to use inbox style
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setOnlyAlertOnce(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(mainActivityPi)
                    .setContentTitle(context.getString(R.string.n_new_messages, totalCount))
                    .setTicker(message)
                    .setContentText(message);

            builder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
            builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);

            // Because of real-time chat.
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);

            nm.notify(NEW_MESSAGE_NOTIF_ID, builder.build());
        }

        void hideNotification() {
            nm.cancel(NEW_MESSAGE_NOTIF_ID);
        }

        private static NotificationManager getNotificationManager(Context context) {
            return (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
        }
    }

    private static final class UnreadNotificationCounter {

        private final SharedPreferences prefs;

        UnreadNotificationCounter(SharedPreferences prefs) {
            this.prefs = prefs;
        }

        static UnreadNotificationCounter get(Context context) {
            return new UnreadNotificationCounter(getNotificationPrefs(context));
        }

        private static SharedPreferences getNotificationPrefs(Context context) {
            return context.getSharedPreferences("notifications",
                    Context.MODE_PRIVATE);
        }

        public int getAndIncrementUnreadNotificationCount() {
            int unread = prefs.getInt("unread", 0);
            int newUnread = unread + 1;
            prefs.edit()
                    .putInt("unread", newUnread)
                    .apply();
            return newUnread;
        }

        public void resetUnreadNotificationCounter() {
            prefs.edit().clear().apply();
        }
    }

    static final class NotificationSwallower extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setResultCode(Activity.RESULT_CANCELED);
        }
    }
}
