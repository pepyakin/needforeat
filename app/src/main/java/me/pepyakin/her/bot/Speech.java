package me.pepyakin.her.bot;

final class Speech {

    // TODO: i18n
    static final String[] vocabulary = new String[]{
            "Feed Me!", "Please, feed me!", "I want to eat!", "eat!",
            "feed me please!", "meal time!"
    };

    private final Rng rng;

    Speech(Rng rng) {
        this.rng = rng;
    }

    static Speech create() {
        return new Speech(new RealRng());
    }

    public String pickNextWord() {
        // Use well-known "headless hen" algorithm to choose what to say.
        int index = rng.nextInt(vocabulary.length);
        return vocabulary[index];
    }
}
