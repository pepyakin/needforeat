package me.pepyakin.her;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import me.pepyakin.her.model.Chat;

public final class InboundMessageReceiver extends BroadcastReceiver {

    public static Intent sendMessageIntent(Context context, @NonNull String message) {
        Intent intent = new Intent(context, InboundMessageReceiver.class);
        intent.putExtra("message", message);
        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        if (message != null) {
            Chat.getInstance(context).receive(message);
            NotificationController.displayNotificationIfNeeded(context, message);
        }
    }
}
