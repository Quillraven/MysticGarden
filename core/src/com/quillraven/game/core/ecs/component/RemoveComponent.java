package com.quillraven.game.core.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class RemoveComponent implements Pool.Poolable, Component {
    @Override
    public void reset() {
        // this component is only used to remove entities at the end of an update loop
        // it does not need any special data
    }
}
