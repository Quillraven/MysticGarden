package com.github.quillraven.mysticgarden.ui.actor

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Scaling
import com.github.quillraven.mysticgarden.ui.Drawable
import com.github.quillraven.mysticgarden.ui.get
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.collections.GdxArray
import ktx.scene2d.*

class VolumeControl(
    private val onVolumeChange: (Float) -> Unit,
) : KGroup, WidgetGroup() {

    private var value = 1f
    private val images = GdxArray<Image>()

    init {
        repeat(numElements) {
            this += Image(Scene2DSkin.defaultSkin[Drawable.BAR_FULL]).apply {
                this.x = it * totalElementWidth()
                this.setSize(elementWidth, elementHeight)
                this.setScaling(Scaling.fill)
                images.add(this)

                this.onClick {
                    var volumeLevel = (this@VolumeControl.children.indexOf(this).toFloat() + 1) / numElements
                    if (volumeLevel <= 0.1) {
                        volumeLevel = 0f
                    }
                    setVolume(volumeLevel)
                    onVolumeChange(volumeLevel)
                }
            }
        }
    }

    fun setVolume(volume: Float) {
        val elementsToFill = (volume.coerceIn(0f, 1f) * numElements).toInt()
        repeat(elementsToFill) {
            images.get(it).drawable = Scene2DSkin.defaultSkin[Drawable.BAR_FULL]
        }
        repeat(numElements - elementsToFill) {
            images.get(elementsToFill + it).drawable = Scene2DSkin.defaultSkin[Drawable.BAR_EMPTY]
        }
    }

    private fun totalElementWidth(): Float = elementWidth + 2 * padding

    override fun getPrefWidth(): Float = totalElementWidth() * numElements

    override fun getPrefHeight(): Float = elementHeight

    companion object {
        private const val numElements = 15
        private const val padding = 1f
        private const val elementWidth = 3f * 1.5f
        private const val elementHeight = 9f * 1.5f
    }
}

@Scene2dDsl
fun <S> KWidget<S>.volumeControl(
    onVolumeChange: (Float) -> Unit,
): VolumeControl = actor(VolumeControl(onVolumeChange))