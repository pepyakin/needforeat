package me.pepyakin.her;

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

    private Subject<ChatItem, ChatItem> chatSubject = PublishSubject.create();

    public void send(String message) {
        chatSubject.onNext(ChatItem.newOutbound(message));
    }

    public Observable<ChatItem> getChat() {
        return chatSubject.asObservable();
    }
}
