package me.pepyakin.her;

import rx.Observable;

public final class Chat {

    final static class ChatItem {
        // true if inbound, otherwise outbound.
        final boolean inbound;
        final String text;

        private ChatItem(boolean inbound, String text) {
            this.inbound = inbound;
            this.text = text;
        }

        public static ChatItem newInbound(String text) {
            return new ChatItem(true, text);
        }

        public static ChatItem newOutbound(String text) {
            return new ChatItem(false, text);
        }
    }

    public void send(String message) {
    }

    public Observable<ChatItem> getChat() {
        return Observable.empty();
    }
}
