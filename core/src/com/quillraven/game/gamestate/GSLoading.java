package com.quillraven.game.gamestate;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.quillraven.game.core.*;
import com.quillraven.game.ui.LoadingUI;

public class GSLoading extends GameState<LoadingUI> {
    private final AssetManager assetManager;

    public GSLoading(final Game game, final LoadingUI hud) {
        super(game, hud);

        assetManager = game.getAssetManager();
        loadTextureAtlas();
        loadAudio();
    }

    private void loadTextureAtlas() {
        assetManager.load("map/tiles/map.atlas", TextureAtlas.class);
    }

    private void loadAudio() {
        for (final AudioManager.AudioType type : AudioManager.AudioType.values()) {
            if (type.isMusic()) {
                assetManager.load(type.getFilePath(), Music.class);
            } else {
                assetManager.load(type.getFilePath(), Sound.class);
            }
        }
    }

    @Override
    public void activate() {
        game.getAudioManager().playAudio(AudioManager.AudioType.INTRO);
        super.activate();
    }

    @Override
    public void processInput(final InputController inputController) {
        if (assetManager.getProgress() == 1 && inputController.isAnyKeyPressed()) {
            game.setGameState(EGameState.GAME);
        }
    }

    @Override
    public void step(final float fixedTimeStep) {
        assetManager.update();
        hud.setProgress(assetManager.getProgress());
        super.step(fixedTimeStep);
    }
}
