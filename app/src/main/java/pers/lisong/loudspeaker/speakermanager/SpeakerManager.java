package pers.lisong.loudspeaker.speakermanager;

import android.media.MediaPlayer;

import java.io.IOException;

import pers.lisong.loudspeaker.audiofocusmanager.inf.IAudioFocusManager;
import pers.lisong.loudspeaker.speakermanager.inf.ISpeakerManager;

public class SpeakerManager extends MediaPlayer implements ISpeakerManager {
    public static final int PLAY_STATE_STOP = 0;
    public static final int PLAY_STATE_START = 1;
    public static final int PLAY_STATE_COMPLETION = 2;

    private IAudioFocusManager audioFocusManager;
    private OnPlayStateChanged onPlayStateChanged;

    public SpeakerManager(IAudioFocusManager audioFocusManager) {
        this.audioFocusManager = audioFocusManager;
        setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                audioFocusManager.abandonAudioFocus();
                if (onPlayStateChanged == null) {
                    return;
                }
                onPlayStateChanged.OnPlayStateChanged(mp, PLAY_STATE_COMPLETION);
            }
        });
    }

    @Override
    public void start(String path) throws IOException {
        reset();
        setDataSource(path);
        prepare();
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        this.audioFocusManager.requestAudioFocus();
        if (onPlayStateChanged == null) {
            return;
        }
        onPlayStateChanged.OnPlayStateChanged(this, PLAY_STATE_START);
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        this.audioFocusManager.abandonAudioFocus();
        if (onPlayStateChanged == null) {
            return;
        }
        onPlayStateChanged.OnPlayStateChanged(this, PLAY_STATE_STOP);
    }

    @Override
    public void suppressVolume(boolean isSuppress) {
        if (isSuppress) {
            setVolume(0.3f, 0.3f);
        } else {
            setVolume(1, 1);
        }
    }

    public OnPlayStateChanged getOnPlayStateChanged() {
        return onPlayStateChanged;
    }

    public void setOnPlayStateChanged(OnPlayStateChanged onPlayStateChanged) {
        this.onPlayStateChanged = onPlayStateChanged;
    }

    public interface OnPlayStateChanged {
        void OnPlayStateChanged(MediaPlayer mp, int state);
    }
}
