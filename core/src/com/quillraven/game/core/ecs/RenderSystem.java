package com.quillraven.game.core.ecs;

import com.badlogic.gdx.utils.Disposable;

public interface RenderSystem extends Disposable {
    void render(final float alpha);

    void resize(final int width, final int height);
}
