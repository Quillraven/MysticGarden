package com.quillraven.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.quillraven.game.MysticGarden;
import com.quillraven.game.core.gamestate.EGameState;

public final class Utils {
    private Utils() {

    }

    public static SpriteBatch getSpriteBatch() {
        return ((MysticGarden) Gdx.app.getApplicationListener()).getSpriteBatch();
    }

    public static ResourceManager getResourceManager() {
        return ((MysticGarden) Gdx.app.getApplicationListener()).getResourceManager();
    }

    public static void setGameState(final EGameState gameStateType) {
        setGameState(gameStateType, false);
    }

    public static void setGameState(final EGameState gameStateType, final boolean disposeActive) {
        ((MysticGarden) Gdx.app.getApplicationListener()).getGame().setGameState(gameStateType, disposeActive);
    }
}
