package com.github.quillraven.mysticgarden.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.I18NBundle
import com.github.quillraven.mysticgarden.MusicAsset
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.audio.AudioService
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.GameTimeEvent
import com.github.quillraven.mysticgarden.ui.model.VictoryModel
import com.github.quillraven.mysticgarden.ui.view.victoryView
import ktx.app.KtxScreen
import ktx.scene2d.actors

class VictoryScreen(
    private val game: MysticGarden,
    private val uiStage: Stage,
    private val i18n: I18NBundle,
    private val audioService: AudioService,
    eventDispatcher: EventDispatcher
) : KtxScreen {

    private var neededTime = 0

    init {
        eventDispatcher.register<GameTimeEvent> {
            neededTime = it.totalTimeSeconds
        }
    }

    override fun show() {
        audioService.play(MusicAsset.VICTORY)

        uiStage.clear()
        uiStage.actors {
            val model = VictoryModel()
            victoryView(i18n, model)
            // change model after view creation to trigger
            // view property model bindings
            model.totalTime = neededTime
        }

        Gdx.input.inputProcessor = uiStage
    }

    override fun resize(width: Int, height: Int) {
        uiStage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        val dt = delta.coerceAtMost(0.25f)

        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
            game.setScreen<MenuScreen>()
        }

        // render UI
        uiStage.viewport.apply()
        uiStage.act(dt)
        uiStage.draw()
    }
}