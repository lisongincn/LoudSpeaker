package pers.lisong.loudspeaker;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import pers.lisong.loudspeaker.audiofocusmanager.AudioFocusManager;
import pers.lisong.loudspeaker.audiofocusmanager.listener.AudioFocusManagerListener;
import pers.lisong.loudspeaker.fileselector.FileSelector;
import pers.lisong.loudspeaker.fileselector.listener.FileSelectorListener;
import pers.lisong.loudspeaker.requestpermissionmanager.RequestPermissionManager;
import pers.lisong.loudspeaker.speakermanager.SpeakerManager;

public class MainActivity extends AppCompatActivity implements FileSelectorListener, SpeakerManager.OnPlayStateChanged, AudioFocusManagerListener {
    private static final String TAG = "MainActivity";
    private static final String PREFERENCES_KEY_FILEPATH = "file_path";
    private RequestPermissionManager requestPermissionManager;
    private FileSelector fileSelector;
    private SpeakerManager speakerManager;
    private AudioFocusManager audioFocusManager;
    private String filePath;
    private EditText fileSelectEditText;
    private Button FileSelectButton;
    private Button startButton;
    private Handler handler;
    private Runnable runnable;
    private EditText intervalEditText;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initID();
        initObject();
        initListener();
        initFilePath();
    }

    private void initID() {
        fileSelectEditText = findViewById(R.id.fileSelect_EditText);
        FileSelectButton = findViewById(R.id.fileSelect_Button);
        startButton = findViewById(R.id.start_button);
        intervalEditText = findViewById(R.id.interval_editText);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initObject() {
        requestPermissionManager = new RequestPermissionManager(this);
        fileSelector = new FileSelector(this);
        audioFocusManager = new AudioFocusManager(this);
        speakerManager = new SpeakerManager(audioFocusManager);
        filePath = getFilePath();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    speakerManager.start(getFilePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void initListener() {
        fileSelector.getListenerManager().addListener(this);
        audioFocusManager.getListenerManager().addListener(this);
        speakerManager.setOnPlayStateChanged(this);
        FileSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!requestPermissionManager.checkPermission()) {
                    requestPermissionManager.requestPermission();
                    return;
                }
                fileSelector.openFileSelector();
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!speakerManager.isPlaying() && startButton.getText().toString().equals(getResources().getString(R.string.start_button_start))) {
                    if (getFilePath() == null) {
                        Toast.makeText(v.getContext(), "文件无效", Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        speakerManager.start(getFilePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.removeCallbacks(runnable);
                    speakerManager.stop();
                }
            }
        });
    }

    void initFilePath() {
        String filePath = getFilePath();
        if (filePath != null) {
            fileSelectEditText.setText(new File(getFilePath()).getName());
        }
    }

    String getFilePath() {
        if (this.filePath == null) {
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            String filePath = preferences.getString(PREFERENCES_KEY_FILEPATH, null);
            if (filePath == null || !new File(filePath).exists()) {
                return null;
            } else {
                return filePath;
            }
        }
        return this.filePath;
    }

    void setFilePath(String filePath) {
        this.filePath = filePath;
        SharedPreferences.Editor edit = getPreferences(MODE_PRIVATE).edit();
        edit.putString(PREFERENCES_KEY_FILEPATH, filePath);
        edit.apply();
    }

    @Override
    public void onSelectFile(String path) {
        if (path == null) {
            return;
        }
        setFilePath(path);
        fileSelectEditText.setText(new File(getFilePath()).getName());
    }

    @Override
    public void onGain() {
        speakerManager.suppressVolume(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onLoss() {
        audioFocusManager.requestAudioFocus();
    }

    @Override
    public void onTransient() {
        speakerManager.suppressVolume(true);
    }

    @Override
    public void OnPlayStateChanged(MediaPlayer mp, int state) {
        switch (state) {
            case SpeakerManager.PLAY_STATE_START:
                startButton.setText(getResources().getString(R.string.start_button_stop));
                break;
            case SpeakerManager.PLAY_STATE_STOP:
                startButton.setText(getResources().getString(R.string.start_button_start));
                break;
            case SpeakerManager.PLAY_STATE_COMPLETION:
                String intervalString = intervalEditText.getText().toString();
                long interval;
                try {
                    interval = Long.parseLong(intervalString);
                } catch (NumberFormatException e) {
                    interval = 0;
                }
                handler.postDelayed(runnable, interval);
                break;
        }
    }
}