package me.pepyakin.her.bot;

import android.content.res.Resources;

import me.pepyakin.her.R;

final class Speech {
    private final String[] vocabulary;
    private final Rng rng;

    Speech(String[] vocabulary, Rng rng) {
        this.vocabulary = vocabulary;
        this.rng = rng;
    }

    static Speech create(Resources resources) {
        String[] vocabulary = resources.getStringArray(R.array.vocabulary);
        return new Speech(vocabulary, new RealRng());
    }

    public String pickNextWord() {
        // Use well-known "headless hen" algorithm to choose what to say.
        int index = rng.nextInt(vocabulary.length);
        return vocabulary[index];
    }
}
