package com.github.quillraven.mysticgarden.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Colors
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.I18NBundle
import ktx.assets.toInternalFile
import ktx.collections.set
import ktx.scene2d.Scene2DSkin
import ktx.style.label
import ktx.style.set
import ktx.style.skin
import ktx.style.touchpad

typealias GdxDrawable = com.badlogic.gdx.scenes.scene2d.utils.Drawable
typealias GdxLabel = com.badlogic.gdx.scenes.scene2d.ui.Label

enum class Bundle(val filePath: String) {
    DEFAULT("i18n/strings");

    val skinKey: String = this.name.lowercase()
}

operator fun Skin.get(bundle: Bundle): I18NBundle = this.get(bundle.skinKey, I18NBundle::class.java)

enum class Font(val scale: Float) {
    SMALL(0.33f),
    NORMAL(0.50f);

    val skinKey: String = "Font_${this.name.lowercase()}"
}

operator fun Skin.get(font: Font): BitmapFont = this.getFont(font.skinKey)

enum class Drawable {
    ARROW,
    AXE,
    BAR_EMPTY,
    BAR_FULL,
    CLUB,
    CRYSTAL,
    FRAME2,
    FRAME3,
    ORB,
    SLOT,
    TOUCH_KNOB,
    TOUCH_PAD,
    WAND,
    BOOTS;

    val atlasKey: String = this.name.lowercase()
}

enum class Label {
    SMALL,
    FRAMED;

    val skinKey: String = "Label_${this.name.lowercase()}"
}

operator fun Skin.get(drawable: Drawable): GdxDrawable = this.getDrawable(drawable.atlasKey)

fun loadSkin(): Skin {
    return skin(TextureAtlas("graphics/ui.atlas")) { skin ->

        // I18N stuff
        Colors.getColors()["Highlight"] = Color(0xb19cd9)
        Colors.getColors()["Normal"] = Color.WHITE
        Bundle.values().forEach { bundle ->
            skin.add(bundle.skinKey, I18NBundle.createBundle(bundle.filePath.toInternalFile()))
        }

        // Scene2D related stuff
        Font.values().forEach { fnt ->
            skin[fnt.skinKey] = BitmapFont("ui/font.fnt".toInternalFile(), skin.getRegion("font")).also { bmp ->
                bmp.data.markupEnabled = true
                bmp.data.setScale(fnt.scale)
            }
        }

        label { font = skin[Font.NORMAL] }
        label(Label.SMALL.skinKey) { font = skin[Font.SMALL] }
        label(Label.FRAMED.skinKey) {
            background = skin[Drawable.FRAME2].apply {
                leftWidth = 1f
                rightWidth = 1f
                topHeight = 1f
                bottomHeight = 1f
            }
            font = skin[Font.SMALL]
        }

        touchpad {
            background = skin[Drawable.TOUCH_PAD]
            knob = skin[Drawable.TOUCH_KNOB]
        }

    }.also {
        Scene2DSkin.defaultSkin = it
    }
}