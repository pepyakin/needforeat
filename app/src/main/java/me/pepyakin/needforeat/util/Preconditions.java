package me.pepyakin.needforeat.util;

import android.os.Looper;

public final class Preconditions {
    private Preconditions() {
    }

    public static void assertMainThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new AssertionError();
        }
    }

    public static void check(boolean condition) {
        if (!condition) {
            throw new AssertionError();
        }
    }
}
