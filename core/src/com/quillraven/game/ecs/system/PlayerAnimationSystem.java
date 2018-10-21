package com.quillraven.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.quillraven.game.core.Utils;
import com.quillraven.game.core.ecs.component.AnimationComponent;
import com.quillraven.game.core.ecs.component.Box2DComponent;
import com.quillraven.game.ecs.ECSEngine;
import com.quillraven.game.ecs.component.PlayerComponent;

import static com.quillraven.game.MysticGarden.UNIT_SCALE;

public class PlayerAnimationSystem extends IteratingSystem {
    private final Animation<Sprite> aniLeft;
    private final Animation<Sprite> aniRight;
    private final Animation<Sprite> aniUp;
    private final Animation<Sprite> aniDown;

    public PlayerAnimationSystem() {
        super(Family.all(PlayerComponent.class, AnimationComponent.class).get());

        // create player animations
        final TextureAtlas.AtlasRegion atlasRegion = Utils.getResourceManager().get("characters_and_effects/character_and_effect.atlas", TextureAtlas.class).findRegion("hero");
        final TextureRegion[][] textureRegions = atlasRegion.split(64, 64);
        aniUp = new Animation<>(0.05f, getKeyFrames(textureRegions[0]), Animation.PlayMode.LOOP);
        aniLeft = new Animation<>(0.05f, getKeyFrames(textureRegions[1]), Animation.PlayMode.LOOP);
        aniDown = new Animation<>(0.05f, getKeyFrames(textureRegions[2]), Animation.PlayMode.LOOP);
        aniRight = new Animation<>(0.05f, getKeyFrames(textureRegions[3]), Animation.PlayMode.LOOP);
    }

    private Array<Sprite> getKeyFrames(final TextureRegion[] textureRegions) {
        final Array<Sprite> keyFrames = new Array<>();

        for (final TextureRegion region : textureRegions) {
            keyFrames.add(new Sprite(region));
        }

        return keyFrames;
    }

    @Override
    protected void processEntity(final Entity entity, final float deltaTime) {
        final PlayerComponent playerCmp = ECSEngine.playerCmpMapper.get(entity);
        final AnimationComponent aniCmp = ECSEngine.aniCmpMapper.get(entity);
        final Box2DComponent b2dCmp = ECSEngine.b2dCmpMapper.get(entity);

        if (aniCmp.animation == null) {
            aniCmp.animation = aniDown;
            aniCmp.width = 48 * UNIT_SCALE;
            aniCmp.height = 48 * UNIT_SCALE;
        }

        if (playerCmp.speed.equals(Vector2.Zero) || !b2dCmp.body.isActive()) {
            aniCmp.aniTimer = 0;
        } else if (playerCmp.speed.x > 0) {
            aniCmp.animation = aniRight;
        } else if (playerCmp.speed.x < 0) {
            aniCmp.animation = aniLeft;
        } else if (playerCmp.speed.y > 0) {
            aniCmp.animation = aniUp;
        } else if (playerCmp.speed.y < 0) {
            aniCmp.animation = aniDown;
        }
    }
}
