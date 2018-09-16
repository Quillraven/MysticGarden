package com.quillraven.game.core.ecs;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.quillraven.game.core.InputController;

public abstract class EntityEngine extends PooledEngine implements Disposable {
    private static final String TAG = EntityEngine.class.getSimpleName();

    private final Array<RenderSystem> renderSystems;

    protected EntityEngine() {
        super();
        this.renderSystems = new Array<>();
    }

    protected void addRenderSystem(final RenderSystem renderSystem) {
        renderSystems.add(renderSystem);
    }

    public void processInput(final InputController inputController) {
        for (final EntitySystem system : this.getSystems()) {
            ((IteratingInputSystem) system).processInput(inputController);
        }
    }

    public void render(final float alpha) {
        for (final RenderSystem system : renderSystems) {
            system.render(alpha);
        }
    }

    public void resize(final int width, final int height) {
        Gdx.app.debug(TAG, "Resizing ECSEngine " + this + " to " + width + "x" + height);
        for (final RenderSystem system : renderSystems) {
            system.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        Gdx.app.debug(TAG, "Disposing ECSEngine " + this);
        for (final RenderSystem system : renderSystems) {
            system.dispose();
        }
    }
}
