package me.pepyakin.her.model;

import android.support.annotation.NonNull;

public final class ChatItem {
    // true if inbound, otherwise outbound.
    public final boolean inbound;

    @NonNull
    public final String text;

    ChatItem(boolean inbound, @NonNull String text) {
        this.inbound = inbound;
        this.text = text;
    }

    public static ChatItem newInbound(@NonNull String text) {
        return new ChatItem(true, text);
    }

    public static ChatItem newOutbound(@NonNull String text) {
        return new ChatItem(false, text);
    }
}
