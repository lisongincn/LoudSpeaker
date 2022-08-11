package pers.lisong.loudspeaker.fileselector;

import android.content.Context;
import android.net.Uri;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.HashSet;

import pers.lisong.loudspeaker.fileselector.inf.IFileSelector;
import pers.lisong.loudspeaker.fileselector.listener.FileSelectorListener;
import pers.lisong.loudspeaker.listenermanager.ListenerManager;
import pers.lisong.loudspeaker.listenermanager.inf.IListenerManager;
import pers.lisong.loudspeaker.utils.Utils;

public class FileSelector implements IFileSelector {
    private final IListenerManager<FileSelectorListener> listenerManager;
    private final Context context;
    private ActivityResultLauncher<String> activityResultLauncher;

    public FileSelector(Context context) {
        this.listenerManager = new ListenerManager<>();
        this.context = context;
        initActivityResultLauncher();
    }

    public void initActivityResultLauncher() {
        ActivityResultCaller activityResultCaller = (ActivityResultCaller) this.context;
        activityResultLauncher = activityResultCaller.registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                HashSet<FileSelectorListener> allListener = listenerManager.getAllListener();
                for (FileSelectorListener next : allListener) {
                    String path = null;
                    if (result != null) {
                        path = Utils.getPath(context, result);
                    }
                    next.onSelectFile(path);
                }
            }
        });
    }

    @Override
    public void openFileSelector() {
        this.activityResultLauncher.launch("audio/*");
    }

    public IListenerManager<FileSelectorListener> getListenerManager() {
        return listenerManager;
    }
}
