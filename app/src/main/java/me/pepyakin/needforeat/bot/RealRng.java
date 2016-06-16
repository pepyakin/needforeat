package me.pepyakin.needforeat.bot;

import java.util.Random;

final class RealRng implements Rng {
    private final Random random = new Random();

    @Override
    public int nextInt(int n) {
        return random.nextInt(n);
    }
}
