package com.github.quillraven.mysticgarden.ui.view

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.github.quillraven.mysticgarden.ui.Drawable
import com.github.quillraven.mysticgarden.ui.GdxLabel
import com.github.quillraven.mysticgarden.ui.actor.CollectInfo
import com.github.quillraven.mysticgarden.ui.actor.ItemSlot
import com.github.quillraven.mysticgarden.ui.actor.collectInfo
import com.github.quillraven.mysticgarden.ui.actor.itemSlot
import com.github.quillraven.mysticgarden.ui.get
import com.github.quillraven.mysticgarden.ui.model.GameModel
import ktx.actors.alpha
import ktx.actors.onChangeEvent
import ktx.scene2d.*

class GameView(model: GameModel) : KTable, Table(Scene2DSkin.defaultSkin) {

    private val timeLabel: GdxLabel

    private val crystalInfo: CollectInfo
    private val orbInfo: CollectInfo

    private val axeSlot: ItemSlot
    private val clubSlot: ItemSlot
    private val wandSlot: ItemSlot

    init {
        touchpad(0f) {
            this.onChangeEvent { model.onTouchChange(knobPercentX, knobPercentY) }

            it.left().padLeft(5f).padBottom(5f).padRight(5f)
        }

        table {
            background = skin[Drawable.FRAME2]
            this.defaults().expandX().left().padLeft(2f)

            this@GameView.timeLabel = label("Zeit: 00:00") { lblCell ->
                lblCell.padTop(3f).row()
            }

            table { collTblCell ->
                this@GameView.crystalInfo = collectInfo(Drawable.CRYSTAL, 10) { collectCell ->
                    collectCell.width(50f)
                }
                this@GameView.orbInfo = collectInfo(Drawable.ORB, 5)
                collTblCell.pad(2f, 1f, 5f, 0f).row()
            }

            table { slotTableCell ->
                this.defaults().padRight(3f).padBottom(2f)

                this@GameView.axeSlot = itemSlot { slotCell -> slotCell.height(25f) }
                this@GameView.clubSlot = itemSlot { slotCell -> slotCell.height(25f) }
                this@GameView.wandSlot = itemSlot { slotCell -> slotCell.height(25f) }

                slotTableCell.padTop(3f)
            }

            alpha = 0.75f
            it.expandX().fill().padRight(5f).padBottom(5f).top()
        }

        setFillParent(true)
        bottom()
    }
}

@Scene2dDsl
fun <S> KWidget<S>.gameView(
    model: GameModel,
    init: GameView.(S) -> Unit = {}
): GameView = actor(GameView(model), init)