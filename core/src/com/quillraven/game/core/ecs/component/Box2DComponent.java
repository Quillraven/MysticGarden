package com.quillraven.game.core.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

public class Box2DComponent implements Component, Pool.Poolable {
    public Body body;
    public final Vector2 positionBeforeUpdate = new Vector2();
    public float width;
    public float height;

    @Override
    public void reset() {
        if (body != null) {
            body.getWorld().destroyBody(body);
            body = null;
        }
        positionBeforeUpdate.set(0, 0);
        width = height = 0;
    }
}
