package com.github.quillraven.mysticgarden.ui.view

import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.Scaling
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.ui.Drawable
import com.github.quillraven.mysticgarden.ui.I18N
import com.github.quillraven.mysticgarden.ui.Label
import com.github.quillraven.mysticgarden.ui.get
import ktx.actors.plusAssign
import ktx.i18n.get
import ktx.scene2d.*

class ControlsView(
    private val i18n: I18NBundle,
) : KTable, Table(Scene2DSkin.defaultSkin) {

    init {
        image(Scene2DSkin.defaultSkin[Drawable.BANNER]) { cell ->
            this.setScaling(Scaling.contain)
            cell.expandX().fillX().height(90f).row()
        }

        table { innerTableCell ->
            background = Scene2DSkin.defaultSkin[Drawable.FRAME3]

            image(Scene2DSkin.defaultSkin[Drawable.HERO]) {
                this.setScaling(Scaling.contain)
            }

            val heroCtrlText = if (MysticGarden.isMobile) I18N.CONTROLSHERO_MOBILE else I18N.CONTROLSHERO
            label(this@ControlsView.i18n[heroCtrlText], Label.NORMAL.skinKey) { cell ->
                this.setAlignment(Align.center, Align.center)
                this.wrap = true
                cell.expand().fill().pad(5f).row()
            }

            val otherCtrlText = if (MysticGarden.isMobile) I18N.CONTROLSTOUCH_MOBILE else I18N.CONTROLSTOUCH
            label(this@ControlsView.i18n[otherCtrlText], Label.NORMAL.skinKey) { cell ->
                this.setAlignment(Align.center, Align.center)
                this.wrap = true
                cell.expand().fill().colspan(2).padLeft(10f).row()
            }

            innerTableCell.expand().fill().pad(3f).row()
        }

        label(i18n[I18N.PRESSANYKEY], Label.SMALL.skinKey) { cell ->
            this.setAlignment(Align.center, Align.center)
            this += forever(sequence(alpha(0.3f, 1f), alpha(1f, 1f)))
            cell.expand().fill()
        }

        this.setFillParent(true)
    }
}


@Scene2dDsl
fun <S> KWidget<S>.controlsView(
    i18n: I18NBundle,
    init: ControlsView.(S) -> Unit = {}
): ControlsView = actor(ControlsView(i18n), init)