package com.quillraven.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.StringBuilder;
import com.quillraven.game.core.ui.HUD;
import com.quillraven.game.core.ui.TTFSkin;

public class VictoryUI extends Table {
    private final TextButton gameTimeTxt;

    public VictoryUI(final HUD hud, final TTFSkin skin) {
        super();

        add(new Image(skin.getDrawable("banner"))).expand().top().padTop(75).row();
        final TextButton gameTimeInfo = new TextButton(hud.getLocalizedString("neededGameTime") + ":", skin, "huge");
        gameTimeInfo.getLabel().setWrap(true);
        add(gameTimeInfo).expand().fillX().top().padTop(65).row();
        gameTimeTxt = new TextButton("[Highlight]00:00:00", skin, "huge");
        add(gameTimeTxt).expand().fillX().top().padBottom(175);
    }

    public void setGameTime(final int hours, final int minutes, final int seconds) {
        final StringBuilder timeLabel = gameTimeTxt.getLabel().getText();
        timeLabel.setLength(0);
        timeLabel.append("[Highlight]");
        if (hours < 10) {
            timeLabel.append('0');
        }
        timeLabel.append(hours);
        timeLabel.append(':');
        if (minutes < 10) {
            timeLabel.append('0');
        }
        timeLabel.append(minutes);
        timeLabel.append(':');
        if (seconds < 10) {
            timeLabel.append('0');
        }
        timeLabel.append(seconds);
        gameTimeTxt.getLabel().invalidateHierarchy();
    }
}
