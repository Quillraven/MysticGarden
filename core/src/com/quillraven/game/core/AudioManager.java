package com.quillraven.game.core;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

public class AudioManager {
    private AudioType currentMusicType;
    private Music currentMusic;
    private final ResourceManager resourceManager;
    private float volume;

    public AudioManager() {
        this.resourceManager = Utils.getResourceManager();
        this.currentMusic = null;
        if (Utils.getPreferenceManager().containsKey("volume")) {
            volume = Utils.getPreferenceManager().getFloatValue("volume");
        } else {
            volume = 1f;
        }
    }

    public void setVolume(final float volume) {
        this.volume = MathUtils.clamp(volume, 0f, 1f);
        if (currentMusic != null) {
            currentMusic.setVolume(currentMusicType.volume * volume);
        }
    }

    public float getVolume() {
        return this.volume;
    }

    public void playAudio(final AudioType type) {
        if (type.isMusic) {
            if (type == currentMusicType) {
                // continue current music
                return;
            } else if (currentMusic != null) {
                currentMusic.stop();
            }
            currentMusicType = type;
            currentMusic = resourceManager.get(type.filePath, Music.class);
            currentMusic.setLooping(true);
            currentMusic.setVolume(type.volume * volume);
            currentMusic.play();
        } else {
            resourceManager.get(type.filePath, Sound.class).play(type.volume * volume);
        }
    }

    public enum AudioType {
        // music
        INTRO("audio/intro.mp3", true, 0.3f),
        ALMOST_FINISHED("audio/almost_finished.ogg", true, 0.35f),
        VICTORY("audio/victory.mp3", true, 0.3f),
        // sounds
        SELECT("audio/select.wav", false, 0.5f),
        CHOP("audio/chop.ogg", false, 1),
        SMASH("audio/smash.ogg", false, 1),
        SWING("audio/swing.ogg", false, 1),
        CRYSTAL_PICKUP("audio/crystal_pickup.ogg", false, 1),
        JINGLE("audio/jingle.wav", false, 0.7f);

        private final String filePath;
        private final boolean isMusic;
        private final float volume;

        AudioType(final String filePath, final boolean isMusic, final float volume) {
            this.filePath = filePath;
            this.isMusic = isMusic;
            this.volume = volume;
        }

        public String getFilePath() {
            return filePath;
        }

        public boolean isMusic() {
            return isMusic;
        }
    }
}
