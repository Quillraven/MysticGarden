package com.quillraven.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.quillraven.game.MysticGarden;
import com.quillraven.game.WorldContactManager;
import com.quillraven.game.core.gamestate.EGameState;
import com.quillraven.game.core.input.InputManager;
import com.quillraven.game.map.MapManager;

public final class Utils {
    private Utils() {

    }

    public static SpriteBatch getSpriteBatch() {
        return ((MysticGarden) Gdx.app.getApplicationListener()).getSpriteBatch();
    }

    public static ResourceManager getResourceManager() {
        return ((MysticGarden) Gdx.app.getApplicationListener()).getResourceManager();
    }

    public static WorldContactManager getWorldContactManager() {
        return ((MysticGarden) Gdx.app.getApplicationListener()).getWorldContactManager();
    }

    public static AudioManager getAudioManager() {
        return ((MysticGarden) Gdx.app.getApplicationListener()).getAudioManager();
    }

    public static MapManager getMapManager() {
        return ((MysticGarden) Gdx.app.getApplicationListener()).getMapManager();
    }

    public static PreferenceManager getPreferenceManager() {
        return ((MysticGarden) Gdx.app.getApplicationListener()).getPreferenceManager();
    }

    public static InputManager getInputManager() {
        return (InputManager) ((InputMultiplexer) Gdx.input.getInputProcessor()).getProcessors().get(0);
    }

    public static void setGameState(final EGameState gameStateType) {
        setGameState(gameStateType, false);
    }

    public static void setGameState(final EGameState gameStateType, final boolean disposeActive) {
        ((MysticGarden) Gdx.app.getApplicationListener()).getGame().setGameState(gameStateType, disposeActive);
    }
}
