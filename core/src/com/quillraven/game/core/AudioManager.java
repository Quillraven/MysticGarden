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
            music.play();
        } else {
            assetManager.get(type.filePath, Sound.class).play();
        }
    }

    public enum AudioType {
        INTRO("audio/intro.mp3", true);

        private final String filePath;
        private final boolean isMusic;

        AudioType(final String filePath, final boolean isMusic) {
            this.filePath = filePath;
            this.isMusic = isMusic;
        }

        public String getFilePath() {
            return filePath;
        }

        public boolean isMusic() {
            return isMusic;
        }
    }
}
