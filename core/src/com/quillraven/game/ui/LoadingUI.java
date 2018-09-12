package com.quillraven.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.quillraven.game.core.Game;
import com.quillraven.game.core.ui.HUD;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class LoadingUI extends HUD {
    private final TextButton pressAnyButtonInfo;
    private final ProgressBar progressBar;

    public LoadingUI(final Game game) {
        super(game);

        progressBar = new ProgressBar(0, 1, 0.01f, false, game.getSkin(), "default");

        pressAnyButtonInfo = new TextButton(getLocalizedString("pressAnyKey"), game.getSkin(), "big");
        pressAnyButtonInfo.setVisible(false);
        pressAnyButtonInfo.getLabel().setWrap(true);

        table.add(pressAnyButtonInfo).expand().fill().center().row();
        table.add(new TextButton(getLocalizedString("loading") + "...", game.getSkin(), "normal")).expandX().fillX().padBottom(15).bottom().row();
        table.add(progressBar).expandX().fillX().pad(0, 50, 25, 50).bottom();
        table.bottom();
    }

    public void setProgress(final float progress) {
        progressBar.setValue(progress);
        if (progress >= 1 && !pressAnyButtonInfo.isVisible()) {
            pressAnyButtonInfo.setVisible(true);
            pressAnyButtonInfo.setColor(1, 1, 1, 0);
            pressAnyButtonInfo.addAction(forever(sequence(alpha(1, 1), alpha(0, 1))));
        }
    }
}
