package com.quillraven.game.core.input;

import com.badlogic.gdx.Input;

public enum EKey {
    RIGHT(Input.Keys.D, Input.Keys.RIGHT),
    LEFT(Input.Keys.A, Input.Keys.LEFT),
    UP(Input.Keys.W, Input.Keys.UP),
    DOWN(Input.Keys.S, Input.Keys.DOWN),
    SELECT(Input.Keys.ENTER, Input.Keys.SPACE),
    BACK(Input.Keys.ESCAPE);

    final int[] keyCode;

    EKey(final int... keyCode) {
        this.keyCode = keyCode;
    }
}
