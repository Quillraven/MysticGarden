package com.github.quillraven.mysticgarden.ui.view

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.Scaling
import com.github.quillraven.mysticgarden.ui.*
import com.github.quillraven.mysticgarden.ui.actor.VolumeControl
import com.github.quillraven.mysticgarden.ui.actor.volumeControl
import com.github.quillraven.mysticgarden.ui.model.MenuModel
import ktx.actors.alpha
import ktx.actors.onClick
import ktx.i18n.get
import ktx.scene2d.*

class MenuView(
    private val model: MenuModel,
    private val i18n: I18NBundle,
) : KTable, Table(Scene2DSkin.defaultSkin) {

    private val continueLabel: GdxLabel
    private val volumeControl: VolumeControl

    init {
        background = skin[Drawable.MENU_BGD]

        image(Scene2DSkin.defaultSkin[Drawable.BANNER]) { cell ->
            this.setScaling(Scaling.contain)
            cell.expandX().fillX().height(90f).row()
        }

        table { innerTableCell ->
            this.defaults().expand().fill()
            this.background = (skin[Drawable.FRAME3] as NinePatchDrawable).tint(Color(1f, 1f, 1f, 0.8f))

            label(this@MenuView.i18n[I18N.NEWGAME], Label.NORMAL.skinKey) { cell ->
                this.setAlignment(Align.center)
                cell.row()

                this.onClick { this@MenuView.model.startNewGame() }
            }

            this@MenuView.continueLabel = label(this@MenuView.i18n[I18N.CONTINUE], Label.NORMAL.skinKey) { cell ->
                this.alpha = 0.3f
                this.setAlignment(Align.center)
                this.touchable = Touchable.disabled
                cell.row()

                this.onClick { this@MenuView.model.continueGame() }
            }

            table {
                label(this@MenuView.i18n[I18N.VOLUME], Label.NORMAL.skinKey) { cell ->
                    this.setAlignment(Align.center)
                    cell.row()
                }
                this@MenuView.volumeControl = volumeControl { volumeValue -> this@MenuView.model.volume(volumeValue) }

                it.row()
            }

            label(this@MenuView.i18n[I18N.QUITGAME], Label.NORMAL.skinKey) { cell ->
                this.setAlignment(Align.center)
                cell.row()

                this.onClick { this@MenuView.model.quitGame() }
            }

            innerTableCell.expand().fill().pad(10f)
        }

        this.setFillParent(true)

        // data bindings
        model.onPropertyChange(MenuModel::hasSaveState) { hasSaveState ->
            if (hasSaveState) {
                continueLabel.alpha = 1f
                continueLabel.touchable = Touchable.enabled
            } else {
                continueLabel.alpha = 0.3f
                continueLabel.touchable = Touchable.disabled
            }
        }

        model.onPropertyChange(MenuModel::volume) { volumeLevel ->
            volumeControl.setVolume(volumeLevel)
        }
    }
}


@Scene2dDsl
fun <S> KWidget<S>.menuView(
    model: MenuModel,
    i18n: I18NBundle,
    init: MenuView.(S) -> Unit = {}
): MenuView = actor(MenuView(model, i18n), init)