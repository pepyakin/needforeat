package me.pepyakin.her;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import me.pepyakin.her.model.ChatItem;
import me.pepyakin.her.view.ChatView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.TestSubscriber;
import rx.subscriptions.BooleanSubscription;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ChatViewTest {
    @Rule
    public ActivityTestRule<TestActivity> activityRule =
            new ActivityTestRule<>(TestActivity.class);

    @Test
    public void messageInput() throws Exception {
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();

        Activity activity = activityRule.getActivity();
        ChatView chatView = obtainChatView(activity)
                .toBlocking()
                .single();
        userSentMessage(chatView)
                .take(1)
                .subscribe(testSubscriber);

        onView(withId(R.id.main_message))
                .perform(typeText("hello world"), pressImeActionButton());

        testSubscriber.awaitTerminalEvent(5, TimeUnit.SECONDS);
        testSubscriber.assertValue("hello world");
    }

    @Test
    public void output() throws Exception {
        Activity activity = activityRule.getActivity();
        obtainChatView(activity)
                .doOnNext(new Action1<ChatView>() {
                    @Override
                    public void call(ChatView chatView) {
                        List<ChatItem> chatItems = Arrays.asList(
                                ChatItem.newInbound("hello"),
                                ChatItem.newOutbound("world"));
                        chatView.setItems(chatItems);
                    }
                })
                .toBlocking()
                .last();

    }

    private Observable<ChatView> obtainChatView(final Activity activity) {
        return Observable
                .fromCallable(new Callable<ChatView>() {
                    @Override
                    public ChatView call() throws Exception {
                        ChatView chatView = new ChatView(activity);
                        activity.setContentView(chatView);
                        return chatView;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    private static Observable<String> userSentMessage(final ChatView chatView) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                chatView.setOnUserSentMessage(new ChatView.OnUserSentMessage() {
                    @Override
                    public void onUserSentMessage(String message) {
                        subscriber.onNext(message);
                    }
                });
                subscriber.add(BooleanSubscription.create(new Action0() {
                    @Override
                    public void call() {
                        chatView.setOnUserSentMessage(null);
                    }
                }));
            }
        });
    }
}
