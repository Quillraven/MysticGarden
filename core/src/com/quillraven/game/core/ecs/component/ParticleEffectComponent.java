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
        NOT_DEFINED("", ""),
        TORCH("characters_and_effects/torch.p", "characters_and_effects/character_and_effect.atlas"),
        CRYSTAL("characters_and_effects/crystal.p", "characters_and_effects/character_and_effect.atlas");

        private final String effectFilePath;
        private final String atlasFilePath;

        ParticleEffectType(final String effectFilePath, final String atlasFilePath) {
            this.effectFilePath = effectFilePath;
            this.atlasFilePath = atlasFilePath;
        }

        public String getEffectFilePath() {
            return effectFilePath;
        }

        public String getAtlasFilePath() {
            return atlasFilePath;
        }
    }
}
