package me.pepyakin.her;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public final class Chat {

    private static Chat chatInstance = new Chat();
    private Subject<List<ChatItem>, List<ChatItem>> chatSubject = PublishSubject.create();
    private ArrayList<ChatItem> chatStorage = new ArrayList<>();

    public static Chat getInstance() {
        return chatInstance;
    }

    public void send(@NonNull String message) {
        addChatItem(ChatItem.newOutbound(message));
    }

    private void addChatItem(ChatItem chatItem) {
        chatStorage.add(chatItem);
        chatSubject.onNext(chatStorage);
    }

    public void receive(@NonNull String message) {
        addChatItem(ChatItem.newInbound(message));
    }

    public Observable<List<ChatItem>> getChat() {
        return chatSubject.asObservable().map(new Func1<List<ChatItem>, List<ChatItem>>() {
            @Override
            public List<ChatItem> call(List<ChatItem> chat) {
                return Collections.unmodifiableList(chat);
            }
        });
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
}
