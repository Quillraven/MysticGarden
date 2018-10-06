package com.quillraven.game.gamestate;

import com.badlogic.gdx.Gdx;
import com.quillraven.game.SaveState;
import com.quillraven.game.core.AudioManager;
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
        return new MenuUI(hud, skin, Utils.getAudioManager().getVolume());
    }

    @Override
    public void activate() {
        super.activate();
        gameStateHUD.activateContinueItem(Utils.getPreferenceManager().containsKey(SaveState.SAVE_STATE_PREFERENCE_KEY));
        Utils.getAudioManager().playAudio(AudioManager.AudioType.INTRO);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        Utils.getPreferenceManager().setFloatValue("volume", gameStateHUD.getVolume());
    }

    @Override
    public void step(final float fixedTimeStep) {
        super.step(fixedTimeStep);
        if (changeVolumne != 0) {
            if (changeVolumne > 0) {
                gameStateHUD.moveSelectionRight();
            } else {
                gameStateHUD.moveSelectionLeft();
            }
            Utils.getAudioManager().setVolume(gameStateHUD.getVolume());
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
                Utils.getAudioManager().playAudio(AudioManager.AudioType.SELECT);
                if (gameStateHUD.isNewGameSelected()) {
                    Utils.getPreferenceManager().removeKey(SaveState.SAVE_STATE_PREFERENCE_KEY);
                    Utils.setGameState(EGameState.GAME);
                    return;
                } else if (gameStateHUD.isContinueSelected()) {
                    Utils.setGameState(EGameState.GAME);
                    return;
                } else if (gameStateHUD.isQuitGameSelected()) {
                    Gdx.app.exit();
                    return;
                }
                gameStateHUD.selectCurrentItem();
                break;
            case UP:
                Utils.getAudioManager().playAudio(AudioManager.AudioType.SELECT);
                gameStateHUD.moveSelectionUp();
                break;
            case DOWN:
                Utils.getAudioManager().playAudio(AudioManager.AudioType.SELECT);
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
