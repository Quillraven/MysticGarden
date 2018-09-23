package com.quillraven.game.core.gamestate;

import com.quillraven.game.gamestate.GSGame;
import com.quillraven.game.gamestate.GSLoading;

public enum EGameState {
    GAME(GSGame.class),
    LOADING(GSLoading.class);

    private final Class<? extends GameState> gsClass;

    EGameState(final Class<? extends GameState> gsClass) {
        this.gsClass = gsClass;
    }

    public Class<? extends GameState> getGameStateType() {
        return gsClass;
    }
}
