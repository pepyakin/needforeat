package me.pepyakin.needforeat;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.pepyakin.needforeat.PermissionAskAgent.PermissionResponse;
import me.pepyakin.needforeat.util.Functionals;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

final class PermissionRequester {

    private final Map<String, Subject<Boolean, Boolean>> subjects;
    private final PermissionAskAgent permissionAskAgent;
    private final PermissionAskAgent.Listener listener;

    PermissionRequester(PermissionAskAgent permissionAskAgent) {
        this.permissionAskAgent = permissionAskAgent;
        this.subjects = new HashMap<>();
        this.listener = new PermissionAskAgent.Listener() {
            @Override
            public void onPermissionResponse(
                    PermissionResponse[] permissionResponses) {
                for (PermissionResponse resp : permissionResponses) {
                    subjects.get(resp.name).onNext(resp.granted);
                }
            }
        };
    }

    public Observable<Boolean> ensurePermissions(String[] permissions) {
        return Observable.from(permissions)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String permission) {
                        return !permissionAskAgent.isGranted(permission);
                    }
                })
                .toList()
                .flatMap(new Func1<List<String>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(List<String> permissionList) {
                        String[] permissions = new String[permissionList.size()];
                        permissionList.toArray(permissions);
                        return requestPermissions(permissions);
                    }
                });
    }

    @NonNull
    private Observable<Boolean> requestPermissions(String[] permissions) {
        if (permissions.length == 0) {
            // No permissions requested or all already granted.
            return Observable.just(true);
        }

        List<Observable<Boolean>> allPermissionSubjects = new ArrayList<>();
        for (String permission : permissions) {
            PublishSubject<Boolean> subject = PublishSubject.create();
            subjects.put(permission, subject);
            allPermissionSubjects.add(subject);
        }

        permissionAskAgent.setListener(listener);
        permissionAskAgent.askPermissions(permissions);

        return Observable.merge(allPermissionSubjects)
                .take(allPermissionSubjects.size())
                .all(Functionals.<Boolean>id());
    }
}
