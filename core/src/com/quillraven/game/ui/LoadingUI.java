package com.quillraven.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
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

        add(new Image(skin.getDrawable("banner"))).expand().top().padTop(65).row();
        add(pressAnyButtonInfo).expand().fillX().center().row();
        add(new TextButton(hud.getLocalizedString("loading") + "...", skin, "normal")).expand().fillX().bottom().row();
        add(progressBar).expandX().fillX().pad(15, 50, 175, 50).bottom();
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
