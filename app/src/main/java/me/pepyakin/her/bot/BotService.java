package me.pepyakin.her.bot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public final class BotService extends Service {

    private static final Random random = new Random();

    private Subscription subscription;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Service is not bindable
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        subscription = Observable.just(null)
                .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Void> observable) {
                        return observable.flatMap(new Func1<Void, Observable<?>>() {
                            @Override
                            public Observable<?> call(Void signal) {
                                // [2000, 3000)
                                long timerDelayMs = 2000 + random.nextInt(1000);
                                return Observable.timer(timerDelayMs, TimeUnit.MILLISECONDS);
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        Log.e("BotService", "message!");
                    }
                });

        // We don't really care about redelivering intent, so use STICKY.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Actually, this service is not meant to be destroyed, so this is just in case.
        super.onDestroy();
        subscription.unsubscribe();
    }
}
