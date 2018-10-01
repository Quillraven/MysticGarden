package com.quillraven.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class GameObjectComponent implements Pool.Poolable, Component {
    public enum GameObjectType {
        NOT_DEFINED, CRYSTAL, TREE, AXE, CHROMA_ORB, FIRESTONE, WALL, PORTAL, WAND, CLUB, TUTORIAL_TREE
    }

    public GameObjectType type;
    public int id;

    @Override
    public void reset() {
        type = null;
        id = 0;
    }
}
