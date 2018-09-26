package com.quillraven.game.core.ecs.component;

import box2dLight.Light;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

public class Box2DComponent implements Component, Pool.Poolable {
    public Body body;
    public Light light;
    public float lightDistance;
    public float lightFluctuationDistance;
    public float lightFluctuationTime;
    public float lightFluctuationSpeed;
    public final Vector2 positionBeforeUpdate = new Vector2();
    public float width;
    public float height;

    @Override
    public void reset() {
        lightFluctuationDistance = 0;
        lightFluctuationTime = 0;
        lightDistance = 0;
        if (light != null) {
            light.remove(true);
            light = null;
        }
        if (body != null) {
            body.getWorld().destroyBody(body);
            body = null;
        }
        positionBeforeUpdate.set(0, 0);
        width = height = 0;
    }
}
