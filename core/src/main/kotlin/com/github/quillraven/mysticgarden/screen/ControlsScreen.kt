package com.github.quillraven.mysticgarden.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.I18NBundle
import com.github.quillraven.mysticgarden.MusicAsset
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.audio.AudioService
import com.github.quillraven.mysticgarden.ui.view.controlsView
import ktx.app.KtxScreen
import ktx.scene2d.actors

class ControlsScreen(
    private val game: MysticGarden,
    private val audioService: AudioService,
    private val uiStage: Stage,
    private val i18n: I18NBundle
) : KtxScreen {

    override fun show() {
        audioService.play(MusicAsset.MENU)

        uiStage.clear()
        uiStage.actors { controlsView(i18n) }
    }

    override fun resize(width: Int, height: Int) {
        uiStage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        val dt = delta.coerceAtMost(0.25f)

        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
            game.setScreen<GameScreen>()
        }

        // render UI
        uiStage.viewport.apply()
        uiStage.act(dt)
        uiStage.draw()
    }
}