package me.pepyakin.her;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.pepyakin.her.bot.BotService;
import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private Chat chat;

    private LinearLayout chatView;

    private Subscription locationSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText messageView = (EditText) findViewById(R.id.main_message);
        chatView = (LinearLayout) findViewById(R.id.main_chat);

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
        chat.getChat().subscribe(new Action1<Chat.ChatItem>() {
            @Override
            public void call(Chat.ChatItem chatItem) {
                addChatItem(chatItem);
            }
        });
    }

    private void addChatItem(Chat.ChatItem chatItem) {
        TextView chatItemView = new TextView(this);
        chatItemView.setText(chatItem.text);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (chatItem.inbound) {
            lp.gravity = Gravity.START;
        } else {
            lp.gravity = Gravity.END;
        }
        chatView.addView(chatItemView, lp);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationSubscription.unsubscribe();
    }
}
