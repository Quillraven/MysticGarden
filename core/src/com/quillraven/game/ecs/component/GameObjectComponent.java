package com.quillraven.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class GameObjectComponent implements Pool.Poolable, Component {
    public enum GameObjectType {
        NOT_DEFINED, CRYSTAL, TREE, AXE, CHROMA_ORB
    }

    public GameObjectType type;
    public int tiledMapID;

    @Override
    public void reset() {
        type = null;
        tiledMapID = 0;
    }
}
