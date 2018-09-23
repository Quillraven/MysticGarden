package com.quillraven.game.core;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public enum AudioManager {
    INSTANCE;

    private Music currentMusic;
    private final ResourceManager resourceManager;

    AudioManager() {
        this.resourceManager = Utils.getResourceManager();
        this.currentMusic = null;
    }

    public void playAudio(final AudioType type) {
        if (type.isMusic) {
            final Music music = resourceManager.get(type.filePath, Music.class);
            if (music.equals(currentMusic)) {
                // continue current music
                return;
            } else if (currentMusic != null) {
                currentMusic.stop();
            }
            currentMusic = music;
            currentMusic.setLooping(true);
            currentMusic.setVolume(type.volume);
            currentMusic.play();
        } else {
            resourceManager.get(type.filePath, Sound.class).play(type.volume);
        }
    }

    public enum AudioType {
        // music
        INTRO("audio/intro.mp3", true, 0.3f),
        ALMOST_FINISHED("audio/almost_finished.ogg", true),
        // sounds
        SELECT("audio/select.wav", false),
        CHOP("audio/chop.ogg", false);

        private final String filePath;
        private final boolean isMusic;
        private final float volume;

        AudioType(final String filePath, final boolean isMusic, final float volume) {
            this.filePath = filePath;
            this.isMusic = isMusic;
            this.volume = volume;
        }

        AudioType(final String filePath, final boolean isMusic) {
            this(filePath, isMusic, 1);
        }

        public String getFilePath() {
            return filePath;
        }

        public boolean isMusic() {
            return isMusic;
        }
    }
}
