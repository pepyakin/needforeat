package me.pepyakin.her;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private Subscription locationSubscription;

    private TextView chatTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatTextView = (TextView) findViewById(R.id.main_chat);

        if (savedInstanceState == null) {
            // TODO: Send initial message
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationSubscription = RxLocationManagerAdapter.deviceLocation(this)
                .subscribe(new Action1<Location>() {
                    @Override
                    public void call(Location location) {
                        chatTextView.setText("location=" + location);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationSubscription.unsubscribe();
    }
}
