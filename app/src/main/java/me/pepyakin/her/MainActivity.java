package me.pepyakin.her;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import me.pepyakin.her.bot.BotService;
import me.pepyakin.her.model.Chat;
import me.pepyakin.her.model.ChatItem;
import me.pepyakin.her.model.GeoPoint;
import me.pepyakin.her.util.Preconditions;
import me.pepyakin.her.view.ChatView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private Chat chat;
    private PermissionRequester permissionRequester;

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
        permissionRequester = new PermissionRequester(this);

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
            requestAndEventuallySendLocation();
        }
    }

    private void requestAndEventuallySendLocation() {
        final Observable<GeoPoint> deviceLocationObservable
                = RxLocationProviderAdapter.singleMostAccurateLocation(this);

        String[] permissions = {ACCESS_FINE_LOCATION};
        locationSubscription = permissionRequester.ensurePermissions(permissions)
                .flatMap(new Func1<Boolean, Observable<GeoPoint>>() {
                    @Override
                    public Observable<GeoPoint> call(Boolean permissionsGranted) {
                        if (permissionsGranted) {
                            return deviceLocationObservable;
                        } else {
                            // TODO: Heuristics?
                            return Observable.never();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GeoPoint>() {
                    @Override
                    public void call(GeoPoint geoPoint) {
                        String coordinates = geoPoint.toString();
                        chat.send(coordinates);
                        locationSent = true;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.w("MainActivity", throwable);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        NotificationController.chatActivityStopped(this);

        if (locationSubscription != null) {
            locationSubscription.unsubscribe();
        }
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

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        Preconditions.check(permissions.length == grantResults.length);
        if (permissions.length == 0) {
            // Cancelled.
            return;
        }

        permissionRequester.onRequestPermissionResult(
                permissions, grantResults);
    }
}
