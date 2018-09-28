package com.quillraven.game.core.gamestate;

import com.quillraven.game.gamestate.GSGame;
import com.quillraven.game.gamestate.GSLoading;
import com.quillraven.game.gamestate.GSMenu;

public enum EGameState {
    GAME(GSGame.class),
    LOADING(GSLoading.class),
    MENU(GSMenu.class);

    private final Class<? extends GameState> gsClass;

    EGameState(final Class<? extends GameState> gsClass) {
        this.gsClass = gsClass;
    }

    public Class<? extends GameState> getGameStateType() {
        return gsClass;
    }
}
