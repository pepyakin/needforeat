package me.pepyakin.her.bot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import me.pepyakin.her.InboundMessageReceiver;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public final class BotService extends Service {

    private Subscription subscription;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BotAi botAi = new BotAi();
        subscription = botAi.botWantToSay()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String message) {
                        sendMessage(message);
                    }
                });

        // We don't really care about redelivering intent, so use STICKY.
        return START_STICKY;
    }

    private void sendMessage(@NonNull String message) {
        Intent intent = InboundMessageReceiver.sendMessageIntent(this, message);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        // Actually, this service is not meant to be destroyed, so this is just in case.
        super.onDestroy();
        subscription.unsubscribe();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Service is not bindable
        return null;
    }
}
