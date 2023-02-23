package com.github.quillraven.mysticgarden.ui.actor

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Scaling
import com.github.quillraven.mysticgarden.ui.Drawable
import com.github.quillraven.mysticgarden.ui.GdxLabel
import com.github.quillraven.mysticgarden.ui.Label
import com.github.quillraven.mysticgarden.ui.get
import ktx.actors.plusAssign
import ktx.actors.txt
import ktx.scene2d.*

// HorizontalGroup would make sense but again, Scene2D randomness
// doesn't allow me to set the image size and instead draws it in original size
// -> use WidgetGroup
class CollectInfo(
    icon: Drawable,
    toCollect: Int,
) : KGroup, WidgetGroup() {

    private val img = Image(Scene2DSkin.defaultSkin[icon])
    private val label = GdxLabel(infoTxt(), Scene2DSkin.defaultSkin, Label.SMALL.skinKey)

    var collected = 0
        set(value) {
            field = value
            label.txt = infoTxt()
        }
    var toCollect = toCollect
        set(value) {
            field = value
            label.txt = infoTxt()
        }

    init {
        this += img.apply {
            this.setScaling(Scaling.fit)
            this.setSize(this@CollectInfo.prefWidth, this@CollectInfo.prefHeight)
        }
        this += label.apply {
            this.setPosition(this@CollectInfo.prefWidth + 3f, 4f)
        }
    }

    private fun infoTxt() = "$collected/$toCollect"

    override fun getPrefWidth(): Float = 15f

    override fun getPrefHeight(): Float = 15f
}

@Scene2dDsl
fun <S> KWidget<S>.collectInfo(
    icon: Drawable,
    toCollect: Int,
    init: CollectInfo.(S) -> Unit = {}
): CollectInfo = actor(CollectInfo(icon, toCollect), init)