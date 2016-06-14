package me.pepyakin.her.bot;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.functions.Func1;

final class BotAi {

    // TODO: i18n
    private static final String[] vocabulary = new String[]{
            "Feed Me!", "Please, feed me!", "I want to eat!", "eat!",
            "feed me please!", "meal time!"
    };
    private final ImpulseProvider impulseProvider;
    private final Rng rng;

    BotAi(ImpulseProvider impulseProvider, Rng rng) {
        this.impulseProvider = impulseProvider;
        this.rng = rng;
    }

    @NonNull
    static BotAi create() {
        return new BotAi(ImpulseProvider.create(), new RealRng());
    }

    @NonNull
    Observable<String> botWantToSay() {
        return impulseProvider.impulse()
                .map(new Func1<Void, String>() {
                    @Override
                    public String call(Void o) {
                        return chooseWhatToSay();
                    }
                });
    }

    private String chooseWhatToSay() {
        // Use well-known "headless hen" algorithm to choose what to say.
        int index = rng.nextInt(vocabulary.length);
        return vocabulary[index];
    }
}
