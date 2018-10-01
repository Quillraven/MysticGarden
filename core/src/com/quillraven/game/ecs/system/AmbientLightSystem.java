package com.quillraven.game.ecs.system;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.quillraven.game.ecs.component.GameObjectComponent;

public class AmbientLightSystem extends EntitySystem implements PlayerContactSystem.PlayerContactListener {
    private final Color ambientLightColor;
    private final RayHandler rayHandler;

    public AmbientLightSystem(final RayHandler rayHandler) {
        this.rayHandler = rayHandler;
        this.ambientLightColor = new Color(0, 0, 0, 0.05f);
        rayHandler.setAmbientLight(ambientLightColor);
    }

    @Override
    public void addedToEngine(final Engine engine) {
        super.addedToEngine(engine);
        engine.getSystem(PlayerContactSystem.class).addPlayerContactListener(this);
    }

    @Override
    public void crystalContact(final int crystalsFound) {
        // nothing to do
    }

    @Override
    public void itemContact(final GameObjectComponent.GameObjectType type) {
        // nothing to do
    }

    @Override
    public void chromaOrbContact(final int chromaOrbsFound) {
        ambientLightColor.a = 0.05f + chromaOrbsFound * 0.05f;
        rayHandler.setAmbientLight(ambientLightColor);
    }

    @Override
    public void portalContact(final boolean hasAllCrystals) {
        // nothing to do
    }
}
