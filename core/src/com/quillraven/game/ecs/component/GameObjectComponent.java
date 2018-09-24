package com.quillraven.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class GameObjectComponent implements Pool.Poolable, Component {
    public enum GameObjectType {
        CRYSTAL, TREE, FIRESTONE, AXE
    }

    public GameObjectType type;

    @Override
    public void reset() {
        type = null;
    }
}
