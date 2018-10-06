package com.quillraven.game.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.quillraven.game.core.ui.HUD;
import com.quillraven.game.core.ui.TTFSkin;

public class MenuUI extends Table {
    private static final String HIGHLIGHT_TEXT_DEACTIVATE = "[Deactivated]";
    private final TTFSkin skin;

    private final Table mainPage;
    private final Table creditsPage;

    private final TextButton continueItem;
    private final Slider volumeSlider;
    private final Array<TextButton> menuItems;
    private int currentItemIdx;
    private int volumeIdx;
    private int creditsIdx;

    public MenuUI(final HUD hud, final TTFSkin skin, final float initialVolumeValue) {
        super();
        this.skin = skin;

        menuItems = new Array<>();
        final Stack menuPages = new Stack();
        volumeSlider = new Slider(0, 1, 0.01f, false, skin, "default");
        volumeSlider.setValue(initialVolumeValue);
        if (volumeSlider.getValue() == 0) {
            volumeSlider.setStyle(skin.get("deactivated", Slider.SliderStyle.class));
        }
        continueItem = new TextButton(HIGHLIGHT_TEXT_DEACTIVATE + hud.getLocalizedString("continue"), skin, "huge");
        mainPage = createMainPage(hud, skin);
        menuPages.add(mainPage);
        currentItemIdx = 0;
        volumeIdx = 1;
        creditsIdx = 2;
        highlightCurrentItem(true);

        creditsPage = createCreditsPage(hud, skin);
        menuPages.add(creditsPage);
        creditsPage.setVisible(false);

        add(menuPages).expand().fill();
    }

    private Table createMainPage(final HUD hud, final TTFSkin skin) {
        final Table content = new Table();
        content.setBackground(skin.getDrawable("menu_background"));
        content.add(new Image(skin.getDrawable("banner"))).expand().top().padTop(65).row();

        menuItems.add(new TextButton(hud.getLocalizedString("newGame"), skin, "huge"));
        content.add(menuItems.peek()).fill().expand().row();
        content.add(continueItem).fill().expand().row();

        final Table soundTable = new Table();
        menuItems.add(new TextButton(hud.getLocalizedString("volume"), skin, "huge"));
        soundTable.add(menuItems.peek()).fillX().expandX().row();
        soundTable.add(volumeSlider).expandX().width(250);
        content.add(soundTable).fill().expand().row();

        menuItems.add(new TextButton(hud.getLocalizedString("creditsMenuItem"), skin, "huge"));
        content.add(menuItems.peek()).fill().expand().row();
        menuItems.add(new TextButton(hud.getLocalizedString("quitGame"), skin, "huge"));
        content.add(menuItems.peek()).fill().expand().padBottom(175).row();

        return content;
    }


    private Table createCreditsPage(final HUD hud, final TTFSkin skin) {
        final Table content = new Table();
        content.setBackground(skin.getDrawable("menu_background"));
        content.add(new Image(skin.getDrawable("banner"))).expandX().top().padTop(65).row();

        final TextButton creditsTxt = new TextButton(hud.getLocalizedString("credits"), skin, "normal");
        creditsTxt.getLabel().setWrap(true);
        content.add(new TextButton(hud.getLocalizedString("creditsMenuItem") + ":", skin, "huge")).expandX().top().padTop(75).row();
        content.add(creditsTxt).expand().fill().top().pad(10, 25, 175, 25);

        return content;
    }

    private void highlightCurrentItem(final boolean highlight) {
        final Label label = menuItems.get(currentItemIdx).getLabel();
        if (highlight) {
            label.getText().insert(0, "[Highlight]");
        } else {
            label.getText().replace("[Highlight]", "");
        }
        label.invalidateHierarchy();
    }

    public void selectCurrentItem() {
        if (currentItemIdx == creditsIdx && !creditsPage.isVisible()) {
            mainPage.setVisible(false);
            creditsPage.setVisible(true);
        } else if (creditsPage.isVisible()) {
            creditsPage.setVisible(false);
            mainPage.setVisible(true);
        }
    }

    public void moveSelectionUp() {
        if (!mainPage.isVisible()) {
            return;
        }

        highlightCurrentItem(false);
        --currentItemIdx;
        if (currentItemIdx < 0) {
            currentItemIdx = menuItems.size - 1;
        }
        highlightCurrentItem(true);
    }

    public void moveSelectionDown() {
        if (!mainPage.isVisible()) {
            return;
        }

        highlightCurrentItem(false);
        ++currentItemIdx;
        if (currentItemIdx >= menuItems.size) {
            currentItemIdx = 0;
        }
        highlightCurrentItem(true);
    }

    public void moveSelectionLeft() {
        if (currentItemIdx == volumeIdx) {
            volumeSlider.setValue(volumeSlider.getValue() - volumeSlider.getStepSize());
            if (volumeSlider.getValue() == 0) {
                volumeSlider.setStyle(skin.get("deactivated", Slider.SliderStyle.class));
            }
        }
    }

    public void moveSelectionRight() {
        if (currentItemIdx == volumeIdx) {
            volumeSlider.setValue(volumeSlider.getValue() + volumeSlider.getStepSize());
            if (MathUtils.isEqual(volumeSlider.getValue(), 0.01f)) {
                volumeSlider.setStyle(skin.get("default", Slider.SliderStyle.class));
            }
        }
    }

    public void activateContinueItem(final boolean activate) {
        highlightCurrentItem(false);
        final boolean alreadyAvailable = menuItems.contains(continueItem, true);
        final Label label = continueItem.getLabel();

        if (activate) {
            // add option to menu items
            if (!alreadyAvailable) {
                menuItems.insert(1, continueItem);
                label.getText().replace(HIGHLIGHT_TEXT_DEACTIVATE, "");
                ++volumeIdx;
                ++creditsIdx;
            }
            if (currentItemIdx == 0 || currentItemIdx > 1) {
                // if selection is new game then switch it to continue
                // if selection is after continue then fix index position because continue is now an additional option before
                ++currentItemIdx;
            }
        } else {
            // remove option from menu items
            if (alreadyAvailable) {
                menuItems.removeValue(continueItem, true);
                label.getText().insert(0, HIGHLIGHT_TEXT_DEACTIVATE);
                --volumeIdx;
                --creditsIdx;
            }
            if (currentItemIdx >= 1) {
                // if selection is after or on continue then fix index position because continue is no longer an option
                --currentItemIdx;
            }
        }
        label.invalidateHierarchy();
        highlightCurrentItem(true);
    }

    public boolean isNewGameSelected() {
        return currentItemIdx == 0;
    }

    public boolean isContinueSelected() {
        return currentItemIdx == 1;
    }

    public boolean isQuitGameSelected() {
        return currentItemIdx == menuItems.size - 1;
    }

    public float getVolume() {
        return volumeSlider.getValue();
    }
}
