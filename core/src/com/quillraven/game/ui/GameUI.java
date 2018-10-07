package com.quillraven.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.quillraven.game.core.ui.HUD;
import com.quillraven.game.core.ui.TTFSkin;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class GameUI extends Table {
    private static final String TXT_STYLE_INFO = "info";
    private final TTFSkin skin;

    private final TextButton timeText;
    private final TextButton crystalText;
    private final TextButton infoBox;

    private final Array<Stack> slots;

    public GameUI(final HUD hud, final TTFSkin skin) {
        super();
        this.skin = skin;

        final Table contentTable = new Table();
        contentTable.setBackground(skin.getDrawable("frame_brown"));

        final TextButton timeLabel = new TextButton(hud.getLocalizedString("time") + ":", skin, TXT_STYLE_INFO);
        timeLabel.getLabel().setAlignment(Align.right);
        timeText = new TextButton("00:00:00", skin, TXT_STYLE_INFO);
        timeText.getLabel().setAlignment(Align.left);

        final Table crystalInfoTable = new Table();
        final Image crystalImg = new Image(skin.getDrawable("crystal"));
        crystalInfoTable.add(crystalImg).expand().fill().right();
        crystalInfoTable.add(new TextButton(":", skin, TXT_STYLE_INFO)).expand().fill().left();
        crystalText = new TextButton("0", skin, TXT_STYLE_INFO);
        crystalText.getLabel().setAlignment(Align.left);

        slots = new Array<>();
        final Table slotTable = new Table();
        for (int i = 0; i < 4; ++i) {
            final Stack slot = new Stack();
            slot.add(new Image(skin.getDrawable("frame_green")));
            slots.add(slot);
            if (i == 1) {
                slotTable.add(slot).row();
            } else {
                slotTable.add(slot);
            }
        }

        contentTable.add(timeLabel).expand().fill().right().padLeft(140).padTop(15);
        contentTable.add(timeText).expand().fill().left().padTop(15).row();

        contentTable.add(crystalInfoTable).expand().right().padTop(5);
        contentTable.add(crystalText).expand().fill().left().padTop(5).row();

        contentTable.add(slotTable).colspan(2).padLeft(70).padBottom(5);

        infoBox = new TextButton("", skin, "info_frame");
        infoBox.getLabel().setWrap(true);
        infoBox.setVisible(false);

        add(infoBox).expand().fillX().top().pad(15, 65, 45, 15).row();
        add(contentTable).expandX().fill().bottom();
        bottom();
    }

    public void setGameTime(final int hours, final int minutes, final int seconds) {
        final StringBuilder timeLbl = timeText.getLabel().getText();
        timeLbl.setLength(0);
        if (hours < 10) {
            timeLbl.append('0');
        }
        timeLbl.append(hours);
        timeLbl.append(':');
        if (minutes < 10) {
            timeLbl.append('0');
        }
        timeLbl.append(minutes);
        timeLbl.append(':');
        if (seconds < 10) {
            timeLbl.append('0');
        }
        timeLbl.append(seconds);
        timeText.getLabel().invalidateHierarchy();
    }

    public void setCrystals(final int crystalsFound) {
        final Label crystalLbl = crystalText.getLabel();
        crystalLbl.getText().setLength(0);
        crystalLbl.getText().append(crystalsFound);
        crystalLbl.invalidateHierarchy();
    }

    public void setAxe(final boolean hasAxe) {
        if (slots.get(0).getChildren().size > 1) {
            slots.get(0).getChildren().get(1).setVisible(hasAxe);
        } else if (hasAxe) {
            slots.get(0).add(new Image(skin.getDrawable("axe")));
        }
    }


    public void setClub(final boolean hasClub) {
        if (slots.get(1).getChildren().size > 1) {
            slots.get(1).getChildren().get(1).setVisible(hasClub);
        } else if (hasClub) {
            slots.get(1).add(new Image(skin.getDrawable("club")));
        }
    }

    public void setWand(final boolean hasWand) {
        if (slots.get(2).getChildren().size > 1) {
            slots.get(2).getChildren().get(1).setVisible(hasWand);
        } else if (hasWand) {
            slots.get(2).add(new Image(skin.getDrawable("wand")));
        }
    }

    public void showInfoMessage(final String message, final float displayTime) {
        infoBox.setText(message);
        infoBox.setVisible(true);
        infoBox.setColor(1, 1, 1, 0);
        infoBox.clearActions();
        infoBox.addAction(sequence(alpha(1, 1), delay(displayTime), alpha(0, 1)));
    }

    public void setChromaOrb(final int chromaOrbsFound) {
        if (slots.get(3).getChildren().size == 1) {
            slots.get(3).add(new Image(skin.getDrawable("chromaorb")));
            final TextButton txt = new TextButton("", skin, "big");
            txt.getLabel().setAlignment(Align.bottomRight);
            txt.getLabelCell().padRight(10).padBottom(10);
            slots.get(3).add(txt);
        }

        final Label chromaOrbLbl = ((TextButton) slots.get(3).getChildren().get(2)).getLabel();
        chromaOrbLbl.getText().setLength(0);
        chromaOrbLbl.getText().append("[Black]");
        chromaOrbLbl.getText().append(chromaOrbsFound);
        chromaOrbLbl.invalidateHierarchy();

        slots.get(3).getChildren().get(1).setVisible(chromaOrbsFound > 0);
        slots.get(3).getChildren().get(2).setVisible(chromaOrbsFound > 0);
    }
}
