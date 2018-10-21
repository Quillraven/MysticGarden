package com.quillraven.game.core.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.quillraven.game.core.Utils;
import com.quillraven.game.core.ecs.EntityEngine;
import com.quillraven.game.core.ecs.component.ParticleEffectComponent;

import java.util.EnumMap;

public class ParticleSystem extends IteratingSystem {
    private final EnumMap<ParticleEffectComponent.ParticleEffectType, ParticleEffectPool> effectPools;

    public ParticleSystem() {
        super(Family.all(ParticleEffectComponent.class).get());
        effectPools = new EnumMap<>(ParticleEffectComponent.ParticleEffectType.class);
    }

    @Override
    protected void processEntity(final Entity entity, final float deltaTime) {
        final ParticleEffectComponent peCmp = EntityEngine.peCmpMapper.get(entity);

        if (peCmp.effect != null) {
            // effect available -> update it
            peCmp.effect.update(deltaTime);
            if (peCmp.effect.isComplete()) {
                entity.remove(ParticleEffectComponent.class);
            }
        } else if (peCmp.type != ParticleEffectComponent.ParticleEffectType.NOT_DEFINED) {
            // type defined but effect not spawned yet -> spawn it
            ParticleEffectPool effectPool = effectPools.get(peCmp.type);
            if (effectPool == null) {
                final ParticleEffect effect = Utils.getResourceManager().get(peCmp.type.getEffectFilePath(), ParticleEffect.class);
                // set blend function cleanup to false to increase render performance because otherwise every effect
                // rendering will cause a spriteBatch flush.
                // this means that we need to take care of setting back the blend function by hand in the GameRenderSystem
                effect.setEmittersCleanUpBlendFunction(false);
                effectPool = new ParticleEffectPool(effect, 1, 128);
                effectPools.put(peCmp.type, effectPool);
            }
            peCmp.effect = effectPool.obtain();
            peCmp.effect.setPosition(peCmp.position.x, peCmp.position.y);
            peCmp.effect.scaleEffect(peCmp.scaling);
        }
    }
}
