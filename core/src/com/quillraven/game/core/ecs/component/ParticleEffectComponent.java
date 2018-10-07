package com.quillraven.game.core.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class ParticleEffectComponent implements Pool.Poolable, Component {
    public ParticleEffectPool.PooledEffect effect;
    public ParticleEffectType type;
    public final Vector2 position = new Vector2();
    public float scaling;

    @Override
    public void reset() {
        type = ParticleEffectType.NOT_DEFINED;
        if (effect != null) {
            effect.free();
            effect = null;
        }
        position.set(0, 0);
        scaling = 1;
    }

    public enum ParticleEffectType {
        NOT_DEFINED(""),
        TORCH("characters_and_effects/torch.p"),
        CRYSTAL("characters_and_effects/crystal.p"),
        PORTAL("characters_and_effects/portal.p");

        private final String effectFilePath;

        ParticleEffectType(final String effectFilePath) {
            this.effectFilePath = effectFilePath;
        }

        public String getEffectFilePath() {
            return effectFilePath;
        }
    }
}
