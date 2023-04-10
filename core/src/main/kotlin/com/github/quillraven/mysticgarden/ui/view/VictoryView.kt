package com.github.quillraven.mysticgarden.ui.view

import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.Scaling
import com.github.quillraven.mysticgarden.ui.Drawable
import com.github.quillraven.mysticgarden.ui.I18N
import com.github.quillraven.mysticgarden.ui.Label
import com.github.quillraven.mysticgarden.ui.get
import com.github.quillraven.mysticgarden.ui.model.VictoryModel
import ktx.actors.plusAssign
import ktx.actors.txt
import ktx.i18n.get
import ktx.scene2d.*

class VictoryView(
    private val i18n: I18NBundle,
    model: VictoryModel
) : KTable, Table(Scene2DSkin.defaultSkin) {

    init {
        image(Scene2DSkin.defaultSkin[Drawable.BANNER]) { cell ->
            this.setScaling(Scaling.contain)
            cell.expandX().fillX().row()
        }
        val timeLabel = label("", Label.NORMAL.skinKey) { cell ->
            this.setAlignment(Align.top, Align.center)
            cell.expand().fill().row()
        }
        label(i18n[I18N.PRESSANYKEY], Label.SMALL.skinKey) { cell ->
            this.wrap = true
            this.setAlignment(Align.center, Align.center)
            this += forever(sequence(alpha(0.3f, 1f), alpha(1f, 1f)))
            cell.expand().fill()
        }

        this.setFillParent(true)

        model.onPropertyChange(VictoryModel::totalTime) { totalTimeSeconds ->
            if (totalTimeSeconds >= 6000) {
                // time is more than 99 minutes and 59 seconds
                // a player should never take that long, but you never know ;)
                timeLabel.txt = i18n[I18N.NEEDEDTIME, "99:59"]
            } else {
                val minutes = totalTimeSeconds / 60
                val seconds = totalTimeSeconds % 60
                timeLabel.txt = i18n[I18N.NEEDEDTIME, "%02d:%02d".format(minutes, seconds)]
            }
        }
    }
}


@Scene2dDsl
fun <S> KWidget<S>.victoryView(
    i18n: I18NBundle,
    model: VictoryModel,
    init: VictoryView.(S) -> Unit = {}
): VictoryView = actor(VictoryView(i18n, model), init)