package me.pepyakin.her.model;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static me.pepyakin.her.util.Preconditions.assertMainThread;

public final class Chat {

    private static Chat chatInstance;

    private final ChatStorage chatStorage;

    private Chat(final ChatStorage chatStorage) {
        this.chatStorage = chatStorage;
    }

    public static Chat getInstance(Context context) {
        // Call from main thread only. To lift this requirement, one
        // should make sure of real thread safety.
        assertMainThread();

        if (chatInstance == null) {
            ChatStorage chatStorage = ChatStorage.createChatStorage(context
                    .getApplicationContext());
            chatInstance = new Chat(chatStorage);
        }
        return chatInstance;
    }

    public void send(@NonNull String message) {
        addChatItem(ChatItem.newOutbound(message));
    }

    public void receive(@NonNull String message) {
        addChatItem(ChatItem.newInbound(message));
    }

    private void addChatItem(ChatItem chatItem) {
        chatStorage.insertChatItem(chatItem);
    }

    public Observable<List<ChatItem>> getChat() {
        return chatStorage.queryChat()
                .observeOn(AndroidSchedulers.mainThread());
    }
}
