package com.quillraven.game.ecs.system;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.quillraven.game.core.InputController;
import com.quillraven.game.core.ecs.IteratingInputSystem;
import com.quillraven.game.core.ecs.component.Box2DComponent;
import com.quillraven.game.ecs.component.PlayerComponent;

public class PlayerMovementSystem extends IteratingInputSystem {
    private final ComponentMapper<PlayerComponent> playerCmpMapper;
    private final ComponentMapper<Box2DComponent> b2dCmpMapper;

    public PlayerMovementSystem(final ComponentMapper<PlayerComponent> playerCmpMapper, final ComponentMapper<Box2DComponent> b2dCmpMapper) {
        super(Family.all(Box2DComponent.class, PlayerComponent.class).get());
        this.playerCmpMapper = playerCmpMapper;
        this.b2dCmpMapper = b2dCmpMapper;
    }

    @Override
    public void processInput(final InputController inputController) {
        final float xFactor;
        final float yFactor;
        if (inputController.isKeyPressed(InputController.Key.LEFT)) {
            xFactor = -1;
        } else if (inputController.isKeyPressed(InputController.Key.RIGHT)) {
            xFactor = 1;
        } else {
            xFactor = 0;
        }

        if (inputController.isKeyPressed(InputController.Key.UP)) {
            yFactor = 1;
        } else if (inputController.isKeyPressed(InputController.Key.DOWN)) {
            yFactor = -1;
        } else {
            yFactor = 0;
        }

        for (final Entity entity : getEntities()) {
            final PlayerComponent playerCmp = playerCmpMapper.get(entity);
            playerCmp.speed.x = xFactor * playerCmp.maxSpeed;
            playerCmp.speed.y = yFactor * playerCmp.maxSpeed;
        }
    }

    @Override
    protected void processEntity(final Entity entity, final float deltaTime) {
        final Box2DComponent b2dCmp = b2dCmpMapper.get(entity);
        final PlayerComponent playerCmp = playerCmpMapper.get(entity);
        final Vector2 worldCenter = b2dCmp.body.getWorldCenter();
        b2dCmp.body.applyLinearImpulse((playerCmp.speed.x - b2dCmp.body.getLinearVelocity().x) * b2dCmp.body.getMass(), (playerCmp.speed.y - b2dCmp.body.getLinearVelocity().y) * b2dCmp.body.getMass(), worldCenter.x, worldCenter.y, true);
        b2dCmp.positionBeforeUpdate.set(b2dCmp.body.getPosition());
    }
}
