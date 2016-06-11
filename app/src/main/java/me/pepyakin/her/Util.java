package me.pepyakin.her;

import android.os.Looper;

public final class Util {
    private Util() {
    }

    public static void assertMainThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            // Call from main thread only. To lift this requirement, one
            // should make sure of real thread safety.
            throw new AssertionError();
        }
    }
}
