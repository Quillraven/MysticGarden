package com.quillraven.game.core.ecs;

import com.badlogic.gdx.utils.Disposable;

public abstract class RenderSystem implements Disposable {
    protected final EntityEngine entityEngine;

    protected RenderSystem(final EntityEngine entityEngine) {
        this.entityEngine = entityEngine;
    }

    public abstract void render(final float alpha);

    public abstract void resize(final int width, final int height);
}
