package pers.lisong.loudspeaker.requestpermissionmanager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import java.util.HashSet;

import pers.lisong.loudspeaker.listenermanager.ListenerManager;
import pers.lisong.loudspeaker.requestpermissionmanager.inf.IRequestPermissionManager;
import pers.lisong.loudspeaker.requestpermissionmanager.listener.RequestPermissionManagerListener;

public class RequestPermissionManager implements IRequestPermissionManager {
    public static final String PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;

    private final Context context;
    private final ListenerManager<RequestPermissionManagerListener> listenerManager = new ListenerManager<>();
    private ActivityResultLauncher<String> activityResultLauncher;

    public RequestPermissionManager(Context context) {
        this.context = context;
        initActivityResultLauncher();
    }

    private void initActivityResultLauncher() {
        ActivityResultCaller activityResultCaller = (ActivityResultCaller) this.context;
        activityResultLauncher = activityResultCaller.registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                HashSet<RequestPermissionManagerListener> allListener = listenerManager.getAllListener();
                for (RequestPermissionManagerListener next : allListener) {
                    next.onAuthorization(result);
                }
            }
        });
    }

    @Override
    public boolean checkPermission() {
        int permission;
        permission = ContextCompat.checkSelfPermission(context, PERMISSION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestPermission() {
        activityResultLauncher.launch(PERMISSION);
    }

    public ListenerManager<RequestPermissionManagerListener> getListenerManager() {
        return listenerManager;
    }
}
