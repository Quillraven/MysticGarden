package com.quillraven.game.core.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.quillraven.game.core.input.EKey;

class OnScreenUI extends Table {
    private final Button btnUp;
    private final Button btnLeft;
    private final Button btnDown;
    private final Button btnRight;

    private final Button btnBack;
    private final Button btnSelect;

    OnScreenUI(final HUD hud, final TTFSkin skin) {
        super();

        final WidgetGroup gamePad = new WidgetGroup();
        btnUp = new Button(skin, "gamePadUp");
        btnUp.setPosition(50, 85);
        btnUp.addListener(hud);
        gamePad.addActor(btnUp);
        btnLeft = new Button(skin, "gamePadLeft");
        btnLeft.setPosition(0, 50);
        btnLeft.addListener(hud);
        gamePad.addActor(btnLeft);
        btnRight = new Button(skin, "gamePadRight");
        btnRight.setPosition(85, 50);
        btnRight.addListener(hud);
        gamePad.addActor(btnRight);
        btnDown = new Button(skin, "gamePadDown");
        btnDown.setPosition(50, 0);
        btnDown.addListener(hud);
        gamePad.addActor(btnDown);

        btnBack = new Button(skin, "back");
        btnBack.addListener(hud);

        btnSelect = new Button(skin, "select");
        btnSelect.addListener(hud);

        add(btnBack).expand().left().top().pad(5, 5, 0, 0).row();
        add(gamePad).left().bottom().pad(0, 5, 5, 0);
        add(btnSelect).expandX().bottom().right().pad(0, 0, 5, 5);
    }

    public EKey getRelatedKey(final Actor target) {
        if (btnUp.equals(target)) {
            return EKey.UP;
        } else if (btnDown.equals(target)) {
            return EKey.DOWN;
        } else if (btnLeft.equals(target)) {
            return EKey.LEFT;
        } else if (btnRight.equals(target)) {
            return EKey.RIGHT;
        } else if (btnBack.equals(target)) {
            return EKey.BACK;
        } else if (btnSelect.equals(target)) {
            return EKey.SELECT;
        } else {
            return null;
        }
    }

    public void setChecked(final EKey key, boolean check) {
        switch (key) {
            case UP:
                btnUp.setChecked(check);
                break;
            case DOWN:
                btnDown.setChecked(check);
                break;
            case RIGHT:
                btnRight.setChecked(check);
                break;
            case LEFT:
                btnLeft.setChecked(check);
                break;
            case BACK:
                btnBack.setChecked(check);
                break;
            case SELECT:
                btnSelect.setChecked(check);
                break;
        }
    }
}
