package com.quillraven.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.quillraven.game.core.EGameState;
import com.quillraven.game.core.Game;

public class MysticGarden extends ApplicationAdapter {
    public static final float UNIT_SCALE = 1 / 32f;

    private Game game;

    @Override
    public void create() {
        this.game = new Game(EGameState.LOADING);
    }

    @Override
    public void render() {
        game.process();
    }

    @Override
    public void resize(final int width, final int height) {
        game.resize(width, height);
    }

    @Override
    public void dispose() {
        game.dispose();
    }
}
