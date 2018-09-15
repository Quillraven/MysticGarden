package com.quillraven.game.core;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
    private final Music currentMusic;
    private final AssetManager assetManager;

    AudioManager(final AssetManager assetManager) {
        this.assetManager = assetManager;
        this.currentMusic = null;
    }

    public void playAudio(final AudioType type) {
        if (type.isMusic) {
            final Music music = assetManager.get(type.filePath, Music.class);
            if (music.equals(currentMusic)) {
                // continue current music
                return;
            } else if (currentMusic != null) {
                currentMusic.stop();
            }
            music.setLooping(true);
            music.setVolume(type.volume);
            music.play();
        } else {
            assetManager.get(type.filePath, Sound.class).play(type.volume);
        }
    }

    public enum AudioType {
        // music
        INTRO("audio/intro.mp3", true, 0.3f),
        // sounds
        SELECT("audio/select.wav", false);

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
