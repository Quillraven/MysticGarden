package com.quillraven.game.gamestate;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.quillraven.game.SaveState;
import com.quillraven.game.core.AudioManager;
import com.quillraven.game.core.Utils;
import com.quillraven.game.core.gamestate.EGameState;
import com.quillraven.game.core.gamestate.GameState;
import com.quillraven.game.core.input.EKey;
import com.quillraven.game.core.input.InputManager;
import com.quillraven.game.core.ui.HUD;
import com.quillraven.game.core.ui.TTFSkin;
import com.quillraven.game.ui.VictoryUI;

public class GSVictory extends GameState<VictoryUI> {
    private final JsonReader jsonReader;

    public GSVictory(final EGameState type, final HUD hud) {
        super(type, hud);
        jsonReader = new JsonReader();
    }

    @Override
    protected VictoryUI createHUD(final HUD hud, final TTFSkin skin) {
        return new VictoryUI(hud, skin);
    }

    @Override
    public void activate() {
        super.activate();

        final JsonValue saveStateStr = jsonReader.parse(Utils.getPreferenceManager().getStringValue(SaveState.SAVE_STATE_PREFERENCE_KEY));
        gameStateHUD.setGameTime(saveStateStr.getInt(SaveState.SAVE_STATE_HOURS_KEY), saveStateStr.getInt(SaveState.SAVE_STATE_MINUTES_KEY), saveStateStr.getInt(SaveState.SAVE_STATE_SECONDS_KEY));
        Utils.getAudioManager().playAudio(AudioManager.AudioType.VICTORY);
    }

    @Override
    public void dispose() {
        // nothing to dispose
    }

    @Override
    public void keyDown(final InputManager manager, final EKey key) {
        if (key == EKey.SELECT || key == EKey.BACK) {
            Utils.setGameState(EGameState.MENU);
        }
    }

    @Override
    public void keyUp(final InputManager manager, final EKey key) {
        // nothing to do
    }
}
