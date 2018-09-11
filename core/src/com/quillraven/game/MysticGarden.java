package com.quillraven.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.quillraven.game.core.Game;
import com.quillraven.game.core.EGameState;

public class MysticGarden extends ApplicationAdapter {
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
