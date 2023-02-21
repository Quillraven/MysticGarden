package com.github.quillraven.mysticgarden.ui.view

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.github.quillraven.mysticgarden.ui.Drawable
import com.github.quillraven.mysticgarden.ui.GdxLabel
import com.github.quillraven.mysticgarden.ui.Label
import com.github.quillraven.mysticgarden.ui.actor.CollectInfo
import com.github.quillraven.mysticgarden.ui.actor.collectInfo
import com.github.quillraven.mysticgarden.ui.get
import com.github.quillraven.mysticgarden.ui.model.GameModel
import ktx.actors.alpha
import ktx.actors.onChangeEvent
import ktx.scene2d.*

class GameView(model: GameModel, leftHand: Boolean) : KTable, Table(Scene2DSkin.defaultSkin) {

    private val timeLabel: GdxLabel

    private val crystalInfo: CollectInfo
    private val orbInfo: CollectInfo

    init {

        table { topTableCell ->
            this@GameView.crystalInfo = collectInfo(Drawable.CRYSTAL, 10) { cell ->
                cell.padLeft(5f)
            }

            this@GameView.timeLabel = label("Zeit: 00:00", Label.SMALL.skinKey) { cell ->
                cell.padLeft(40f).fill()
            }

            this@GameView.orbInfo = collectInfo(Drawable.ORB, 5) { cell ->
                cell.padLeft(10f)
            }

            this.alpha = 0.75f
            topTableCell.top().left().padTop(5f).row()
        }

        table { centerTableCell ->
            val itemSize = 12f
            this.defaults().top().height(itemSize).width(itemSize).padRight(4f)

            image(Scene2DSkin.defaultSkin[Drawable.AXE]) { this.setScaling(Scaling.fit) }
            image(Scene2DSkin.defaultSkin[Drawable.CLUB]) { this.setScaling(Scaling.fit) }
            image(Scene2DSkin.defaultSkin[Drawable.WAND]) { this.setScaling(Scaling.fit) }

            this.alpha = 0.5f
            centerTableCell.top().center().padLeft(10f).row()
        }

        table { bottomTableCell ->
            touchpad(0f) { cell ->
                this.onChangeEvent { model.onTouchChange(knobPercentX, knobPercentY) }

                cell.expand()
                    .align(if (leftHand) Align.left else Align.right)
                    .bottom()
                    .padLeft(5f).padBottom(5f)
            }

            this.alpha = 0.75f
            bottomTableCell.expand().fill().bottom()
        }

        setFillParent(true)
    }
}

@Scene2dDsl
fun <S> KWidget<S>.gameView(
    model: GameModel,
    leftHand: Boolean,
    init: GameView.(S) -> Unit = {}
): GameView = actor(GameView(model, leftHand), init)