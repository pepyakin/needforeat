package me.pepyakin.needforeat.bot;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

final class ImpulseProvider {
    private final Scheduler scheduler;
    private final Rng rng;

    static ImpulseProvider create() {
        return new ImpulseProvider(
                Schedulers.computation(), new RealRng());
    }

    ImpulseProvider(Scheduler scheduler, Rng rng) {
        this.scheduler = scheduler;
        this.rng = rng;
    }

    @NonNull
    public Observable<Void> impulse() {
        return Observable.<Void>just(null)
                .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Void> observable) {
                        return observable.flatMap(new Func1<Void, Observable<?>>() {
                            @Override
                            public Observable<?> call(Void signal) {
                                // [2000, 3000)
                                long timerDelayMs = 2000 + rng.nextInt(1000);
                                return Observable.timer(
                                        timerDelayMs, TimeUnit.MILLISECONDS,
                                        scheduler);
                            }
                        });
                    }
                })
                .skip(1);
    }
}
