package tiny.engine.core;

import tiny.engine.audio.Audio;
import tiny.engine.audio.Music;
import tiny.engine.audio.Sound;

import java.net.URL;

/**
 * The GameAudio class encapsulates and provides access to the underlying audio
 * component. It provides methods to retrieve audio resources such as sounds and
 * music files from either the class path, or file system.
 *
 * @author Damian Strain
 */
public final class GameAudio {

    private final Audio audio;

    /**
     * Constructs the audio component.
     */
    public GameAudio() {
        audio = new Audio();
    }

    /**
     * Initialises the audio component ready for playback. This must be called
     * before loading any audio resources.
     */
    public void init() {
        audio.init();
    }

    /**
     * Returns a Sound resource for the specified URL.
     *
     * @param url the URL for the Sound resource
     * @return a new Sound resource
     */
    public Sound newSound(URL url) {
        return audio.loadSound(url);
    }

    /**
     * Returns a Music resource for the specified URL.
     *
     * @param url the URL for the Music resource
     * @return a new Music resource
     */
    public Music newMusic(URL url) {
        return audio.loadMusic(url);
    }

    /**
     * Shuts down the Audio system, freeing up resources.
     */
    public void shutdown() {
        audio.shutdown();
    }
}
