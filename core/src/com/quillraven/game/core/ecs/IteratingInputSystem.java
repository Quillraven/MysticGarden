package com.quillraven.game.core.ecs;

import com.badlogic.ashley.core.Family;
import com.quillraven.game.core.InputController;

public abstract class IteratingInputSystem extends com.badlogic.ashley.systems.IteratingSystem {
    protected IteratingInputSystem(final Family family) {
        super(family);
    }

    public abstract void processInput(final InputController inputController);
}
