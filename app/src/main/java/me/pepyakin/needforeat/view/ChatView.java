package me.pepyakin.needforeat.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import me.pepyakin.needforeat.R;
import me.pepyakin.needforeat.model.ChatItem;

public final class ChatView extends FrameLayout {

    private final ChatItemsAdapter chatItemsAdapter = new ChatItemsAdapter();

    @Nullable
    private OnUserSentMessage onUserSentMessage;

    public ChatView(Context context) {
        super(context);
        View.inflate(context, R.layout.activity_main, this);

        EditText messageView = (EditText) findViewById(R.id.main_message);
        RecyclerView chatView = (RecyclerView) findViewById(R.id.main_chat);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);

        //noinspection ConstantConditions
        chatView.setLayoutManager(layoutManager);
        chatView.setAdapter(chatItemsAdapter);

        //noinspection ConstantConditions
        messageView.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(
                            TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEND) {
                            String message = v.getText().toString();
                            v.setText("");
                            dispatchUserSentMessage(message);
                            return true;
                        }
                        return false;
                    }
                });
    }

    private void dispatchUserSentMessage(String message) {
        if (onUserSentMessage != null) {
            onUserSentMessage.onUserSentMessage(message);
        }
    }

    public void setItems(List<ChatItem> chat) {
        chatItemsAdapter.setItems(chat);
    }

    public void setOnUserSentMessage(
            @Nullable OnUserSentMessage onUserSentMessage) {
        this.onUserSentMessage = onUserSentMessage;
    }

    public interface OnUserSentMessage {
        void onUserSentMessage(String message);
    }
}
