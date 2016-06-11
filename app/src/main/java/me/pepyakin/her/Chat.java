package me.pepyakin.her;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public final class Chat {

    private static Chat chatInstance = new Chat();

    public static Chat getInstance() {
        return chatInstance;
    }

    final static class ChatItem {
        // true if inbound, otherwise outbound.
        final boolean inbound;

        @NonNull
        final String text;

        private ChatItem(boolean inbound, @NonNull String text) {
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

    private Subject<ChatItem, ChatItem> chatSubject = PublishSubject.create();

    public void send(@NonNull String message) {
        chatSubject.onNext(ChatItem.newOutbound(message));
    }

    public void receive(@NonNull String message) {
        chatSubject.onNext(ChatItem.newInbound(message));
    }

    public Observable<ChatItem> getChat() {
        return chatSubject.asObservable();
    }
}
