package me.pepyakin.her;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.pepyakin.her.util.Functionals;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

final class PermissionRequester {

    private static final Map<String, Subject<Boolean, Boolean>> subjects = new
            HashMap<>();
    final Activity activity;

    PermissionRequester(Activity activity) {
        this.activity = activity;
    }

    public Observable<Boolean> ensurePermissions(String[] permissions) {
        return Observable.from(permissions)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String permission) {
                        return !isGranted(permission);
                    }
                })
                .toList()
                .flatMap(new Func1<List<String>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(List<String> permissionList) {
                        List<Observable<Boolean>> allPermissionSubjects
                                = new ArrayList<>();
                        for (String permission : permissionList) {
                            PublishSubject<Boolean> subject = PublishSubject.create();
                            subjects.put(permission, subject);
                            allPermissionSubjects.add(subject);
                        }

                        String[] permissions = new String[permissionList.size()];
                        ActivityCompat.requestPermissions(activity,
                                permissionList.toArray(permissions), 0);

                        return Observable.merge(allPermissionSubjects)
                                .take(allPermissionSubjects.size())
                                .all(Functionals.<Boolean>id());
                    }
                });
    }

    public void onRequestPermissionResult(
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        for (int i = 0; i < permissions.length; i++) {
            subjects.get(permissions[i]).onNext(grantResults[i] ==
                    PackageManager.PERMISSION_GRANTED);
        }
    }

    private boolean isGranted(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }
}
