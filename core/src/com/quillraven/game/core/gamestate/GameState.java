package com.quillraven.game.core.gamestate;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.quillraven.game.core.Utils;
import com.quillraven.game.core.input.KeyInputListener;
import com.quillraven.game.core.ui.HUD;
import com.quillraven.game.core.ui.TTFSkin;

public abstract class GameState<T extends Table> implements Disposable, KeyInputListener {
    private final EGameState type;
    protected final HUD hud;
    protected final T gameStateHUD;

    protected GameState(final EGameState type, final HUD hud) {
        this.type = type;
        this.hud = hud;
        gameStateHUD = createHUD(hud, hud.getSkin());
        hud.addGameStateHUD(gameStateHUD);
        gameStateHUD.setVisible(false);
    }

    protected abstract T createHUD(final HUD hud, final TTFSkin skin);

    public EGameState getType() {
        return type;
    }

    public void activate() {
        Utils.getInputManager().addKeyInputListener(this);
        gameStateHUD.setVisible(true);
    }

    public void deactivate() {
        Utils.getInputManager().removeKeyInputListener(this);
        gameStateHUD.setVisible(false);
    }

    public void step(final float fixedTimeStep) {
        hud.step(fixedTimeStep);
    }

    public void render(final float alpha) {
        hud.render(alpha);
    }

    public void resize(final int width, final int height) {
        hud.resize(width, height);
    }
}
