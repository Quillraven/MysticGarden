package com.quillraven.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.quillraven.game.core.ui.HUD;
import com.quillraven.game.core.ui.TTFSkin;

public class GameUI extends Table {
    private final TTFSkin skin;

    private final TextButton timeText;
    private final TextButton crystalText;

    private final Array<Stack> slots;

    public GameUI(final HUD hud, final TTFSkin skin) {
        super();
        this.skin = skin;

        final Table contentTable = new Table();
        contentTable.setBackground(skin.getDrawable("frame_brown"));

        final TextButton timeLabel = new TextButton(hud.getLocalizedString("time") + ":", skin, "big");
        timeLabel.getLabel().setAlignment(Align.right);
        timeText = new TextButton("00:00:00", skin, "big");
        timeText.getLabel().setAlignment(Align.left);

        final Table crystalInfoTable = new Table();
        final Image crystalImg = new Image(skin.getDrawable("crystal"));
        crystalInfoTable.add(crystalImg).expand().fill().right();
        crystalInfoTable.add(new TextButton(":", skin, "big")).expand().fill().left();
        crystalText = new TextButton("0", skin, "big");
        crystalText.getLabel().setAlignment(Align.left);

        slots = new Array<>();
        final Table slotTable = new Table();
        for (int i = 0; i < 4; ++i) {
            final Stack slot = new Stack();
            slot.add(new Image(skin.getDrawable("frame_blue")));
            slots.add(slot);
            slotTable.add(slot);
        }

        contentTable.add(timeLabel).expand().fill().right().padLeft(15).padTop(15);
        contentTable.add(timeText).expand().fill().left().padTop(15).row();

        contentTable.add(crystalInfoTable).expand().right().padLeft(15).padTop(10);
        contentTable.add(crystalText).expand().fill().left().padTop(10).row();

        contentTable.add(slotTable).colspan(2).expand().fill().center().padBottom(10).padTop(10).row();

        add(contentTable).expandX().fill().bottom();
        bottom();
    }

    public void setGameTime(final int hours, final int minutes, final int seconds) {
        timeText.getLabel().getText().setLength(0);
        if (hours < 10) {
            timeText.getLabel().getText().append('0');
        }
        timeText.getLabel().getText().append(hours);
        timeText.getLabel().getText().append(':');
        if (minutes < 10) {
            timeText.getLabel().getText().append('0');
        }
        timeText.getLabel().getText().append(minutes);
        timeText.getLabel().getText().append(':');
        if (seconds < 10) {
            timeText.getLabel().getText().append('0');
        }
        timeText.getLabel().getText().append(seconds);
        timeText.getLabel().invalidateHierarchy();
    }

    public void setCrystals(final int crystalsFound) {
        crystalText.getLabel().getText().setLength(0);
        crystalText.getLabel().getText().append(crystalsFound);
        crystalText.getLabel().invalidateHierarchy();
    }

    public void setAxe(final boolean hasAxe) {
        if (slots.get(0).getChildren().size > 1) {
            slots.get(0).getChildren().get(1).setVisible(hasAxe);
        } else if (hasAxe) {
            slots.get(0).add(new Image(skin.getDrawable("axe")));
        }
    }
}
