package com.quillraven.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.quillraven.game.core.Utils;
import com.quillraven.game.core.ecs.component.Box2DComponent;
import com.quillraven.game.ecs.ECSEngine;
import com.quillraven.game.ecs.component.PlayerComponent;
import com.quillraven.game.map.Map;
import com.quillraven.game.map.MapManager;

import static com.quillraven.game.ecs.system.GameRenderSystem.RENDER_OFFSET_Y;

public class PlayerCameraSystem extends IteratingSystem implements MapManager.MapListener {
    private final OrthographicCamera gameCamera;
    private Array<Rectangle> camBoundaries;
    private Rectangle currentBoundary;
    private float camTime;
    private final float camLerpTime;
    private final Vector3 lerpTarget;

    public PlayerCameraSystem(final OrthographicCamera gameCamera) {
        super(Family.all(PlayerComponent.class, Box2DComponent.class).get());
        this.gameCamera = gameCamera;
        this.camLerpTime = 1.75f;
        this.camTime = this.camLerpTime;
        this.lerpTarget = new Vector3();
        Utils.getMapManager().addMapListener(this);
    }

    private void findCurrentBoundary(final Vector2 playerPosition) {
        for (final Rectangle rect : camBoundaries) {
            if (rect.contains(playerPosition)) {
                currentBoundary = rect;
                return;
            }
        }
    }

    @Override
    protected void processEntity(final Entity entity, final float deltaTime) {
        final Box2DComponent b2dCmp = ECSEngine.b2dCmpMapper.get(entity);

        if (currentBoundary == null) {
            // no boundary yet -> search one
            findCurrentBoundary(b2dCmp.positionBeforeUpdate);
        }

        if (currentBoundary != null) {
            final float camW = gameCamera.viewportWidth * 0.5f;
            final float camH = gameCamera.viewportHeight * 0.5f;
            if (camTime < camLerpTime) {
                // interpolate to new boundary
                camTime += deltaTime;
                gameCamera.position.interpolate(lerpTarget, Math.min(1f, camTime / camLerpTime), Interpolation.exp10In);
                if (camTime >= camLerpTime) {
                    // interpolation finished -> let body move again!
                    b2dCmp.body.setActive(true);
                }
            } else if (currentBoundary.contains(b2dCmp.positionBeforeUpdate)) {
                // update location in current boundary
                gameCamera.position.x = MathUtils.clamp(b2dCmp.positionBeforeUpdate.x, currentBoundary.x + camW, currentBoundary.x + currentBoundary.width - camW);
                gameCamera.position.y = MathUtils.clamp(b2dCmp.positionBeforeUpdate.y, currentBoundary.y + camH, currentBoundary.y + currentBoundary.height - camH + RENDER_OFFSET_Y);
            } else {
                // outside boundary -> find new boundary location and set interpolation target
                findCurrentBoundary(b2dCmp.positionBeforeUpdate);
                if (currentBoundary != null) {
                    lerpTarget.x = MathUtils.clamp(b2dCmp.positionBeforeUpdate.x, currentBoundary.x + camW, currentBoundary.x + currentBoundary.width - camW);
                    lerpTarget.y = MathUtils.clamp(b2dCmp.positionBeforeUpdate.y, currentBoundary.y + camH, currentBoundary.y + currentBoundary.height - camH + RENDER_OFFSET_Y);
                    camTime = 0f;
                    // stop body until camera interpolation is done
                    b2dCmp.body.setActive(false);
                }
            }
        }
    }

    @Override
    public void mapChanged(final Map map) {
        camBoundaries = map.getCamBoundaries();
    }
}
