package com.quillraven.game.core;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class InputController extends InputAdapter {
    private final Key[] keyMapping;
    private final boolean keyState[];

    InputController() {
        this.keyMapping = new Key[256];
        for (final Key key : Key.values()) {
            for (final int code : key.keyCode) {
                keyMapping[code] = key;
            }
        }
        this.keyState = new boolean[Key.values().length];
    }

    @Override
    public boolean keyDown(final int keycode) {
        final Key key = keyMapping[keycode];
        if (key == null) {
            // no relevant key for game
            return false;
        }

        keyState[key.ordinal()] = true;
        return true;
    }

    @Override
    public boolean keyUp(final int keycode) {
        final Key key = keyMapping[keycode];
        if (key == null) {
            // no relevant key for game
            return false;
        }

        keyState[key.ordinal()] = false;
        return true;
    }

    public boolean isKeyPressed(final Key key) {
        return keyState[key.ordinal()];
    }

    public boolean isAnyKeyPressed() {
        for (final boolean state : keyState) {
            if (state) {
                return true;
            }
        }
        return false;
    }

    public enum Key {
        RIGHT(Input.Keys.D, Input.Keys.RIGHT),
        LEFT(Input.Keys.A, Input.Keys.LEFT),
        UP(Input.Keys.W, Input.Keys.UP),
        DOWN(Input.Keys.S, Input.Keys.DOWN);

        private final int[] keyCode;

        Key(final int... keyCode) {
            this.keyCode = keyCode;
        }
    }
}
