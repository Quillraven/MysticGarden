package com.quillraven.game.core.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool;

public class AnimationComponent implements Component, Pool.Poolable {
    public Animation<Sprite> animation;
    public float width;
    public float height;
    public float aniTimer;

    @Override
    public void reset() {
        animation = null;
        aniTimer = 0;
        width = 0;
        height = 0;
    }
}
