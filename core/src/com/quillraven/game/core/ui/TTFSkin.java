package com.quillraven.game.core.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Custom skin class to avoid the "Pixmap already disposed" exception when disposing the skin.
 * The reason is that the bitmap fonts that are created on runtime are not handled correctly within
 * the {@link com.badlogic.gdx.assets.AssetManager} and get therefore disposed twice.
 */
public class TTFSkin extends com.badlogic.gdx.scenes.scene2d.ui.Skin {
    TTFSkin(final TextureAtlas atlas) {
        super(atlas);

        Colors.put("Highlight", new Color(0xff7f50ff));
        Colors.put("Deactivated", new Color(0x88888877));
        Colors.put("Normal", new Color(0xffffffff));
        Colors.put("Black", new Color(0x000000ff));
    }

    @Override
    public void load(final FileHandle skinFile) {
        super.load(skinFile);
        // enable markup for all fonts to use the Colors of the constructor when creating texts
        final ObjectMap<String, BitmapFont> allFonts = this.getAll(BitmapFont.class);
        if (allFonts != null) {
            for (final BitmapFont font : allFonts.values()) {
                font.getData().markupEnabled = true;
            }
        }

    }

    @Override
    public void dispose() {
        for (String bitmapFontKey : this.getAll(BitmapFont.class).keys()) {
            remove(bitmapFontKey, BitmapFont.class);
        }
        super.dispose();
    }
}
