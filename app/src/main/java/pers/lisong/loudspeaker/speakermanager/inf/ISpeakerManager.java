package pers.lisong.loudspeaker.speakermanager.inf;

import java.io.IOException;

public interface ISpeakerManager {
    void start(String path) throws IOException;
    void suppressVolume(boolean isSuppress);
}