package com.quillraven.game.core;

import com.quillraven.game.core.GameState;
import com.quillraven.game.gamestate.GSGame;
import com.quillraven.game.gamestate.GSLoading;
import com.quillraven.game.ui.GameUI;
import com.quillraven.game.core.ui.HUD;
import com.quillraven.game.ui.LoadingUI;

public enum EGameState {
    GAME(GSGame.class, GameUI.class),
    LOADING(GSLoading.class, LoadingUI.class);

    private final Class<? extends GameState> gsClass;
    private final Class<? extends HUD> hudClass;

    EGameState(final Class<? extends GameState> gsClass, final Class<? extends HUD> hudClass) {
        this.gsClass = gsClass;
        this.hudClass = hudClass;
    }

    public Class<? extends GameState> getGameStateType() {
        return gsClass;
    }

    public Class<? extends HUD> getHUDType() {
        return hudClass;
    }
}
