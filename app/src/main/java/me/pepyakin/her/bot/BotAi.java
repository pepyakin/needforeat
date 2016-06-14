package me.pepyakin.her.bot;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.functions.Func1;

final class BotAi {

    private final ImpulseProvider impulseProvider;
    private final Speech speech;

    BotAi(ImpulseProvider impulseProvider, Speech speech) {
        this.impulseProvider = impulseProvider;
        this.speech = speech;
    }

    @NonNull
    static BotAi create() {
        return new BotAi(ImpulseProvider.create(), Speech.create());
    }

    @NonNull
    Observable<String> botWantToSay() {
        return impulseProvider.impulse()
                .map(new Func1<Void, String>() {
                    @Override
                    public String call(Void o) {
                        return speech.pickNextWord();
                    }
                });
    }
}
