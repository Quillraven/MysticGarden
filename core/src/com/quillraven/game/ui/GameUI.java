package com.quillraven.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.quillraven.game.core.Game;
import com.quillraven.game.core.ui.HUD;

public class GameUI extends HUD {
    public GameUI(final Game game) {
        super(game);

        TextButton testBtn = new TextButton("TEST", game.getSkin(), "big");
        table.add(testBtn).expandX().fillX().center();
        table.bottom();
    }
}
