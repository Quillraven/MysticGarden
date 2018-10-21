package com.quillraven.game.core.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.quillraven.game.core.ecs.EntityEngine;
import com.quillraven.game.core.ecs.component.AnimationComponent;

public class AnimationSystem extends IteratingSystem {
    public AnimationSystem() {
        super(Family.all(AnimationComponent.class).get());
    }

    @Override
    protected void processEntity(final Entity entity, final float deltaTime) {
        EntityEngine.aniCmpMapper.get(entity).aniTimer += deltaTime;
    }
}
