package com.quillraven.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Disposable;
import com.quillraven.game.core.ui.HUD;

public abstract class GameState<T extends HUD> implements Disposable {
    private static final String TAG = GameState.class.getSimpleName();

    protected final T hud;
    protected final Game game;
    private final InputMultiplexer inputMultiplexer;

    protected GameState(final Game game, final T hud) {
        this.hud = hud;
        this.game = game;
        this.inputMultiplexer = new InputMultiplexer(game.getInputController(), hud.getStage());
    }

    public abstract void processInput(final InputController inputController);

    public void activate() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void deactivate() {
        Gdx.input.setInputProcessor(null);
    }

    public void step(final float fixedTimeStep) {
        hud.step(fixedTimeStep);
    }

    public void render(final float alpha) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        hud.render();
    }

    public void resize(final int width, final int height) {
        Gdx.app.debug(TAG, "Resizing gamestate " + this + " to " + width + "x" + height);
        hud.resize(width, height);
    }

    @Override
    public void dispose() {
        Gdx.app.debug(TAG, "Disposing gamestate " + this);
        hud.dispose();
    }
}
