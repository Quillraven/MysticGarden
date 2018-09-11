package com.quillraven.game.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.quillraven.game.core.Game;

public abstract class HUD implements Disposable {
    private static final String TAG = HUD.class.getSimpleName();

    private final Stage stage;

    protected HUD(final Game game) {
        stage = new Stage(new ScreenViewport(new OrthographicCamera(0, 0)), game.getSpriteBatch());
    }

    public Stage getStage() {
        return stage;
    }

    public void step(final float deltaTime) {
        stage.act(deltaTime);
    }

    public void render() {
        stage.getViewport().apply();
        stage.draw();
    }

    public void resize(final int width, final int height) {
        Gdx.app.debug(TAG, "Resizing HUD " + this + " to " + width + "x" + height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        Gdx.app.debug(TAG, "Disposing HUD " + this);
        stage.dispose();
    }
}
