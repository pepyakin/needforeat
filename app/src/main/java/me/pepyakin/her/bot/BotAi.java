package me.pepyakin.her.bot;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

final class BotAi {

    private static final Random random = new Random();

    // TODO: i18n
    private static final String[] vocabulary = new String[] {
            "Feed Me!", "Please, feed me!", "I want to eat!", "eat!",
            "feed me please!", "meal time!"
    };

    Observable<String> botWantToSay() {
        return Observable.just(null)
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
                .map(new Func1<Object, String>() {
                    @Override
                    public String call(Object o) {
                        return chooseWhatToSay();
                    }
                });
    }

    private String chooseWhatToSay() {
        // Use well-known "headless hen" algorithm to choose what to say.
        int index = random.nextInt(vocabulary.length);
        return vocabulary[index];
    }
}
