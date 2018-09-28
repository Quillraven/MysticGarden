package com.quillraven.game.gamestate;

import com.badlogic.gdx.Gdx;
import com.quillraven.game.core.Utils;
import com.quillraven.game.core.gamestate.EGameState;
import com.quillraven.game.core.gamestate.GameState;
import com.quillraven.game.core.input.EKey;
import com.quillraven.game.core.input.InputManager;
import com.quillraven.game.core.ui.HUD;
import com.quillraven.game.core.ui.TTFSkin;
import com.quillraven.game.ui.MenuUI;

public class GSMenu extends GameState<MenuUI> {
    private int changeVolumne;

    public GSMenu(final EGameState type, final HUD hud) {
        super(type, hud);
        changeVolumne = 0;
    }

    @Override
    protected MenuUI createHUD(final HUD hud, final TTFSkin skin) {
        return new MenuUI(hud, skin);
    }

    @Override
    public void step(final float fixedTimeStep) {
        super.step(fixedTimeStep);
        if (changeVolumne > 0) {
            gameStateHUD.moveSelectionRight();
        } else if (changeVolumne < 0) {
            gameStateHUD.moveSelectionLeft();
        }
    }

    @Override
    public void dispose() {
        // nothing to dispose
    }

    @Override
    public void keyDown(final InputManager manager, final EKey key) {
        switch (key) {
            case SELECT:
                if (gameStateHUD.isNewGameSelected()) {
                    Utils.setGameState(EGameState.GAME);
                    return;
                } else if (gameStateHUD.isQuitGameSelected()) {
                    Gdx.app.exit();
                    return;
                }
                gameStateHUD.selectCurrentItem();
                break;
            case UP:
                gameStateHUD.moveSelectionUp();
                break;
            case DOWN:
                gameStateHUD.moveSelectionDown();
                break;
            case LEFT:
                changeVolumne = -1;
                break;
            case RIGHT:
                changeVolumne = 1;
                break;
            default:
                // nothing to do
                break;
        }
    }

    @Override
    public void keyUp(final InputManager manager, final EKey key) {
        if (key == EKey.LEFT) {
            changeVolumne = manager.isKeyDown(EKey.RIGHT) ? 1 : 0;
        } else if (key == EKey.RIGHT) {
            changeVolumne = manager.isKeyDown(EKey.LEFT) ? -1 : 0;
        }
    }
}
