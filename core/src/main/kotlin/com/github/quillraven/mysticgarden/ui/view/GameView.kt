package com.github.quillraven.mysticgarden.ui.view

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Scaling
import com.github.quillraven.mysticgarden.ui.Drawable
import com.github.quillraven.mysticgarden.ui.Label
import com.github.quillraven.mysticgarden.ui.get
import com.github.quillraven.mysticgarden.ui.model.GameModel
import ktx.actors.alpha
import ktx.actors.onChangeEvent
import ktx.scene2d.*

class GameView(model: GameModel) : KTable, Table(Scene2DSkin.defaultSkin) {

    init {
        touchpad(0f) {
            this.onChangeEvent { model.onTouchChange(knobPercentX, knobPercentY) }

            it.left().padLeft(5f).padBottom(5f).padRight(5f)
        }

        table {
            background = skin[Drawable.FRAME2]

            label("Zeit: 00:00") { lblCell ->
                lblCell.colspan(4).expandX().left().padBottom(3f).padLeft(2f).row()
            }

            image(skin[Drawable.CRYSTAL]) { imgCell ->
                this.setScaling(Scaling.fit)
                imgCell.height(15f).width(15f)
            }
            label("0/10", Label.SMALL.skinKey) { lblCell ->
                lblCell.left().padTop(1f)
            }

            image(skin[Drawable.ORB]) { imgCell ->
                this.setScaling(Scaling.fit)
                imgCell.height(15f).width(15f).padLeft(5f)
            }
            label("0/5", Label.SMALL.skinKey) { lblCell ->
                lblCell.expandX().left().padTop(1f).row()
            }

            table { slotTableCell ->
                image(skin[Drawable.SLOT]) { imgCell ->
                    this.setScaling(Scaling.fit)
                    imgCell.height(25f)
                }
                image(skin[Drawable.SLOT]) { imgCell ->
                    this.setScaling(Scaling.fit)
                    imgCell.height(25f)
                }
                image(skin[Drawable.SLOT]) { imgCell ->
                    this.setScaling(Scaling.fit)
                    imgCell.height(25f)
                }

                slotTableCell.colspan(4).padTop(3f)
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