package com.quillraven.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.quillraven.game.core.ui.HUD;
import com.quillraven.game.core.ui.TTFSkin;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class LoadingUI extends Table {
    private final TextButton pressAnyButtonInfo;
    private final ProgressBar progressBar;

    public LoadingUI(final HUD hud, final TTFSkin skin) {
        super();

        progressBar = new ProgressBar(0, 1, 0.01f, false, skin, "default");

        pressAnyButtonInfo = new TextButton(hud.getLocalizedString("pressAnyKey"), skin, "huge");
        pressAnyButtonInfo.setVisible(false);
        pressAnyButtonInfo.getLabel().setWrap(true);

        add(pressAnyButtonInfo).expandX().fillX().bottom().padBottom(100).row();
        add(new TextButton(hud.getLocalizedString("loading") + "...", skin, "normal")).expandX().fillX().padBottom(15).bottom().row();
        add(progressBar).expandX().fillX().pad(0, 50, 225, 50).bottom();
        bottom();
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
