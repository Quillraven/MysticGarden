package com.github.quillraven.mysticgarden.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.I18NBundle
import com.github.quillraven.mysticgarden.MusicAsset
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.audio.AudioService
import com.github.quillraven.mysticgarden.ui.model.MenuModel
import com.github.quillraven.mysticgarden.ui.view.menuView
import ktx.app.KtxScreen
import ktx.scene2d.actors

class MenuScreen(
    private val game: MysticGarden,
    private val uiStage: Stage,
    private val audioService: AudioService,
    private val i18n: I18NBundle,
    private val prefs: Preferences,
) : KtxScreen {

    override fun show() {
        audioService.play(MusicAsset.MENU)
        uiStage.clear()
        uiStage.actors {
            val model = MenuModel(game, audioService)
            menuView(model, i18n)
            // modify model afterwards to trigger menuView data bindings
            model.hasSaveState = MysticGarden.prefKeyPlayerPos in prefs
            model.volume = audioService.mscVolume
        }

        Gdx.input.inputProcessor = uiStage
    }

    override fun resize(width: Int, height: Int) {
        uiStage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        val dt = delta.coerceAtMost(0.25f)

        // render UI
        uiStage.viewport.apply()
        uiStage.act(dt)
        uiStage.draw()
    }
}