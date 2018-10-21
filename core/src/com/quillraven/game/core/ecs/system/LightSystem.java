package com.quillraven.game.core.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.quillraven.game.core.ecs.component.Box2DComponent;
import com.quillraven.game.ecs.ECSEngine;

public class LightSystem extends IteratingSystem {

    public LightSystem() {
        super(Family.all(Box2DComponent.class).get());
    }

    @Override
    protected void processEntity(final Entity entity, final float deltaTime) {
        final Box2DComponent b2dCmp = ECSEngine.b2dCmpMapper.get(entity);
        if (b2dCmp.light != null && b2dCmp.lightFluctuationDistance > 0) {
            b2dCmp.lightFluctuationTime += (b2dCmp.lightFluctuationSpeed * deltaTime);
            if (b2dCmp.lightFluctuationTime > MathUtils.PI2) {
                b2dCmp.lightFluctuationTime = 0;
            }
            b2dCmp.light.setDistance(b2dCmp.lightDistance + MathUtils.sin(b2dCmp.lightFluctuationTime) * b2dCmp.lightFluctuationDistance);
        }
    }
}
