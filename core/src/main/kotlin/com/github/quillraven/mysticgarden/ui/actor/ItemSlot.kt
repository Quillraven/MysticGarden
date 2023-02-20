package com.github.quillraven.mysticgarden.ui.actor

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Scaling
import com.github.quillraven.mysticgarden.ui.Drawable
import com.github.quillraven.mysticgarden.ui.get
import ktx.actors.plusAssign
import ktx.scene2d.*

class ItemSlot : KGroup, WidgetGroup() {

    private val background = Image(Scene2DSkin.defaultSkin[Drawable.SLOT])
    private val item = Image()

    init {
        this += background
        this += item.apply {
            val pad = 8f
            setPosition(pad, pad)
            setSize(this@ItemSlot.prefWidth - 2 * pad, this@ItemSlot.prefHeight - 2 * pad)
            setScaling(Scaling.fill)
        }
    }

    override fun getPrefWidth(): Float = background.drawable.minWidth

    override fun getPrefHeight(): Float = background.drawable.minHeight

    fun item(drawable: Drawable) {
        item.drawable = Scene2DSkin.defaultSkin[drawable]
    }
}

@Scene2dDsl
fun <S> KWidget<S>.itemSlot(
    init: ItemSlot.(S) -> Unit = {}
): ItemSlot = actor(ItemSlot(), init)