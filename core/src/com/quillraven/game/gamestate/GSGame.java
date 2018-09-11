package com.quillraven.game.gamestate;

import com.quillraven.game.core.EGameState;
import com.quillraven.game.core.Game;
import com.quillraven.game.core.GameState;
import com.quillraven.game.core.InputController;
import com.quillraven.game.ui.GameUI;

public class GSGame extends GameState<GameUI> {

    public GSGame(final Game game, final GameUI hud) {
        super(game, hud);
    }

    @Override
    public void processInput(final InputController inputController) {
        if (inputController.isKeyPressed(InputController.Key.LEFT)) {
            game.setGameState(EGameState.LOADING);
        }
    }
}
