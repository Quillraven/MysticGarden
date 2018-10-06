package com.quillraven.game.core.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

public class InputManager implements InputProcessor {
    private final EKey[] keyMapping;
    private final boolean[] keyState;
    private final Array<KeyInputListener> listeners;

    public InputManager() {
        this.keyMapping = new EKey[256];
        for (final EKey key : EKey.values()) {
            for (final int code : key.keyCode) {
                keyMapping[code] = key;
            }
        }
        this.keyState = new boolean[EKey.values().length];
        listeners = new Array<>();
    }

    public void addKeyInputListener(final KeyInputListener listener) {
        listeners.add(listener);
    }

    public void removeKeyInputListener(final KeyInputListener listener) {
        listeners.removeValue(listener, true);
    }

    public void notifyKeyDown(final EKey key) {
        keyState[key.ordinal()] = true;
        for (final KeyInputListener listener : listeners) {
            listener.keyDown(this, key);
        }
    }

    public void notifyKeyUp(final EKey key) {
        keyState[key.ordinal()] = false;
        for (final KeyInputListener listener : listeners) {
            listener.keyUp(this, key);
        }
    }

    public boolean isKeyDown(final EKey key) {
        return keyState[key.ordinal()];
    }

    @Override
    public boolean keyDown(final int keycode) {
        final EKey key = keyMapping[keycode];
        if (key == null) {
            // no relevant key for game
            return false;
        }

        notifyKeyDown(key);
        return true;
    }

    @Override
    public boolean keyUp(final int keycode) {
        final EKey key = keyMapping[keycode];
        if (key == null) {
            // no relevant key for game
            return false;
        }

        notifyKeyUp(key);
        return true;
    }

    @Override
    public boolean keyTyped(final char character) {
        return false;
    }

    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(final int amount) {
        return false;
    }
}
