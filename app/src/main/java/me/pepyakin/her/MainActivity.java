package me.pepyakin.her;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import me.pepyakin.her.bot.BotService;
import me.pepyakin.her.model.Chat;
import me.pepyakin.her.model.ChatItem;
import me.pepyakin.her.view.ChatView;
import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private Chat chat;
    private Subscription locationSubscription;
    private Subscription chatSubscription;

    private boolean locationSent = false;

    private ChatView chatView;

    public static Intent buildIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chat = Chat.getInstance(this);

        chatView = new ChatView(this);
        setContentView(chatView);

        if (savedInstanceState == null) {
            startService(new Intent(this, BotService.class));
        } else {
            locationSent = savedInstanceState.getBoolean("locationSent");
        }

        chatView.setOnUserSentMessage(new ChatView.OnUserSentMessage() {
            @Override
            public void onUserSentMessage(String message) {
                chat.send(message);
            }
        });
        chatSubscription = chat.getChat().subscribe(new Action1<List<ChatItem>>() {
            @Override
            public void call(List<ChatItem> chat) {
                chatView.setItems(chat);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        NotificationController.chatActivityStarted(this);

        if (!locationSent) {
            locationSubscription = RxLocationManagerAdapter.singleMostAccurateLocation(this)
                    .subscribe(new Action1<Location>() {
                        @Override
                        public void call(Location location) {
                            // TODO: Get real coordinates
                            String coordinates = location.toString();
                            chat.send(coordinates);

                            locationSent = true;
                        }
                    });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        NotificationController.chatActivityStopped(this);

        locationSubscription.unsubscribe();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("locationSent", locationSent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        chatSubscription.unsubscribe();
    }
}
