package com.quillraven.game.gamestate;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.quillraven.game.core.AudioManager;
import com.quillraven.game.core.ResourceManager;
import com.quillraven.game.core.Utils;
import com.quillraven.game.core.gamestate.EGameState;
import com.quillraven.game.core.gamestate.GameState;
import com.quillraven.game.core.input.EKey;
import com.quillraven.game.core.input.InputManager;
import com.quillraven.game.core.ui.HUD;
import com.quillraven.game.core.ui.TTFSkin;
import com.quillraven.game.ui.LoadingUI;

public class GSLoading extends GameState<LoadingUI> {
    private final ResourceManager resourceManager;
    private boolean isMusicLoaded;

    public GSLoading(final EGameState type, final HUD hud) {
        super(type, hud);
        isMusicLoaded = false;

        resourceManager = Utils.getResourceManager();
        resourceManager.load("characters/character.atlas", TextureAtlas.class);
        resourceManager.load("map/tiles/map.atlas", TextureAtlas.class);
        resourceManager.load("map/map.tmx", TiledMap.class);
        loadAudio();
    }

    @Override
    protected LoadingUI createHUD(final HUD hud, final TTFSkin skin) {
        return new LoadingUI(hud, skin);
    }

    private void loadAudio() {
        for (final AudioManager.AudioType type : AudioManager.AudioType.values()) {
            if (resourceManager.isLoaded(type.getFilePath())) {
                continue;
            }
            if (type.isMusic()) {
                resourceManager.load(type.getFilePath(), Music.class);
            } else {
                resourceManager.load(type.getFilePath(), Sound.class);
            }
        }
    }

    @Override
    public void step(final float fixedTimeStep) {
        resourceManager.update();
        gameStateHUD.setProgress(resourceManager.getProgress());
        if (!isMusicLoaded && resourceManager.isLoaded(AudioManager.AudioType.INTRO.getFilePath())) {
            AudioManager.INSTANCE.playAudio(AudioManager.AudioType.INTRO);
            isMusicLoaded = true;
        }
        super.step(fixedTimeStep);
    }

    @Override
    public void dispose() {
        hud.removeGameStateHUD(gameStateHUD);
    }

    @Override
    public void keyDown(final InputManager manager, final EKey key) {
        if (resourceManager.getProgress() == 1) {
            AudioManager.INSTANCE.playAudio(AudioManager.AudioType.SELECT);
            Utils.setGameState(EGameState.GAME, true);
        }
    }

    @Override
    public void keyUp(final InputManager manager, final EKey key) {
        // nothing to do for key up
    }
}
