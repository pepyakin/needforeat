package me.pepyakin.needforeat.model;

import android.support.annotation.NonNull;

public final class ChatItem {
    // true if inbound, otherwise outbound.
    public final boolean inbound;

    @NonNull
    public final String text;

    public final long timestamp;

    ChatItem(boolean inbound, @NonNull String text, long timestamp) {
        this.inbound = inbound;
        this.text = text;
        this.timestamp = timestamp;
    }

    public static ChatItem newInbound(@NonNull String text) {
        return new ChatItem(true, text, System.currentTimeMillis());
    }

    public static ChatItem newOutbound(@NonNull String text) {
        return new ChatItem(false, text, System.currentTimeMillis());
    }
}
