package com.quillraven.game.ecs.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.quillraven.game.ecs.component.AnimationComponent;

public class AnimationSystem extends IteratingSystem {
    private final ComponentMapper<AnimationComponent> aniCmpMapper;

    public AnimationSystem(final ComponentMapper<AnimationComponent> aniCmpMapper) {
        super(Family.all(AnimationComponent.class).get());

        this.aniCmpMapper = aniCmpMapper;
    }

    @Override
    protected void processEntity(final Entity entity, final float deltaTime) {
        aniCmpMapper.get(entity).aniTimer += deltaTime;
    }
}
