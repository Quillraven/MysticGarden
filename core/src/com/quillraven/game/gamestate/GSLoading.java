package com.quillraven.game.gamestate;

import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.quillraven.game.core.AudioManager;
import com.quillraven.game.core.ResourceManager;
import com.quillraven.game.core.Utils;
import com.quillraven.game.core.ecs.component.ParticleEffectComponent;
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
        resourceManager.load("characters_and_effects/character_and_effect.atlas", TextureAtlas.class);
        resourceManager.load("map/tiles/map.atlas", TextureAtlas.class);
        resourceManager.load("map/map.tmx", TiledMap.class);
        loadAudio();

        final ParticleEffectLoader.ParticleEffectParameter peParams = new ParticleEffectLoader.ParticleEffectParameter();
        peParams.atlasFile = "characters_and_effects/character_and_effect.atlas";
        for (final ParticleEffectComponent.ParticleEffectType peType : ParticleEffectComponent.ParticleEffectType.values()) {
            if (peType == ParticleEffectComponent.ParticleEffectType.NOT_DEFINED) {
                continue;
            }
            resourceManager.load(peType.getEffectFilePath(), ParticleEffect.class, peParams);
        }
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
            Utils.getAudioManager().playAudio(AudioManager.AudioType.INTRO);
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
            Utils.getAudioManager().playAudio(AudioManager.AudioType.SELECT);
            Utils.setGameState(EGameState.MENU, true);
        }
    }

    @Override
    public void keyUp(final InputManager manager, final EKey key) {
        // nothing to do for key up
    }
}
