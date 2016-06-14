package me.pepyakin.her.util;

import android.os.Looper;

public final class Preconditions {
    private Preconditions() {
    }

    public static void assertMainThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            // Call from main thread only. To lift this requirement, one
            // should make sure of real thread safety.
            throw new AssertionError();
        }
    }
}
