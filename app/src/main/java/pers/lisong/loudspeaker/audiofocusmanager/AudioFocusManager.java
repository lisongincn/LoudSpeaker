package pers.lisong.loudspeaker.audiofocusmanager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashSet;

import pers.lisong.loudspeaker.audiofocusmanager.inf.IAudioFocusManager;
import pers.lisong.loudspeaker.audiofocusmanager.listener.AudioFocusManagerListener;
import pers.lisong.loudspeaker.listenermanager.ListenerManager;

public class AudioFocusManager implements IAudioFocusManager {
    private Context context;
    private ListenerManager<AudioFocusManagerListener> listenerManager;
    private AudioManager audioManager;
    private AudioAttributes audioAttributes;
    private AudioFocusRequest audioFocusRequest;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AudioFocusManager(Context context) {
        this.context = context;
        listenerManager = new ListenerManager<>();
        initAudioManager();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initAudioManager() {
        audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                HashSet<AudioFocusManagerListener> allListener = listenerManager.getAllListener();
                for (AudioFocusManagerListener next : allListener) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN://恢复
                            next.onGain();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS://停止
                            next.onLoss();
                            break;
                        default: //降低音量
                            next.onTransient();
                            break;
                    }
                }
            }
        };

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setFocusGain(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void requestAudioFocus() {
        audioManager.requestAudioFocus(audioFocusRequest);
    }

    @Override
    public void abandonAudioFocus() {
        audioManager.abandonAudioFocus(audioFocusChangeListener);
    }

    public ListenerManager<AudioFocusManagerListener> getListenerManager() {
        return listenerManager;
    }
}