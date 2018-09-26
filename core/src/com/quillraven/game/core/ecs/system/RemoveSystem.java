package com.quillraven.game.core.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.quillraven.game.core.ecs.component.RemoveComponent;

public class RemoveSystem extends IteratingSystem {
    public RemoveSystem() {
        super(Family.all(RemoveComponent.class).get());
    }

    @Override
    protected void processEntity(final Entity entity, final float deltaTime) {
        this.getEngine().removeEntity(entity);
    }
}
