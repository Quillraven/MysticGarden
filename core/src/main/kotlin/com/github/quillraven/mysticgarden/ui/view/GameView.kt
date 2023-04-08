package com.github.quillraven.mysticgarden.ui.view

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.Scaling
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.component.ItemType
import com.github.quillraven.mysticgarden.ui.*
import com.github.quillraven.mysticgarden.ui.actor.CollectInfo
import com.github.quillraven.mysticgarden.ui.actor.collectInfo
import com.github.quillraven.mysticgarden.ui.model.GameModel
import ktx.actors.*
import ktx.app.gdxError
import ktx.i18n.get
import ktx.scene2d.*

class GameView(
    private val i18n: I18NBundle,
    model: GameModel,
) : KTable, Table(Scene2DSkin.defaultSkin) {

    private val timeLabel: GdxLabel
    private val infoLabel: GdxLabel

    private val crystalInfo: CollectInfo
    private val orbInfo: CollectInfo

    init {
        table { topTableCell ->
            this@GameView.crystalInfo = collectInfo(Drawable.CRYSTAL, 0) { cell ->
                cell.padLeft(5f)
            }

            this@GameView.timeLabel = label(this@GameView.i18n[I18N.TIME, "00:00"], Label.SMALL.skinKey) { cell ->
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
                cell.bottom().expand().fillX().pad(0f, 10f, 0f, 10f)
            }

            midTableCell.expand().fill().padBottom(10f).row()
        }

        if (MysticGarden.isMobile) {
            // add touchpad only for mobile devices
            table { bottomTableCell ->
                touchpad(0f) { cell ->
                    this.onChangeEvent { model.onTouchChange(knobPercentX, knobPercentY) }

                    cell.expand()
                        .align(Align.left)
                        .bottom()
                        .pad(0f, 5f, 5f, 5f)
                }

                imageButton(ImageButton.BACK.skinKey) { cell ->
                    this.onClick { model.goToMenu() }
                    cell.right().bottom().width(25f).height(25f).padRight(5f).padBottom(5f)
                }

                this.alpha = 0.75f
                bottomTableCell.fill().bottom()
            }
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
                    timeLabel.txt = i18n[I18N.TIME, "99:59"]
                } else {
                    val minutes = totalTimeSeconds / 60
                    val seconds = totalTimeSeconds % 60
                    timeLabel.txt = i18n[I18N.TIME, "%02d:%02d".format(minutes, seconds)]
                }
            }

            onPropertyChange(GameModel::infoMsg) {
                infoLabel.txt = it
                if (it.isBlank()) {
                    infoLabel.clearActions()
                    infoLabel.alpha = 0f
                } else {
                    infoLabel += alpha(1f) + fadeOut(6f, Interpolation.swingIn)
                }
            }
            onPropertyChange(GameModel::item) { showItem(itemDrawable(it)) }

            onPropertyChange(GameModel::allItems) { items ->
                stage.root.clearActions()
                stage.actors.filterIsInstance<Image>()
                    .forEach(stage.root::removeActor)

                items.forEach { showItem(itemDrawable(it)) }
            }
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
        stage += delay(5f, Actions.run {
            val numItems = stage.actors.size - 1
            val offsetTop = 30f
            val offsetLeft = 62f
            val imgSize = 11f
            val padLeft = 5f

            stage += Image(Scene2DSkin.defaultSkin[drawable]).also { img ->
                img.setScaling(Scaling.fit)
                img.centerPosition(stage.width, stage.height)
                // for the love of god I cannot find out how to get the correct
                // infoLabel position from Scene2D to position the image above the infoLabel.
                // Sounds like an easy task, but I guess you need dark magic and a master in rocket science
                // to achieve that. I always get the position (10, -1) when clearly it is something different.
                // So f%$& you again Scene2D -> I am hardcoding it.
                val baseY = if (MysticGarden.isMobile) 80f else 10f
                img.y = baseY + infoLabel.height + 10f
                img += alpha(0.1f) +
                        fadeIn(2f, Interpolation.bounceOut) +
                        parallel(
                            moveTo(offsetLeft + (imgSize + padLeft) * numItems, stage.height - offsetTop, 1f),
                            sizeTo(imgSize, imgSize, 1f)
                        )
            }
        })
    }
}

@Scene2dDsl
fun <S> KWidget<S>.gameView(
    i18n: I18NBundle,
    model: GameModel,
    init: GameView.(S) -> Unit = {}
): GameView = actor(GameView(i18n, model), init)