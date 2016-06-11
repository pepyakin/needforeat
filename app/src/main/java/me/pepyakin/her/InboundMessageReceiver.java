package me.pepyakin.her;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

public final class InboundMessageReceiver extends BroadcastReceiver {

    public static final String NOTIFICATION_ABOUT_TO_SHOW = BuildConfig.APPLICATION_ID +
            ".action.notification_about_to_show";

    public static Intent sendMessageIntent(Context context, @NonNull String message) {
        Intent intent = new Intent(context, InboundMessageReceiver.class);
        intent.putExtra("message", message);
        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        if (message != null) {
            Chat.getInstance().receive(message);
            displayNotificationIfNeeded(context, message);
        }
    }

    private void displayNotificationIfNeeded(Context context, final String message) {
        Intent intent = new Intent(NOTIFICATION_ABOUT_TO_SHOW);
        intent.setPackage(context.getPackageName());

        BroadcastReceiver resultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == Activity.RESULT_OK) {
                    // Pretend to be notification
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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
}
