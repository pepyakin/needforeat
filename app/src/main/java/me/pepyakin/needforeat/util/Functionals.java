package me.pepyakin.needforeat.util;

import rx.functions.Func1;

public final class Functionals {
    private Functionals() { }

    public static <T> Func1<T, T> id() {
        return new Func1<T, T>() {
            @Override
            public T call(T t) {
                return t;
            }
        };
    }
}
