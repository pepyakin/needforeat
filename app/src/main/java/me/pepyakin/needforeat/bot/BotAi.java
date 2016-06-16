package me.pepyakin.needforeat.bot;

import android.content.Context;
import android.support.annotation.NonNull;

import rx.Observable;
import rx.functions.Func1;

final class BotAi {

    private final ImpulseProvider impulseProvider;
    private final Speech speech;

    private BotAi(ImpulseProvider impulseProvider, Speech speech) {
        this.impulseProvider = impulseProvider;
        this.speech = speech;
    }

    @NonNull
    static BotAi create(Context context) {
        Speech speech = Speech.create(context.getResources());
        return new BotAi(ImpulseProvider.create(), speech);
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
