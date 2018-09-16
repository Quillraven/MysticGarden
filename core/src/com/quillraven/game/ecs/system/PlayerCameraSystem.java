package com.quillraven.game.ecs.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.quillraven.game.core.ecs.component.Box2DComponent;
import com.quillraven.game.ecs.component.PlayerComponent;

public class PlayerCameraSystem extends IteratingSystem {
    private final OrthographicCamera gameCamera;
    private final ComponentMapper<Box2DComponent> b2dCmpMapper;

    public PlayerCameraSystem(final OrthographicCamera gameCamera, final ComponentMapper<Box2DComponent> b2dCmpMapper) {
        super(Family.all(PlayerComponent.class, Box2DComponent.class).get());
        this.gameCamera = gameCamera;
        this.b2dCmpMapper = b2dCmpMapper;
    }

    @Override
    protected void processEntity(final Entity entity, final float deltaTime) {
        final Box2DComponent b2dCmp = b2dCmpMapper.get(entity);

        gameCamera.position.set(b2dCmp.positionBeforeUpdate.x, b2dCmp.positionBeforeUpdate.y, 0);
        gameCamera.update();
    }
}
