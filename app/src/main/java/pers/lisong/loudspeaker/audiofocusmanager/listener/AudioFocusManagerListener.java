package pers.lisong.loudspeaker.audiofocusmanager.listener;

public interface AudioFocusManagerListener {
    /**
     * 获得焦点，可以恢复音量
     */
    void onGain();
    /**
     * 永久失去焦点，停止播放
     */
    void onLoss();
    /**
     * 暂时失去焦点，降低音量
     */
    void onTransient();
}