package me.pepyakin.her;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import me.pepyakin.her.bot.BotService;
import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private Chat chat;
    private final NotificationSwallower notificationSwallower = new NotificationSwallower();
    private final MyAdapter adapter = new MyAdapter();

    private Subscription locationSubscription;
    private Subscription chatSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText messageView = (EditText) findViewById(R.id.main_message);
        RecyclerView chatView = (RecyclerView) findViewById(R.id.main_chat);

        //noinspection ConstantConditions
        chatView.setLayoutManager(new LinearLayoutManager(this));
        chatView.setAdapter(adapter);

        //noinspection ConstantConditions
        messageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String message = v.getText().toString();
                    chat.send(message);
                    v.setText("");
                    return true;
                }
                return false;
            }
        });

        if (savedInstanceState == null) {
            locationSubscription = RxLocationManagerAdapter.singleMostAccurateLocation(this)
                    .subscribe(new Action1<Location>() {
                        @Override
                        public void call(Location location) {
                            // TODO: Get real coordinates
                            String coordinates = location.toString();
                            chat.send(coordinates);
                        }
                    });

            startService(new Intent(this, BotService.class));
        }

        chat = Chat.getInstance();
        chatSubscription = chat.getChat().subscribe(new Action1<List<Chat.ChatItem>>() {
            @Override
            public void call(List<Chat.ChatItem> chat) {
                adapter.setItems(chat);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(notificationSwallower,
                new IntentFilter(InboundMessageReceiver.NOTIFICATION_ABOUT_TO_SHOW));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(notificationSwallower);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO: We need to unsubscribe in onStop, for being good citizen and
        // not use location services in background.
        locationSubscription.unsubscribe();

        chatSubscription.unsubscribe();
    }

    static final class NotificationSwallower extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setResultCode(Activity.RESULT_CANCELED);
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        final static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView messageTextView;

            public ViewHolder(TextView messageTextView) {
                super(messageTextView);
                this.messageTextView = messageTextView;
            }
        }

        private List<Chat.ChatItem> items = Collections.emptyList();

        public void setItems(List<Chat.ChatItem> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView messageTextView = new TextView(parent.getContext());
            return new ViewHolder(messageTextView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Chat.ChatItem chatItem = items.get(position);

            int desiredGravity;
            if (chatItem.inbound) {
                desiredGravity = Gravity.START;
            } else {
                desiredGravity = Gravity.END;
            }
            holder.messageTextView.setText(chatItem.text);
            holder.messageTextView.setGravity(desiredGravity);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
