package com.github.quillraven.mysticgarden.ui.view

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.component.ItemType
import com.github.quillraven.mysticgarden.ui.Drawable
import com.github.quillraven.mysticgarden.ui.GdxLabel
import com.github.quillraven.mysticgarden.ui.Label
import com.github.quillraven.mysticgarden.ui.actor.CollectInfo
import com.github.quillraven.mysticgarden.ui.actor.collectInfo
import com.github.quillraven.mysticgarden.ui.get
import com.github.quillraven.mysticgarden.ui.model.GameModel
import ktx.actors.*
import ktx.app.gdxError
import ktx.scene2d.*

class GameView(model: GameModel, leftHand: Boolean) : KTable, Table(Scene2DSkin.defaultSkin) {

    private val timeLabel: GdxLabel
    private val infoLabel: GdxLabel

    private val crystalInfo: CollectInfo
    private val orbInfo: CollectInfo

    init {
        table { topTableCell ->
            this@GameView.crystalInfo = collectInfo(Drawable.CRYSTAL, 0) { cell ->
                cell.padLeft(5f)
            }

            this@GameView.timeLabel = label("Zeit: 00:00", Label.SMALL.skinKey) { cell ->
                cell.padLeft(40f).fill()
            }

            this@GameView.orbInfo = collectInfo(Drawable.ORB, 0) { cell ->
                cell.padLeft(10f)
            }

            this.alpha = 0.75f
            topTableCell.top().left().padTop(5f).row()
        }

        table { midTableCell ->
            this@GameView.infoLabel = label("", Label.FRAMED.skinKey) { cell ->
                this.wrap = true
                this.alpha = 0f
                cell.bottom().expand().fillX().pad(0f, 10f, 40f, 10f)
            }

            midTableCell.expand().fill().row()
        }

        table { bottomTableCell ->
            val tp = touchpad(0f) { cell ->
                this.onChangeEvent { model.onTouchChange(knobPercentX, knobPercentY) }

                cell.expand()
                    .align(if (leftHand) Align.left else Align.right)
                    .bottom()
                    .pad(0f, 5f, 5f, 5f)
            }

            if (MysticGarden.isMobile) {
                this.alpha = 0.75f
            } else {
                // for non-mobile we keep the touchpad widget for a
                // consistent infoLabel position, but we will hide it and deactivate it
                this.alpha = 0f
                tp.touchable = Touchable.disabled
            }
            bottomTableCell.fill().bottom()
        }

        setFillParent(true)
        initPropertyBinding(model)
    }

    private fun initPropertyBinding(model: GameModel) {
        with(model) {
            onPropertyChange(GameModel::maxCrystals) { crystalInfo.toCollect = it }
            onPropertyChange(GameModel::collectedCrystals) { crystalInfo.collected = it }

            onPropertyChange(GameModel::maxOrbs) { orbInfo.toCollect = it }
            onPropertyChange(GameModel::collectedOrbs) { orbInfo.collected = it }

            onPropertyChange(GameModel::totalTime) { totalTimeSeconds ->
                if (totalTimeSeconds >= 6000) {
                    // time is more than 99 minutes and 59 seconds
                    // a player should never take that long, but you never know ;)
                    timeLabel.txt = "Zeit: 99:59"
                } else {
                    val minutes = totalTimeSeconds / 60
                    val seconds = totalTimeSeconds % 60
                    timeLabel.txt = "Zeit: %02d:%02d".format(minutes, seconds)
                }
            }

            onPropertyChange(GameModel::item) { showItem(itemDrawable(item)) }
        }
    }

    private fun itemDrawable(itemType: ItemType): Drawable = when (itemType) {
        ItemType.AXE -> Drawable.AXE
        ItemType.CLUB -> Drawable.CLUB
        ItemType.WAND -> Drawable.WAND
        ItemType.BOOTS -> Drawable.BOOTS
        else -> gdxError("Unsupported type $itemType")
    }

    private fun showItem(drawable: Drawable) {
        infoLabel.txt = "You found a $drawable. AMAZING STUFF!!!"
        infoLabel += alpha(1f) + parallel(
            fadeOut(4f, Interpolation.pow3OutInverse),
            delay(3f, Actions.run { addItemImage(drawable) })
        )
    }

    private fun addItemImage(drawable: Drawable) {
        val numItems = stage.actors.size - 1
        val offsetTop = 30f
        val offsetLeft = 62f
        val imgSize = 11f
        val padLeft = 5f

        stage += Image(Scene2DSkin.defaultSkin[drawable]).also { img ->
            img.setScaling(Scaling.fit)
            img.centerPosition(stage.width, stage.height)
            img += alpha(0.1f) +
                    fadeIn(2f, Interpolation.bounceOut) +
                    parallel(
                        moveTo(offsetLeft + (imgSize + padLeft) * numItems, stage.height - offsetTop, 1f),
                        sizeTo(imgSize, imgSize, 1f)
                    )
        }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.gameView(
    model: GameModel,
    leftHand: Boolean,
    init: GameView.(S) -> Unit = {}
): GameView = actor(GameView(model, leftHand), init)