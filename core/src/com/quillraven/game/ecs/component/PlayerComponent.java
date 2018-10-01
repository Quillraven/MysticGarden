package com.quillraven.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class PlayerComponent implements Pool.Poolable, Component {
    public final Vector2 speed = new Vector2();
    public float maxSpeed;
    public int crystals;
    public boolean hasAxe;
    public boolean hasClub;
    public boolean hasWand;
    public float sleepTime;
    public int chromaOrbs;

    @Override
    public void reset() {
        speed.set(0, 0);
        maxSpeed = 0;
        crystals = 0;
        hasAxe = false;
        sleepTime = 0;
        chromaOrbs = 0;
    }
}
