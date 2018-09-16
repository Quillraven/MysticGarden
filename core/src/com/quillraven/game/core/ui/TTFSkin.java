package com.quillraven.game.core.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Custom skin class to avoid the "Pixmap already disposed" exception when disposing the skin.
 * The reason is that the bitmap fonts that are created on runtime are not handled correctly within
 * the {@link com.badlogic.gdx.assets.AssetManager} and get therefore disposed twice.
 */
public class TTFSkin extends com.badlogic.gdx.scenes.scene2d.ui.Skin {
    TTFSkin(final TextureAtlas atlas) {
        super(atlas);

        Colors.put("Highlight", new Color(0xff0000ff));
        Colors.put("Disabled", new Color(0x22222222));
        Colors.put("Black", new Color(0x000000ff));
    }

    @Override
    public void dispose() {
        for (String bitmapFontKey : this.getAll(BitmapFont.class).keys()) {
            remove(bitmapFontKey, BitmapFont.class);
        }
        super.dispose();
    }
}
