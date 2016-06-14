package me.pepyakin.her.bot;

final class MockRng implements Rng {
    private int nextRandom;

    void setNextRandom(int nextRandom) {
        this.nextRandom = nextRandom;
    }

    @Override
    public int nextInt(int n) {
        return nextRandom;
    }
}
